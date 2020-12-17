package pl.kibicelecha.covidcheck.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.util.Map;

public class Place implements Serializable
{
    private String userId;
    private Double latitude;
    private Double longitude;
    private Long timestamp;

    public Place()
    {
    }

    public Place(String userId, Double latitude, Double longitude)
    {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public Double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(Double latitude)
    {
        this.latitude = latitude;
    }

    public Double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(Double longitude)
    {
        this.longitude = longitude;
    }

    public Map<String, String> getTimestamp()
    {
        return ServerValue.TIMESTAMP;
    }

    public void setTimestamp(Long timestamp)
    {
        this.timestamp = timestamp;
    }

    @Exclude
    public Long getTimestampLong()
    {
        return timestamp;
    }

    @Override
    public String toString()
    {
        return latitude + ", " + longitude;
    }
}
