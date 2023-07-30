package com.example.budgetbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.google.firebase.ktx.Firebase;

public class MainActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPass;
    private Button btnLogin;

    private TextView mForgetPassword;
    private TextView mSignupHere;

    private CheckBox p_box;

    private ProgressDialog mDialog;

    //Firebase...

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        //If already register
        if (mAuth.getCurrentUser() !=null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }

        mDialog = new ProgressDialog(this);

        loginDetails();
    }

    private void loginDetails(){

        mEmail=findViewById(R.id.email_login);
        mPass=findViewById(R.id.password_login);
        btnLogin=findViewById(R.id.btn_login);
        mForgetPassword=findViewById(R.id.forget_password);
        mSignupHere=findViewById(R.id.signup_reg);
        p_box = findViewById(R.id.show_pass);



        p_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        btnLogin.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String email=mEmail.getText().toString().trim();
                String pass=mPass.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email Required..");
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    mPass.setError("Password Required..");
                    return;
                }

                mDialog.setMessage("Processing...");
                mDialog.show();

                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                   if (task.isSuccessful()){

                       mDialog.dismiss();
                       startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                       Toast.makeText(getApplicationContext(),"Login Successfully ",Toast.LENGTH_SHORT).show();
                   } else{
                       mDialog.dismiss();
                       Toast.makeText(getApplicationContext(),"Login Failed ",Toast.LENGTH_SHORT).show();
                   }
                    }
                });

            }
        });

        mSignupHere.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TermsAndConditionActivity.class));
            }

            //reset Activity
        });
        mForgetPassword.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ResetActivity.class));
            }
        });

    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog =new AlertDialog.Builder( MainActivity.this);
        alertDialog.setTitle("Exit App");
        alertDialog.setMessage("Do you want to exit app?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick (DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick (DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }
}