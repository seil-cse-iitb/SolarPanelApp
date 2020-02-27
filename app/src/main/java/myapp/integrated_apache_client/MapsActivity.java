package myapp.integrated_apache_client;

import com.google.android.gms.maps.model.LatLng;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Button clearMarkers, calculatePotential;
    final ArrayList<LatLng> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        clearMarkers = findViewById(R.id.clearMarkers);
        calculatePotential = findViewById(R.id.calculatePotential);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
        clearMarkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                markers.clear();
            }
        });
        calculatePotential.setOnClickListener(new View.OnClickListener() {
            private static final double EARTH_RADIUS = 6378137;// meters
            private static final String LOG_TAG= "log";// meters

            public  double calculateAreaOfGPSPolygonOnEarthInSquareMeters(final List<LatLng> locations) {
                return calculateAreaOfGPSPolygonOnSphereInSquareMeters(locations, EARTH_RADIUS);
            }

            private  double calculateAreaOfGPSPolygonOnSphereInSquareMeters(final List<LatLng> locations, final double radius) {
                if (locations.size() < 3) {
                    return 0;
                }

                final double diameter = radius * 2;
                final double circumference = diameter * Math.PI;
                final List<Double> listY = new ArrayList<Double>();
                final List<Double> listX = new ArrayList<Double>();
                final List<Double> listArea = new ArrayList<Double>();
                // calculate segment x and y in degrees for each point
                final double latitudeRef = locations.get(0).latitude;
                final double longitudeRef = locations.get(0).longitude;
                for (int i = 1; i < locations.size(); i++) {
                    final double latitude = locations.get(i).latitude;
                    final double longitude = locations.get(i).longitude;
                    listY.add(calculateYSegment(latitudeRef, latitude, circumference));
                    Log.d(LOG_TAG, String.format("Y %s: %s", listY.size() - 1, listY.get(listY.size() - 1)));
                    listX.add(calculateXSegment(longitudeRef, longitude, latitude, circumference));
                    Log.d(LOG_TAG, String.format("X %s: %s", listX.size() - 1, listX.get(listX.size() - 1)));
                }

                // calculate areas for each triangle segment
                for (int i = 1; i < listX.size(); i++) {
                    final double x1 = listX.get(i - 1);
                    final double y1 = listY.get(i - 1);
                    final double x2 = listX.get(i);
                    final double y2 = listY.get(i);
                    listArea.add(calculateAreaInSquareMeters(x1, x2, y1, y2));
                    Log.d(LOG_TAG, String.format("area %s: %s", listArea.size() - 1, listArea.get(listArea.size() - 1)));
                }

                // sum areas of all triangle segments
                double areasSum = 0;
                for (final Double area : listArea) {
                    areasSum = areasSum + area;
                }

                // get abolute value of area, it can't be negative
                return Math.abs(areasSum);// Math.sqrt(areasSum * areasSum);
            }

            private  Double calculateAreaInSquareMeters(final double x1, final double x2, final double y1, final double y2) {
                return (y1 * x2 - x1 * y2) / 2;
            }

            private  double calculateYSegment(final double latitudeRef, final double latitude, final double circumference) {
                return (latitude - latitudeRef) * circumference / 360.0;
            }

            private  double calculateXSegment(final double longitudeRef, final double longitude, final double latitude,
                                                    final double circumference) {
                return (longitude - longitudeRef) * circumference * Math.cos(Math.toRadians(latitude)) / 360.0;
            }




            double CalculatePolygonArea(ArrayList<LatLng> markers) {
                double area = 0, p1_lat, p1_long, p2_lat, p2_long;
                if (markers.size() > 2) {
                    for (int i = 0; i < markers.size() - 1; i++) {
                        p1_lat = markers.get(i).latitude;//Arraylat[i];
                        p1_long = markers.get(i).longitude;//Arraylong[i];
                        p2_lat = markers.get(i + 1).latitude;//Arraylat[i+1];
                        p2_long = markers.get(i + 1).longitude;//Arraylong[i+1];
//                        area += Math.toRadians(p2_long - p1_long) * (2 + Math.sin(Math.toRadians(p1_lat)) + Math.sin(Math.toRadians(p2_lat)));
//                        area += ;
                    }
                    area = area * 6378.137 * 6378.137 / 2;
                }
                return Math.abs(area);
            }

            double ConvertToRadian(double input) {
                return input * Math.PI / 180;
            }

            @Override
            public void onClick(View view) {
                double potential;
//                potential = CalculatePolygonArea(markers) * 5.5;
                final double area=calculateAreaOfGPSPolygonOnEarthInSquareMeters(markers);
                potential =  area* 5.5;
                final double finalPotential = potential;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(MapsActivity.this)
                                .setTitle("Solar Potential")
                                .setMessage("Potential: " + String.format("%.2f", finalPotential)+" kWh/day\nArea: "+String.format("%.2f", area)+" m^2")
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .show();
                    }
                });
            }
        });
        // Add a marker in Sydney and move the camera
        LatLng iitB = new LatLng(19.1334, 72.9133);
        final MarkerOptions position = new MarkerOptions().position(iitB).title("IITB");
        map.addMarker(position);
        map.setMinZoomPreference(14);
        map.moveCamera(CameraUpdateFactory.newLatLng(iitB));
        map.clear();
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.addMarker(new MarkerOptions().position(latLng));
                markers.add(latLng);
            }
        });
    }
}
