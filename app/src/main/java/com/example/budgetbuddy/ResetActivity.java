package com.example.budgetbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;


public class ResetActivity extends AppCompatActivity {

    private Button btn;
    private EditText email;

    private TextView mSignin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        btn = findViewById(R.id.btn_forgot);
        email = findViewById(R.id.email_Forgot);
        mSignin = findViewById(R.id.forget_login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString();
                FirebaseAuth.getInstance().sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ResetActivity.this, "Email Sent!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ResetActivity.this, "Failed to send request", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }

        });

    }

}
