package ua.com.mcsim.gpstracker;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class TrackerActivity extends AppCompatActivity {

    private TextView tvNetStatus, tvGPSstatus, tvCoord;
    private Button btnStart,btnAuth;
    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            if (ActivityCompat.checkSelfPermission(TrackerActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackerActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                 return;
            }
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        else {
            tvCoord.setText(formatLocation(location));
        }
    }

    private boolean isAuthorised() {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirechatUser = mFirebaseAuth.getCurrentUser();
        return mFirechatUser != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        tvNetStatus = (TextView) findViewById(R.id.tv_network_status);
        tvGPSstatus = (TextView) findViewById(R.id.tv_gps_status);
        tvCoord = (TextView) findViewById(R.id.tv_coord);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAuthorised()) {
                    startTracking();
                } else {
                    Toast.makeText(TrackerActivity.this, "You are not authorised! Please Sign In.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TrackerActivity.this, AuthorisationActivity.class);
                    startActivity(intent);

                }
            }
        });
        btnAuth = (Button) findViewById(R.id.btn_auth);
        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrackerActivity.this, AuthorisationActivity.class);
                startActivity(intent);
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    private void startTracking() {
        Intent service = new Intent(this,GPSservice.class);
        if (!isServiceRunning(GPSservice.class)) {
            startService(service);
            btnStart.setText("Stop tracking");
            Toast.makeText(this, "Tracking started :)", Toast.LENGTH_SHORT).show();
        } else {
            stopService(service);
            Toast.makeText(this, "Tracking stoped :(", Toast.LENGTH_SHORT).show();
            btnStart.setText("Start tracking");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);
        checkEnabled();
    }

    private void checkEnabled() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            tvGPSstatus.setTextColor(Color.GREEN);
            tvGPSstatus.setText("ENABLED");
        } else {
            tvGPSstatus.setTextColor(Color.RED);
            tvGPSstatus.setText("DISABLED");
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            tvNetStatus.setTextColor(Color.GREEN);
            tvNetStatus.setText("ENABLED");
        } else {
            tvNetStatus.setTextColor(Color.RED);
            tvNetStatus.setText("DISABLED");
        }
    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
