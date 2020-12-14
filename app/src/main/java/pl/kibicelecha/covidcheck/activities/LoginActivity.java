package pl.kibicelecha.covidcheck.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import pl.kibicelecha.covidcheck.R;
import pl.kibicelecha.covidcheck.repository.UserRepository;
import pl.kibicelecha.covidcheck.service.auth.LoginService;

public class LoginActivity extends AppCompatActivity
{
    private LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginService = new LoginService(new UserRepository());
    }

    public void signIn(View view)
    {
        String email = ((TextView) findViewById(R.id.email_log_txt)).getText().toString();
        String password = ((TextView) findViewById(R.id.password_log_txt)).getText().toString();

        boolean isAuthed = loginService.authenticate(email, password);
        if (!isAuthed)
        {
            Toast.makeText(this, R.string.login_err_invalid_cred, Toast.LENGTH_LONG).show();
            return;
        }

        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.putExtra("email", email);
        startActivity(homeIntent);
        finish();
    }

    public void startRegister(View view)
    {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}