package com.example.buget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MonthView extends AppCompatActivity {

    ImageButton tottest;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monthac_layout);

        tottest = (ImageButton)findViewById(R.id.month_view);
        tottest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MonthView.this,MainActivity.class));
            }
        });
    }
}
