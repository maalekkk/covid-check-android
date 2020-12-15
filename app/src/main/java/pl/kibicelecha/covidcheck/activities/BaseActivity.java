package pl.kibicelecha.covidcheck.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import im.delight.android.location.SimpleLocation;

public class BaseActivity extends AppCompatActivity
{
    protected FirebaseAuth auth;
    protected SimpleLocation location;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        auth.useAppLanguage();
        location = new SimpleLocation(this);
        if (!location.hasLocationEnabled())
        {
            SimpleLocation.openSettings(this);
        }
    }

    protected void startActivity(Context context, Class<?> activityClass)
    {
        startActivity(new Intent(context, activityClass));
    }

    protected void startActivityAndFinish(Context context, Class<?> activityClass)
    {
        startActivity(context, activityClass);
        finish();
    }
}