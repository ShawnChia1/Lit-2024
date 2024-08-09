package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityReportBinding;
import com.google.android.material.textfield.TextInputEditText;

public class ReportActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityReportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        TextInputEditText textInputEditText = findViewById(R.id.textInputEditText);
        String url = getIntent().getStringExtra("url");
        if (url != null && !url.isEmpty()) {
            textInputEditText.setText(url);
        }
        String[] options = new String[] {"Disinformation", "Racism", "Terroism", "Crime", "Sexual", "Harassment", "Violence", "Self-harm", "Cyberbullying", "Promoting unhealthy lifestlyes", "Encouraging actions that endanger public health"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, options);
        autoCompleteTextView.setAdapter(adapter);

        MaterialButton submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ReportActivity.this, "Report submitted successfully", Toast.LENGTH_SHORT).show();
                if (url != null && !url.isEmpty()) {
                    String[] result = new String[2];
                    result[0] = url;
                    result[1] = autoCompleteTextView.getText().toString();
                    BrowserActivity.urlBlacklist.add(result);
                }
                finish();
            }
        });
        ImageButton closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}