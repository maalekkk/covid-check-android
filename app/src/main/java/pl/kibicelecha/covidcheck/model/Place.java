package pl.kibicelecha.covidcheck.model;

public class Place
{
    private User user;
    private double latitude;
    private double longitude;
    private long timestamp;

    public Place(PlaceSerializable placeSerializable, User user)
    {
        this.latitude = placeSerializable.getLatitude();
        this.longitude = placeSerializable.getLongitude();
        this.timestamp = placeSerializable.getTimestamp();
        this.user = user;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
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
}
