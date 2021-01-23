package pl.kibicelecha.covidcheck.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.yayandroid.locationmanager.base.LocationBaseActivity;
import com.yayandroid.locationmanager.configuration.Configurations;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;

public class BaseActivity extends LocationBaseActivity
{
    protected static final String DB_COLLECTION_USERS = "users";
    protected static final String DB_COLLECTION_PLACE = "places";
    protected FirebaseAuth auth;
    protected FirebaseDatabase database;
    protected Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        auth.useAppLanguage();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
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

    @Override
    public LocationConfiguration getLocationConfiguration()
    {
        return Configurations.defaultConfiguration("rational", "gps");
    }

    @Override
    public void onLocationChanged(Location location)
    {
        this.location = location;
    }

    @Override
    public void onLocationFailed(int type)
    {

    }
}