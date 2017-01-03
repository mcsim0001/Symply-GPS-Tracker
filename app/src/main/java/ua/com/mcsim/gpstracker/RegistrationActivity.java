package ua.com.mcsim.gpstracker;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ua.com.mcsim.gpstracker.forms.User;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etPhone;
    private EditText etName;
    private Button btnRegUser;
    private Context context;
    private DatabaseReference mDatabaseReference;
    private String phoneNum;
    private String imeiNum;
    private final String CHILD_USERS = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);

        etPhone = (EditText) findViewById(R.id.et_phone);
        etName = (EditText) findViewById(R.id.et_name);
        etName.setText("Nickname");
        context = this;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(CHILD_USERS);

        //Gettin device phone number and IMEI
        TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        phoneNum = tMgr.getLine1Number();
        etPhone.setText(phoneNum);
        imeiNum = tMgr.getDeviceId();

        btnRegUser = (Button) findViewById(R.id.btn_reg);
        btnRegUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User message = new User(etName.getText().toString(),phoneNum);
                try {
                    mDatabaseReference.child(imeiNum).setValue(message);
                    Toast.makeText(context, "New user registered", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Database Error ", Toast.LENGTH_SHORT).show();
                    Log.d("mLog","Database Error... ");
                }
            }
        });
    }

}
