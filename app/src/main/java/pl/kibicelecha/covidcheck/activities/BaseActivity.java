package pl.kibicelecha.covidcheck.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.yayandroid.locationmanager.base.LocationBaseActivity;
import com.yayandroid.locationmanager.configuration.Configurations;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;

import pl.kibicelecha.covidcheck.R;

public class BaseActivity extends LocationBaseActivity
{
    protected FirebaseAuth auth;
    protected Location location;
    private ConnectivityManager connectivityManager;
    private NetworkCallback networkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = createNetworkCallback();
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

    @Override
    protected void onResume()
    {
        super.onResume();
        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        connectivityManager.unregisterNetworkCallback(networkCallback);
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

    private NetworkCallback createNetworkCallback()
    {
        return new NetworkCallback()
        {
            @Override
            public void onAvailable(@NonNull Network network)
            {
                super.onAvailable(network);
            }

            @Override
            public void onLost(@NonNull Network network)
            {
                super.onLost(network);
                Toast.makeText(getApplicationContext(), R.string.global_err_no_int_conn, Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public LocationConfiguration getLocationConfiguration()
    {
        return Configurations.defaultConfiguration(getString(R.string.global_info_ask_rational), getString(R.string.global_info_ask_gps));
    }

    @Override
    public void onLocationChanged(Location location)
    {
        this.location = location;
    }

    @Override
    public void onLocationFailed(int type)
    {
        System.err.println("Location failed with type: " + type);
    }
}