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
        mEmail = findViewById(R.id.email_log_txt);
        mPassword = findViewById(R.id.password_log_txt);
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
                .addOnSuccessListener(this, task ->
                {
                    if (auth.getCurrentUser().isEmailVerified())
                    {
                        startActivityAndFinish(LoginActivity.this, MainActivity.class);
                    }
                    else
                    {
                        Toast.makeText(this, R.string.login_err_unverified_email, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, task ->
                        Toast.makeText(this, R.string.login_err_invalid_cred, Toast.LENGTH_SHORT).show());
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

        mEmail.setError(null);
        if (TextUtils.isEmpty(email))
        {
            mEmail.setError(getString(R.string.login_err_required));
            valid = false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            mEmail.setError(getString(R.string.login_err_invalid_email));
            valid = false;
        }

        mPassword.setError(null);
        if (TextUtils.isEmpty(password))
        {
            mPassword.setError(getString(R.string.login_err_required));
            valid = false;
        }

        return valid;
    }
}