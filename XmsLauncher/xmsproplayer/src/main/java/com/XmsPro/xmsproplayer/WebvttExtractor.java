package com.XmsPro.xmsproplayer;

import android.text.TextUtils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.text.webvtt.WebvttParserUtil;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class WebvttExtractor  implements Extractor {

    private static final Pattern LOCAL_TIMESTAMP = Pattern.compile("LOCAL:([^,]+)");
    private static final Pattern MEDIA_TIMESTAMP = Pattern.compile("MPEGTS:(\\d+)");

    private final String language;
    private final TimestampAdjuster timestampAdjuster;
    private final ParsableByteArray sampleDataWrapper;

    private ExtractorOutput output;

    private byte[] sampleData;
    private int sampleSize;

    public WebvttExtractor(String language, TimestampAdjuster timestampAdjuster) {
        this.language = language;
        this.timestampAdjuster = timestampAdjuster;
        this.sampleDataWrapper = new ParsableByteArray();
        sampleData = new byte[1024];
    }

    // Extractor implementation.

    @Override
    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        // This extractor is only used for the HLS use case, which should not call this method.
        throw new IllegalStateException();
    }

    @Override
    public void init(ExtractorOutput output) {
        this.output = output;
        output.seekMap(new SeekMap.Unseekable(C.TIME_UNSET));
    }

    @Override
    public void seek(long position, long timeUs) {
        // This extractor is only used for the HLS use case, which should not call this method.
        throw new IllegalStateException();
    }

    @Override
    public void release() {
        // Do nothing
    }

    @Override
    public int read(ExtractorInput input, PositionHolder seekPosition)
            throws IOException, InterruptedException {
        int currentFileSize = (int) input.getLength();

        // Increase the size of sampleData if necessary.
        if (sampleSize == sampleData.length) {
            sampleData = Arrays.copyOf(sampleData,
                    (currentFileSize != C.LENGTH_UNSET ? currentFileSize : sampleData.length) * 3 / 2);
        }

        // Consume to the input.
        int bytesRead = input.read(sampleData, sampleSize, sampleData.length - sampleSize);
        if (bytesRead != C.RESULT_END_OF_INPUT) {
            sampleSize += bytesRead;
            if (currentFileSize == C.LENGTH_UNSET || sampleSize != currentFileSize) {
                return Extractor.RESULT_CONTINUE;
            }
        }

        // We've reached the end of the input, which corresponds to the end of the current file.
        processSample();
        return Extractor.RESULT_END_OF_INPUT;
    }

    private void processSample() throws ParserException {
        ParsableByteArray webvttData = new ParsableByteArray(sampleData);

        // Validate the first line of the header.
        try {
            WebvttParserUtil.validateWebvttHeaderLine(webvttData);
        } catch (SubtitleDecoderException e) {
            throw new ParserException(e);
        }

        // Defaults to use if the header doesn't contain an X-TIMESTAMP-MAP header.
        long vttTimestampUs = 0;
        long tsTimestampUs = 0;

        // Parse the remainder of the header looking for X-TIMESTAMP-MAP.
        String line;
        while (!TextUtils.isEmpty(line = webvttData.readLine())) {
            if (line.startsWith("X-TIMESTAMP-MAP")) {
                Matcher localTimestampMatcher = LOCAL_TIMESTAMP.matcher(line);
                if (!localTimestampMatcher.find()) {
                    throw new ParserException("X-TIMESTAMP-MAP doesn't contain local timestamp: " + line);
                }
                Matcher mediaTimestampMatcher = MEDIA_TIMESTAMP.matcher(line);
                if (!mediaTimestampMatcher.find()) {
                    throw new ParserException("X-TIMESTAMP-MAP doesn't contain media timestamp: " + line);
                }
                vttTimestampUs = WebvttParserUtil.parseTimestampUs(localTimestampMatcher.group(1));
                tsTimestampUs = TimestampAdjuster.ptsToUs(Long.parseLong(mediaTimestampMatcher.group(1)));
            }
        }

        // Find the first cue header and parse the start time.
        Matcher cueHeaderMatcher = WebvttParserUtil.findNextCueHeader(webvttData);
        if (cueHeaderMatcher == null) {
            // No cues found. Don't output a sample, but still output a corresponding track.
            buildTrackOutput(0);
            return;
        }

        long firstCueTimeUs = WebvttParserUtil.parseTimestampUs(cueHeaderMatcher.group(1));
        long sampleTimeUs = timestampAdjuster.adjustTsTimestamp(
                TimestampAdjuster.usToPts(firstCueTimeUs + tsTimestampUs - vttTimestampUs));
        long subsampleOffsetUs = sampleTimeUs - firstCueTimeUs;
        // Output the track.
        TrackOutput trackOutput = buildTrackOutput(subsampleOffsetUs);
        // Output the sample.
        sampleDataWrapper.reset(sampleData, sampleSize);
        trackOutput.sampleData(sampleDataWrapper, sampleSize);
        trackOutput.sampleMetadata(sampleTimeUs, C.BUFFER_FLAG_KEY_FRAME, sampleSize, 0, null);
    }

    private TrackOutput buildTrackOutput(long subsampleOffsetUs) {
        TrackOutput trackOutput = output.track(0, C.TRACK_TYPE_TEXT);
        trackOutput.format(Format.createTextSampleFormat(null, MimeTypes.TEXT_VTT, null,
                Format.NO_VALUE, 0, language, null, subsampleOffsetUs));
        output.endTracks();
        return trackOutput;
    }

}