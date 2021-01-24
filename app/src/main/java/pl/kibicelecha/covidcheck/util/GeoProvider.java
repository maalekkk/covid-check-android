package pl.kibicelecha.covidcheck.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeoProvider
{
    private final Geocoder geocoder;

    public GeoProvider(Context context)
    {
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
            return defaultStr;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return defaultStr;
        }
    }
}
