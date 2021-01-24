package pl.kibicelecha.covidcheck.model;

public class User
{
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
}

