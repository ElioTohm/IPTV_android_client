package xms.com.smarttv.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.XmsPro.xmsproplayer.FTPPlayer;

import xms.com.smarttv.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment {

    FTPPlayer ftPplayer;

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ftPplayer.releasePlayer();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        SurfaceView surfaceView = view.findViewById(R.id.video_door);
        ftPplayer = new FTPPlayer(getActivity(), surfaceView,null);
        ftPplayer.createPlayer();
        ftPplayer.SetSource("rtsp://192.168.10.102:554/user=admin&password=&channel=1&stream=0.sdp");
//        ftPplayer.SetSource("udp://224.1.10.14:1234");
        return view;
    }

}
