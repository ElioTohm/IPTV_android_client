package xms.com.smarttv.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import xms.com.smarttv.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MapFragment extends Fragment {
    private static final int MAP_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 724;
    private static final String ARG_LATITUDE = "lattitude";
    private static final String ARG_LONGITUDE = "longitude";
    private static final String ARG_ZOOM = "zoom";

    private Double latitude;
    private Double longitude;
    private int zoom = 0;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param latitude Parameter 1.
     * @param longitude Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance(Double latitude, Double longitude, int zoom) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LATITUDE, latitude);
        args.putDouble(ARG_LONGITUDE, longitude);
        args.putInt(ARG_ZOOM, zoom);
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.latitude = getArguments().getDouble(ARG_LATITUDE);
            this.longitude = getArguments().getDouble(ARG_LONGITUDE);
            this.zoom = getArguments().getInt(ARG_ZOOM);
        }
        if (ContextCompat.checkSelfPermission(getContext(),
                ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (! ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{ACCESS_FINE_LOCATION, WRITE_EXTERNAL_STORAGE, ACCESS_NETWORK_STATE},
                        MAP_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        MapView map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();

        if (zoom == 0 ) {
            mapController.setZoom(9);
        } else {
            mapController.setZoom(this.zoom);
        }
        GeoPoint startPoint;
        if (this.latitude == null && this.longitude == null) {
            startPoint = new GeoPoint(33.888630, 35.495480);
        } else {
            startPoint = new GeoPoint(this.latitude, this.longitude);
        }

        mapController.setCenter(startPoint);

        return view;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MAP_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
    }

}
