package com.example.budgetbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mEmail;


    private EditText  mPass, cPass;
    private Button btnReg;
    private TextView mSignin, text_login;

    private FirebaseAuth mAuth;

    private ProgressDialog mDialog;

    private CheckBox p_box1, p_box2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth=FirebaseAuth.getInstance();

        mDialog= new ProgressDialog(this);
        registration();
    }

    private void registration(){

        mEmail=findViewById(R.id.email_reg);
        mPass=findViewById(R.id.password_reg);
        cPass=findViewById(R.id.cpassword_reg);
        btnReg=findViewById(R.id.btn_reg);
        mSignin=findViewById(R.id.signin_here);
        text_login = findViewById(R.id.signin_here);
        p_box1 = findViewById(R.id.checkBox);
        p_box2 = findViewById(R.id.checkBox2);


        text_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }

        });


        p_box1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });


        p_box2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    cPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    cPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        btnReg.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String email=mEmail.getText().toString().trim();
                String pass=mPass.getText().toString().trim();
                String confirmPass = cPass.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email Required..");
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    mPass.setError("Password Required..");
                    return;
                }
                if (!pass.equals(confirmPass)) {
                    cPass.setError("Passwords do not match");
                    return;
                }

                mDialog.setMessage("Processing..");

                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    mDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Registration Complete", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                                } else {
                                    mDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

    }
}