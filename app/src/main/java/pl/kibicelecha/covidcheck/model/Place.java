package pl.kibicelecha.covidcheck.model;

import java.util.Map;

import im.delight.android.location.SimpleLocation.Point;

public class Place
{
    private String userId;
    private Point point;
    private Map<String, String> timestamp;

    public Place()
    {
    }

    public Place(String userId, Point point, Map<String, String> timestamp)
    {
        this.userId = userId;
        this.point = point;
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

    public Point getPoint()
    {
        return point;
    }

    public void setPoint(Point point)
    {
        this.point = point;
    }

    public Map<String, String> getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Map<String, String> timestamp)
    {
        this.timestamp = timestamp;
    }
}
