package com.trycatch.wasuradananjith.smartkubura2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trycatch.wasuradananjith.smartkubura2.Model.User;

public class LoginActivity extends AppCompatActivity {

    TextView proceedToSignUp;
    EditText etPhone, etPassword;
    DatabaseReference mDatabase;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etPassword = (EditText)findViewById(R.id.edtPassword);
        etPhone = (EditText)findViewById(R.id.edtTelephone);
        btnLogin = (Button)findViewById(R.id.btn_sign_in);
        proceedToSignUp = (TextView)findViewById(R.id.link_sign_up);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(etPhone.getText().toString(),etPassword.getText().toString());
            }
        });

        proceedToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

    }

    private void signIn(final String phone, final String pwd) {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(phone).exists()){
                    if (!phone.isEmpty()){
                        final User login = dataSnapshot.child(phone).getValue(User.class);
                        if (login.getPassword().equals(pwd)){
                            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                                    R.style.AppTheme_Dark_Dialog);
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("මදක් රැඳෙන්න ...");
                            progressDialog.show();

                            // set current user
                            SharedPreferences pref = getSharedPreferences("loginData", MODE_PRIVATE);
                            final SharedPreferences.Editor editor = pref.edit();
                            editor.putString("phone", phone);
                            editor.putString("email", login.getEmail());
                            editor.putString("user", login.getName());
                            editor.commit();

                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            Toast.makeText(LoginActivity.this,"පිළිගන්නවා "+login.getName()+"!",Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                        }
                                    }, 3000);

                        }
                        else{
                            Toast.makeText(LoginActivity.this,"මුරපදය වැරදිය !",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(LoginActivity.this,"ජංගම දුරකථන අංකය සටහන් කරන්න !",Toast.LENGTH_SHORT).show();

                    }
                }
                else{
                    Toast.makeText(LoginActivity.this,"ඔබ ලියාපදිංචි වී නැත !",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}