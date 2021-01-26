package pl.kibicelecha.covidcheck.model;

import java.util.Objects;

public class User
{
    private String id;
    private String username;
    private boolean infected;
    private long lastUpdate;

    public User()
    {
    }

    public User(String username, boolean infected, long lastUpdate)
    {
        this.username = username;
        this.infected = infected;
        this.lastUpdate = lastUpdate;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public boolean isInfected()
    {
        return infected;
    }

    public void setInfected(boolean infected)
    {
        this.infected = infected;
    }

    public long getLastUpdate()
    {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate)
    {
        this.lastUpdate = lastUpdate;
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

        User user = (User) o;

        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }
}

