package pl.kibicelecha.covidcheck.service;

import pl.kibicelecha.covidcheck.model.User;

public class UserService
{
    private User currentUser;

    public UserService()
    {
    }

    public UserService(User currentUser)
    {
        this.currentUser = currentUser;
    }

    public User getCurrentUser()
    {
        return currentUser;
    }

    public void setCurrentUser(User currentUser)
    {
        this.currentUser = currentUser;
    }
}
