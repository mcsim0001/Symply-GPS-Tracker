package ua.com.mcsim.gpstracker.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import ua.com.mcsim.gpstracker.forms.GPSmessage;


public class GPSservice extends IntentService {

    private static final String TRACKING = "tracking";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirechatUser;
    private String phoneNumber = "+380673024779";
    private DatabaseReference mSimpleFirechatDatabaseReference;

    public GPSservice() {
        super("GPSservice");
    }

    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            sendGPSmessage(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String provider) {

            if (ActivityCompat.checkSelfPermission(GPSservice.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GPSservice.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            sendGPSmessage(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public void onCreate() {
        Log.d("mLog", "onCreate ..start");

        //GPS stuff
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);

        //Firebase stuff
        mSimpleFirechatDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirechatUser = mFirebaseAuth.getCurrentUser();
        Log.d("mLog", "onCreate ..finish");
        super.onCreate();

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("mLog", "on Handle intent start..");
        try {
            TimeUnit.SECONDS.sleep(120);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("mLog", "on Handle intent finish..");
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("mLog", "on Start command");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("mLog", "on Destroy");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(locationListener);
        Log.d("mLog", "listener removed!");

        super.onDestroy();
    }

    private void sendGPSmessage(Location location){
        GPSmessage message;
        String comment = " ";
        if (mFirechatUser!=null) {
            if (location!=null) {
                message = new GPSmessage(mFirechatUser.getEmail(),
                                                    phoneNumber,
                                                    String.valueOf(location.getLatitude()),
                                                    String.valueOf(location.getLongitude()),
                                                    String.valueOf(location.getTime()),
                                                    comment);
                mSimpleFirechatDatabaseReference.child(TRACKING).push().setValue(message);
                Log.d("mLog","Sended message:\n Email "+ message.getUserMail() +
                                            "\n Tracker name "+ message.getPhoneNumber()+
                                            "\n Latitude "+ message.getCoordLat()+
                                            "\n Longitude "+ message.getCoordLong()+
                                            "\n Time "+message.getCoordTime()+
                                            "\n Comment "+ message.getComment());
            } else { Log.d("mLog","Location Error...");}

        } else {Log.d("mLog","FirebaseUser = null");}
    }
}
