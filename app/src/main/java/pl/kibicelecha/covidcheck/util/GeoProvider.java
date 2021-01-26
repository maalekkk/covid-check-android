package pl.kibicelecha.covidcheck.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import pl.kibicelecha.covidcheck.R;

public class GeoProvider
{
    private final Context context;
    private final Geocoder geocoder;

    public GeoProvider(Context context)
    {
        this.context = context;
        this.geocoder = new Geocoder(context, Locale.getDefault());
    }

    public String getLocationName(double latitude, double longitude)
    {
        final String defaultStr = latitude + ", " + longitude;
        try
        {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && !addressList.isEmpty())
            {
                return addressList.get(0).getAddressLine(0);
            }
        }
        catch (IOException e)
        {
            Toast.makeText(context, R.string.global_err_location_not_readed, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return defaultStr;
    }
}
