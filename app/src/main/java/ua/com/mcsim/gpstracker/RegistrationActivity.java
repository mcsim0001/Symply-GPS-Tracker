package ua.com.mcsim.gpstracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

import static ua.com.mcsim.gpstracker.BaseActivity.CHILD_USERS;
import static ua.com.mcsim.gpstracker.BaseActivity.PREF_ID;
import static ua.com.mcsim.gpstracker.BaseActivity.PREF_NAME;
import static ua.com.mcsim.gpstracker.BaseActivity.PREF_PHONE;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etPhone;
    private EditText etName;
    private Button btnRegUser;
    private Context context;
    private DatabaseReference mDatabaseReference;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        etPhone = (EditText) findViewById(R.id.et_phone);
        etPhone.setText(pref.getString(PREF_PHONE,"+3123456789"));
        etName = (EditText) findViewById(R.id.et_name);
        etName.setText(pref.getString(PREF_NAME,"Nickname"));
        context = this;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(CHILD_USERS);

        btnRegUser = (Button) findViewById(R.id.btn_reg);
        btnRegUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User newUser = new User(etName.getText().toString(), pref.getString(PREF_PHONE,""));
                try {
                    mDatabaseReference.child(pref.getString(PREF_ID,"")).setValue(newUser);
                    SharedPreferences.Editor ed = pref.edit();
                    ed.putString(PREF_NAME, etName.getText().toString());
                    ed.commit();
                    setResult(RESULT_OK);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Database Error ", Toast.LENGTH_SHORT).show();
                    Log.d("mLog", "Database Error... ");
                    setResult(RESULT_CANCELED);
                }
                finish();
            }
        });
    }

}
