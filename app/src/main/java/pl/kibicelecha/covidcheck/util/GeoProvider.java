package pl.kibicelecha.covidcheck.util;

import android.content.Context;
import android.location.Geocoder;

import java.io.IOException;
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
        String address;
        try
        {
            address = geocoder.getFromLocation(latitude, longitude, 1).get(0).getAddressLine(0);
        }
        catch (IOException e)
        {
            address = latitude + ", " + longitude;
            e.printStackTrace();
        }
        return address;
    }
}
