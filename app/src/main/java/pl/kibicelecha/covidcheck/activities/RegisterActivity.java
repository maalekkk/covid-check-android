package pl.kibicelecha.covidcheck.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import pl.kibicelecha.covidcheck.R;
import pl.kibicelecha.covidcheck.model.User;

public class RegisterActivity extends BaseActivity
{
    private TextView mUsername;
    private TextView mEmail;
    private TextView mPassword;
    private TextView mPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mUsername = ((TextView) findViewById(R.id.username_registration_txt));
        mEmail = ((TextView) findViewById(R.id.email_registration_txt));
        mPassword = ((TextView) findViewById(R.id.password_registration_txt));
        mPasswordConfirm = ((TextView) findViewById(R.id.password2_registration_txt));
    }

    public void signUp(View view)
    {
        String username = mUsername.getText().toString();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String passwordConfirm = mPasswordConfirm.getText().toString();
        if (!validateForm(username, email, password, passwordConfirm))
        {
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(this, task ->
                {
                    FirebaseDatabase.getInstance().getReference(DB_COLLECTION_USERS).child(auth.getUid())
                            .setValue(new User(username, false));
                    auth.getCurrentUser().sendEmailVerification();
                    auth.signOut();
                    Toast.makeText(this, "Email verification was sent!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(this, task ->
                        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show());
    }

    public void backLogin(View view)
    {
        finish();
    }

    private boolean validateForm(String username, String email, String pass, String confirmPass)
    {
        boolean valid = true;

        if (TextUtils.isEmpty(username))
        {
            mUsername.setError("Required.");
            valid = false;
        }
        else
        {
            mUsername.setError(null);
        }

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