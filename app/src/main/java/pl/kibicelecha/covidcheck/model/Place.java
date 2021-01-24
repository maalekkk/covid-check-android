package pl.kibicelecha.covidcheck.model;

public class Place
{
    private String userId;
    private double latitude;
    private double longitude;
    private long timestamp;

    public Place()
    {
    }

    public Place(String userId, double latitude, double longitude, long timestamp)
    {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    @Override
    public String toString()
    {
        return latitude + ", " + longitude;
    }
}
