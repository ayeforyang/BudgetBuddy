package com.example.budgetbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

public class TermsAndConditionActivity extends AppCompatActivity {

    private CheckBox agreeCheckBox;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);

        agreeCheckBox = findViewById(R.id.checkbox_agree);
        registerButton = findViewById(R.id.button_register);

        registerButton.setEnabled(false); // Disable the button initially

        agreeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                registerButton.setEnabled(isChecked); // Enable the button when the CheckBox is checked
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the registration activity or process
                Intent intent = new Intent(TermsAndConditionActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish(); // Optional: Finish the Terms and Conditions activity so the user can't go back to it without re-accepting the terms
            }
        });
    }
}
