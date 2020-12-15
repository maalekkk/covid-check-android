package pl.kibicelecha.covidcheck.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import pl.kibicelecha.covidcheck.R;

public class ResetPwActivity extends BaseActivity
{
    private TextView mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pw);
        mEmail = ((TextView) findViewById(R.id.email_reset_txt));
    }

    public void resetPassword(View view)
    {
        String email = mEmail.getText().toString();
        if (!validateForm(email))
        {
            return;
        }

        auth.sendPasswordResetEmail(email).addOnCompleteListener(this, task ->
        {
            if (!task.isSuccessful())
            {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Email with reset password was sent!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    public void backLogin(View view)
    {
        finish();
    }

    private boolean validateForm(String email)
    {
        if (TextUtils.isEmpty(email))
        {
            mEmail.setError("Required.");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            mEmail.setError("Invalid email.");
            return false;
        }
        mEmail.setError(null);
        return true;
    }
}