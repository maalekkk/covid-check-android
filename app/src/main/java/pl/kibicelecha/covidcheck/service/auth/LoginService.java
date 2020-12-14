package pl.kibicelecha.covidcheck.service.auth;

import pl.kibicelecha.covidcheck.repository.UserRepository;

public class LoginService
{
    //TODO add pw hasher
    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public boolean authenticate(String email, String password)
    {
        //TODO check user in db
        return email.equals(password);
    }
}
