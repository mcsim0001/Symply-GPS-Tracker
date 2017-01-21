package ua.com.mcsim.gpstracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaseActivity extends AppCompatActivity {

    public static final String CHILD_PERMISSIONS = "permission";
    public static final String CHILD_USERS = "users";
    public static final String CHILD_DATE = "date";
    public static final String CHILD_FROM = "from";
    public static final String CHILD_STATUS = "status";
    public static final String CHILD_PHONE = "phone";
    public static final String PREF_ID = "my_id";
    public static final String PREF_PHONE = "my_phone";
    public static final String PREF_NAME = "my_name";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("mLog","BaseActivity: onCreate");
        makeGeneralPreference();
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("mLog", "onAuthStateChanged:signed_in:" + user.getUid());


                } else {
                    // User is signed out
                    Toast.makeText(BaseActivity.this, "You are not authorised! Please Sign In.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BaseActivity.this, AuthorisationActivity.class);
                    startActivity(intent);
                }
            }
        };

        makeSQLiteDatabase();
    }

    private void makeSQLiteDatabase() {
    }

    private void makeGeneralPreference() {
        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_ID, tMgr.getDeviceId());
        editor.putString(PREF_PHONE, tMgr.getLine1Number());
        editor.commit();
        Log.d("mLog", "Preference ID: " + pref.getString(PREF_ID,"") + " Phone: " + pref.getString(PREF_PHONE,""));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("mLog","BaseActivity: onStart");
        mFirebaseAuth.addAuthStateListener(mAuthListener);
        Log.d("mLog", "AuthListener added");
    }



    @Override
    protected void onStop() {
        super.onStop();
        Log.d("mLog","BaseActivity: onStop");
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
            Log.d("mLog", "AuthListener removed");
        }
    }


}