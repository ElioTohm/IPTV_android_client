package xms.com.smarttv.models;

import java.util.ArrayList;
import java.util.List;

import xms.com.smarttv.R;

public final class ServiceAppList {
    public static final String MOVIE_CATEGORY[] = {
            "Services",
    };

    public static List<ServiceApp> list;

    public static List<ServiceApp> setupMovies() {
        list = new ArrayList<ServiceApp>();
        String title[] = {
                "Multimedia",
                "Weather",
                "Room Service",
                "Shoping",
                "Where to go"
        };

        String description = "Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est "
                + "quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit "
                + "amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit "
                + "facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id "
                + "lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.";

        String videoUrl[] = {
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Gmail%20Blue.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Fiber%20to%20the%20Pole.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose.mp4"
        };
        String bgImageUrl[] = {
                "http://www.cityfilm.tv/uploads/tx_extpagesdb/media.jpg",
                "http://images.kuoni.co.uk/73/dubai-33488291-1494255242-ImageGalleryLightboxLarge.jpg",
                "http://amosphere.com/wp-content/uploads/2016/07/top-room-service.jpg",
                "http://gotourasia.info/wp-content/uploads/2016/03/3.jpg",
                "http://arunnath.com/i/2017/04/dubai-night-wallpapers-widescreen.jpg"
        };
        String cardImageUrl[] = {
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search/card.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Gmail%20Blue/card.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Fiber%20to%20the%20Pole/card.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose/card.jpg",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose/card.jpg"
        };

        int[] drawables = {
            R.drawable.tv,
            R.drawable.weather,
            R.drawable.serviceroom,
            R.drawable.shop,
            R.drawable.map
        };

        list.add(buildMovieInfo(0, title[0],
                description, "", videoUrl[0], cardImageUrl[0], bgImageUrl[0], drawables[0]));
        list.add(buildMovieInfo(0, title[1],
                description, "", videoUrl[1], cardImageUrl[1], bgImageUrl[1], drawables[1]));
        list.add(buildMovieInfo(1, title[2],
                description, "", videoUrl[2], cardImageUrl[2], bgImageUrl[2], drawables[2]));
        list.add(buildMovieInfo(1, title[3],
                description, "", videoUrl[3], cardImageUrl[3], bgImageUrl[3], drawables[3]));
        list.add(buildMovieInfo(1, title[4],
                description, "", videoUrl[4], cardImageUrl[4], bgImageUrl[4], drawables[4]));

        return list;
    }

    private static ServiceApp buildMovieInfo(int category, String title,
                                             String description, String studio, String videoUrl, String cardImageUrl,
                                             String bgImageUrl, int drawable) {
        ServiceApp serviceApp = new ServiceApp();
        serviceApp.setId(ServiceApp.getCount());
        ServiceApp.incCount();
        serviceApp.setTitle(title);
        serviceApp.setDescription(description);
        serviceApp.setStudio(studio);
        serviceApp.setCategory(category);
        serviceApp.setCardImageUrl(cardImageUrl);
        serviceApp.setBackgroundImageUrl(bgImageUrl);
        serviceApp.setVideoUrl(videoUrl);
        serviceApp.setSvgimage(drawable);
        return serviceApp;
    }
}
