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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Place place = (Place) o;

        if (Double.compare(place.latitude, latitude) != 0)
        {
            return false;
        }
        if (Double.compare(place.longitude, longitude) != 0)
        {
            return false;
        }
        return timestamp == place.timestamp;
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }
}
