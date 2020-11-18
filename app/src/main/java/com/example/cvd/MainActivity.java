package com.example.cvd;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LauncherActivity;
import android.content.Intent;
import android.icu.text.DateFormatSymbols;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.text.DateFormat;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ListView recent_locations;
    String[] months;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        recent_locations = findViewById(R.id.recent_locations_list);
//        months = new DateFormatSymbols().getMonths();
//        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, R.layout.custom_textview, months);
//        recent_locations.setAdapter(monthAdapter);
    }
}