package pl.kibicelecha.covidcheck.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

import im.delight.android.location.SimpleLocation;

public class BaseActivity extends AppCompatActivity
{
    protected static final String DB_COLLECTION_USERS = "users";
    protected static final String DB_COLLECTION_PLACE = "places";
    private static final int TAG_CODE_PERMISSION_LOCATION = 1;
    protected FirebaseAuth auth;
    protected SimpleLocation location;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        auth.useAppLanguage();
        location = new SimpleLocation(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if (!location.hasLocationEnabled())
            {
                SimpleLocation.openSettings(this);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    TAG_CODE_PERMISSION_LOCATION);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (auth.getCurrentUser() != null)
        {
            auth.getCurrentUser().reload();
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