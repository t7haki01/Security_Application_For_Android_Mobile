package scurity.app.securityapplicationforandroidmobile;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmergencyFragment extends Fragment implements View.OnClickListener {
    private static final String[] CALL_PERMISSIONS = {
            Manifest.permission.CALL_PHONE
    };

    private Button dial;
    Button btnGPS;
    TextView location1;


    public EmergencyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_emergency, container, false);
        final Context context=view.getContext();
//paste
        dial = (Button) view.findViewById(R.id.btn_dial);
        dial.setOnClickListener(this);
        int permissionCallPhone = ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
        if (permissionCallPhone != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    CALL_PERMISSIONS,
                    1

            );
        }

        //

        location1 =  view.findViewById(R.id.location); //Why I can't call this location without the 1.????
        btnGPS =  view.findViewById(R.id.button);

        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context,"Getting data. Wait few seconds, please.", Toast.LENGTH_LONG).show();
                // Acquire a reference to the system Location Manager
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
                LocationListener locationListener = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        // Called when a new location is found by the network location provider.
                        //El metodo de lo que quiero que haga. Cuando cambia la posicion.
                        location1.setText("Latitude: "+location.getLatitude()+"\n"+"Longitude: "+location.getLongitude()+"\n"+"Speed: "+location.getSpeed()+"\n"+"Location accuracy: "+location.getAccuracy()+"\n"+"Altitude: "+location.getAltitude());
                        //Trying to change the text with the gps data, got stuck there. 8:53
                        //Why can't resolve method setText?
                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {}

                    public void onProviderEnabled(String provider) {}

                    public void onProviderDisabled(String provider) {}
                };

// Register the listener with the Location Manager to receive location updates
                int permissionCheck = ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        });

//Checking for permissions
        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck== PackageManager.PERMISSION_DENIED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        return view;
    }




    //paste the rest

    public void onDialButton (View v){
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:+123"));
        startActivity(intent);
        //verifyPermissions();
    }
}
