package ua.com.mcsim.gpstracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
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
import com.google.firebase.database.FirebaseDatabase;


public class FirebaseHelper extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth fAuth;
    private FirebaseDatabase fDatabase;
    private FirebaseUser fUser;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public FirebaseHelper(Context context,GoogleApiClient mGoogleApiClient) {
        this.context = context;
        this.mGoogleApiClient = mGoogleApiClient;
    }

    private Context context;
    private String userName;

    public FirebaseHelper() {
    }



    public void startAuthListener() {
        Log.d("mLog","FirebaseHelper method startAuthListener started");
        if (mAuthListener != null) {
            fAuth.addAuthStateListener(mAuthListener);
        } else {
            createAuthListener();
            try {
                fAuth.addAuthStateListener(mAuthListener);
            } catch (Exception e) {
                Log.d("mLog","Error! AuthStateListener not added");
                e.printStackTrace();
            }
        }
    }

    public void stopAuthListener() {
        if (mAuthListener != null) {
            fAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void signOut(){
        // Firebase sign out
        fAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Toast.makeText(context, "Signed Out", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("mLog","Google Play Services connection failed.");
        Toast.makeText(context, "Google Play Services connection failed.", Toast.LENGTH_SHORT).show();
    }


    //Full authorisation complex with Google authorization
    private void createAuthListener() {
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (fUser != null) {
                    // User is signed in
                    Log.d("mLog", "AuthStateChanged: signed in: " + fUser.getDisplayName());

                } else {
                    // User is signed out
                    Log.d("mLog","AuthStateChanged: User is signed out");
                    Toast.makeText(context, "You are not authorised! Please Sign In.", Toast.LENGTH_SHORT).show();
                    signIn();
                }
            }
        };
    }

    private void signIn() {
        /*Log.d("mLog","Start signIn method");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(context.getApplicationContext())
                .enableAutoManage(((FragmentActivity) context)*//*FragmentActivity *//*, this *//* OnConnectionFailedListener *//*)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();*/


                Intent authorizeIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                ((FragmentActivity) context).startActivityForResult(authorizeIntent, RC_SIGN_IN);
        Log.d("mLog","Created button");
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
                Toast.makeText(context, "Google signIn error. Try again later.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Authentication sucsess.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public String getUserName() {

        return (fUser!=null? fUser.getDisplayName():"No name");
    }
}
