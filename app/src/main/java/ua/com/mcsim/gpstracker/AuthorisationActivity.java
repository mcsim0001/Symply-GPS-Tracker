package ua.com.mcsim.gpstracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ua.com.mcsim.gpstracker.forms.User;

import static ua.com.mcsim.gpstracker.BaseActivity.CHILD_USERS;
import static ua.com.mcsim.gpstracker.BaseActivity.PREF_ID;
import static ua.com.mcsim.gpstracker.BaseActivity.PREF_NAME;

public class AuthorisationActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
                                                                        View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;
    private static final int RC_REG = 1;
    private TextView tvUsername;
    private ImageView ivUser;
    private Button btnSignOut;
    private SignInButton btnSignIn;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser firebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private boolean result;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorisation);

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        tvUsername = (TextView) findViewById(R.id.tv_username);
        ivUser = (ImageView) findViewById(R.id.iv_user);
        btnSignIn = (SignInButton) findViewById(R.id.btn_signin);
        btnSignIn.setOnClickListener(this);
        btnSignOut = (Button) findViewById(R.id.btn_signout);
        btnSignOut.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /*Activity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mFirebaseAuth = FirebaseAuth.getInstance();
        Log.d("mLog", "Created Authorisation Activity");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);


            } else {
                Log.d("mLog","Google Signin not success");
                Toast.makeText(this, "Google signIn error. Try again later.", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == RC_REG) {
            if (resultCode == RESULT_OK) {
                Log.d("mLog","Registration result: OK");
                Toast.makeText(this, "New user registered", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.d("mLog","Registration result: CANCELED");
                Toast.makeText(this, "Database Error... Try again later.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("mLog", "Start firebaseAuthWithGoogle");
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(AuthorisationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AuthorisationActivity.this, "Authentication sucsess.",
                                    Toast.LENGTH_SHORT).show();
                            Log.d("mLog", "Authentication sucsess");
                            firebaseUser = mFirebaseAuth.getCurrentUser();
                            SharedPreferences.Editor ed = pref.edit();
                            ed.putString(PREF_NAME,firebaseUser.getDisplayName());
                            ed.commit();
                            checkRegistration();
                        }
                    }
                });
    }

    private void checkRegistration() {
        Log.d("mLog", "Start checkRegistration method");
        Log.d("mLog", "Preference ID: " + pref.getString(PREF_ID,""));
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference(CHILD_USERS);
        Log.d("mLog", "Checking for " + pref.getString(PREF_ID,""));
        mDatabaseReference.child(pref.getString(PREF_ID,"")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result = (dataSnapshot.getValue()!=null);
                Log.d("mLog", "Taking snapshot, result: " + result);
                if (result) {
                    Log.d("mLog", "User is already registered.");
                    User user = dataSnapshot.getValue(User.class);
                    Log.d("mLog", "Username: " + user.getUserName() + " Phone: " + user.getPhone());
                    setPrefName(user.getUserName());
                    finish();
                } else {
                    Intent intent = new Intent(AuthorisationActivity.this, RegistrationActivity.class);
                    startActivityForResult(intent,RC_REG);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("mLog", "DatabaseError");
                Toast.makeText(AuthorisationActivity.this, "Connection error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setPrefName(String name) {
        SharedPreferences.Editor ed = pref.edit();
        ed.putString(PREF_NAME,name);
        ed.commit();
    }

    public void signIn(){
        Log.d("mLog", "Start signIn method");
        Intent authorizeIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(authorizeIntent, RC_SIGN_IN);

    }

    public void signOut(){
        // Firebase sign out
        mFirebaseAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Toast.makeText(AuthorisationActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                    }
                });
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_signin:{
                signIn();

                break;
            }
            case R.id.btn_signout:{
                signOut();
                break;
            }
        }
    }

}
