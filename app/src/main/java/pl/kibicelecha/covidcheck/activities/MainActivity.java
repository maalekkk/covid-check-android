package pl.kibicelecha.covidcheck.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import pl.kibicelecha.covidcheck.R;

public class MainActivity extends BaseActivity
{
    ListView recent_locations;
    String[] months;
    private TextView mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEmail = ((TextView) findViewById(R.id.email_home_txt));
        mEmail.setText(auth.getCurrentUser().getEmail());

//        recent_locations = findViewById(R.id.recent_locations_list);
//        months = new DateFormatSymbols().getMonths();
//        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, R.layout.custom_textview, months);
//        recent_locations.setAdapter(monthAdapter);
    }

    public void logout(View view)
    {
        auth.signOut();
        startActivityAndFinish(this, LoginActivity.class);
    }
}