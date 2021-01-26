package pl.kibicelecha.covidcheck.model;

public class PlaceSerializable
{
    private String userId;
    private double latitude;
    private double longitude;
    private long timestamp;

    public PlaceSerializable()
    {
    }

    public PlaceSerializable(String userId, double latitude, double longitude, long timestamp)
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

        PlaceSerializable that = (PlaceSerializable) o;

        if (Double.compare(that.latitude, latitude) != 0)
        {
            return false;
        }
        if (Double.compare(that.longitude, longitude) != 0)
        {
            return false;
        }
        if (timestamp != that.timestamp)
        {
            return false;
        }
        return userId.equals(that.userId);
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

    @Override
    public String toString()
    {
        return latitude + ", " + longitude;
    }
}
