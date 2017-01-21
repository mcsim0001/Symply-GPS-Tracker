package ua.com.mcsim.gpstracker;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashSet;
import java.util.Set;
import ua.com.mcsim.gpstracker.forms.TrackingPermission;

public class PermissionActivity extends BaseActivity implements View.OnClickListener {


    EditText etTargetPhone;
    Button btnSendPermission;
    private DatabaseReference mDatabaseReference;
    private Set<String> targetSet;
    private String number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_permission);

        etTargetPhone = (EditText) findViewById(R.id.et_target_phone);
        btnSendPermission = (Button) findViewById(R.id.btn_send_permission);
        btnSendPermission.setOnClickListener(this);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        targetSet = new HashSet<>();
    }

    @Override
    public void onClick(View view) {
        Log.d("mLog", "onClick start ");
        if (view.getId() == R.id.btn_send_permission) {
            number = etTargetPhone.getText().toString();
            ValueEventListener mDatabaseListener = new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("mLog", "onDataChange start ");
                    targetSet.clear();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            // do something with the individual "child"
                            if (child.getKey() != null) {
                                Log.d("mLog", "child.getKey: " + child.getKey());
                                targetSet.add(child.getKey());
                                Log.d("mLog", "targetSet: " + targetSet.toString());
                            }
                        }
                    }
                    Log.d("mLog", "Create permission for number: " + number);
                    createNewPermission();
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("mLog", "Database error.");
                }
            };
            mDatabaseReference.child(CHILD_USERS).orderByChild(CHILD_PHONE).equalTo(number).addListenerForSingleValueEvent(mDatabaseListener);

        }
        Log.d("mLog", "onClick finish ");
    }

    public void createNewPermission() {

        String targetID = getTargetID(targetSet);
        if (targetID != null) {
            TrackingPermission newPermission = new TrackingPermission(getSpectator(), targetID, RESULT_CANCELED);
            try {
                mDatabaseReference.child(CHILD_PERMISSIONS).push().setValue(newPermission);
                Toast.makeText(this, "Permission created. Wait request from target.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Permission error.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User with number " + number + " not found. Please, check phone number and try again.", Toast.LENGTH_LONG).show();
        }
        Log.d("mLog", "createNewPermission finish ");
    }

    private String getTargetID(Set<String> target) {
        Log.d("mLog", "getTargetID start ");

        String id = null;
        if (!target.isEmpty()) {
            for (String targetID : target) {
                id = targetID;
                Log.d("mLog", "targetID: " + targetID);
            }
        }
        Log.d("mLog", "Final ID: " + id);

        return id;
    }

    private String getSpectator() {
        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return tMgr.getDeviceId();
    }

}
