package pl.kibicelecha.covidcheck.model;

public class User
{
    String username;
    boolean infected;

    public User()
    {
    }

    public User(String username, boolean infected)
    {
        this.username = username;
        this.infected = infected;
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
}
