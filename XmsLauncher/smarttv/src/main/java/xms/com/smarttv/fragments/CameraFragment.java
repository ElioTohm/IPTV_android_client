package xms.com.smarttv.fragments;


import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import xms.com.smarttv.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment {

    VideoView videoView = null;

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (videoView != null) {
            videoView.stopPlayback();
            videoView = null;
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        videoView = view.findViewById(R.id.video_door);
        videoView.setVideoURI(Uri.parse("rtsp://192.168.10.102:554/user=admin&password=&channel=1&stream=0.sdp"));
        videoView.start();
        return view;
    }

}
