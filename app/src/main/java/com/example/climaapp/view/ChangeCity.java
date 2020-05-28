package com.example.climaapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.climaapp.R;

public class ChangeCity extends AppCompatActivity {

    private EditText inputCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_city_layout);

        ImageButton backButton = findViewById(R.id.backButton);
        inputCity = findViewById(R.id.queryET);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();
                if (inputCity.getText().toString().isEmpty()){
                    Toast.makeText(ChangeCity.this, "Please type a city name", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED,replyIntent);
                }else {
                    String city = inputCity.getText().toString().trim();
                    replyIntent.putExtra("city",city);
                    setResult(RESULT_OK,replyIntent);
                }
                finish();
            }
        });
    }
}
