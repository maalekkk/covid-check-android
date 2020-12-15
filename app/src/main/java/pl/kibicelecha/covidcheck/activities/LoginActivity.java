package pl.kibicelecha.covidcheck.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import pl.kibicelecha.covidcheck.R;

public class LoginActivity extends BaseActivity
{
    private TextView mEmail;
    private TextView mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmail = ((TextView) findViewById(R.id.email_log_txt));
        mPassword = ((TextView) findViewById(R.id.password_log_txt));
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (auth.getCurrentUser() != null && auth.getCurrentUser().isEmailVerified())
        {
            startActivityAndFinish(this, MainActivity.class);
        }
    }

    public void signIn(View view)
    {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        if (!validateForm(email, password))
        {
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task ->
                {
                    if (!task.isSuccessful())
                    {
                        Toast.makeText(this, R.string.login_err_invalid_cred, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    auth.getCurrentUser().reload();
                    if (auth.getCurrentUser().isEmailVerified())
                    {
                        startActivityAndFinish(LoginActivity.this, MainActivity.class);
                    }
                    else
                    {
                        auth.signOut();
                        Toast.makeText(this, "Email is not verified!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void startRegister(View view)
    {
        startActivity(this, RegisterActivity.class);
    }

    public void startResetPw(View view)
    {
        startActivity(this, ResetPwActivity.class);
    }

    private boolean validateForm(String email, String password)
    {
        boolean valid = true;

        if (TextUtils.isEmpty(email))
        {
            mEmail.setError("Required.");
            valid = false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            mEmail.setError("Invalid email.");
            valid = false;
        }
        else
        {
            mEmail.setError(null);
        }

        if (TextUtils.isEmpty(password))
        {
            mPassword.setError("Required.");
            valid = false;
        }
        else
        {
            mPassword.setError(null);
        }

        return valid;
    }
}