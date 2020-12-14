package pl.kibicelecha.covidcheck.activities;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import pl.kibicelecha.covidcheck.R;

public class MainActivity extends AppCompatActivity
{
    ListView recent_locations;
    String[] months;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        recent_locations = findViewById(R.id.recent_locations_list);
//        months = new DateFormatSymbols().getMonths();
//        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, R.layout.custom_textview, months);
//        recent_locations.setAdapter(monthAdapter);
    }
}