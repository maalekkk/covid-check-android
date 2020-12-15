package pl.kibicelecha.covidcheck.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import pl.kibicelecha.covidcheck.R;

public class RegisterActivity extends BaseActivity
{
    private TextView mEmail;
    private TextView mPassword;
    private TextView mPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mEmail = ((TextView) findViewById(R.id.email_registration_txt));
        mPassword = ((TextView) findViewById(R.id.password_registration_txt));
        mPasswordConfirm = ((TextView) findViewById(R.id.password2_registration_txt));
    }

    public void signUp(View view)
    {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String passwordConfirm = mPasswordConfirm.getText().toString();
        if (!validateForm(email, password, passwordConfirm))
        {
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task ->
                {
                    if (!task.isSuccessful())
                    {
                        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    auth.getCurrentUser().sendEmailVerification();
                    auth.signOut();
                    Toast.makeText(this, "Email verification was sent!", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    public void backLogin(View view)
    {
        finish();
    }

    private boolean validateForm(String email, String pass, String confirmPass)
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

        if (TextUtils.isEmpty(pass))
        {
            mPassword.setError("Required.");
            valid = false;
        }
        else
        {
            mPassword.setError(null);
        }

        if (!TextUtils.equals(pass, confirmPass))
        {
            mPasswordConfirm.setError("Password must be the same.");
            valid = false;
        }
        else
        {
            mPasswordConfirm.setError(null);
        }

        return valid;
    }
}