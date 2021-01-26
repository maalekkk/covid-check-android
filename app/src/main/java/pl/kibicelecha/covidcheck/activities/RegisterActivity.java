package pl.kibicelecha.covidcheck.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.UserProfileChangeRequest;

import pl.kibicelecha.covidcheck.R;
import pl.kibicelecha.covidcheck.model.User;
import pl.kibicelecha.covidcheck.util.TimeProvider;

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
        mUsername = findViewById(R.id.username_registration_txt);
        mEmail = findViewById(R.id.email_registration_txt);
        mPassword = findViewById(R.id.password_registration_txt);
        mPasswordConfirm = findViewById(R.id.password2_registration_txt);
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
                    UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build();
                    auth.getCurrentUser().updateProfile(request);

                    User registeredUser = new User(username, false, TimeProvider.nowEpoch());
                    registeredUser.setId(auth.getUid());
                    database.getReference(DB_COLLECTION_USERS).child(auth.getUid()).setValue(registeredUser);
                    //TODO auth.getCurrentUser().sendEmailVerification();
                    Toast.makeText(this, R.string.register_info_email_verification, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(this, task ->
                        Toast.makeText(this, R.string.global_err_sth_wrong, Toast.LENGTH_SHORT).show());
    }

    public void backLogin(View view)
    {
        finish();
    }

    private boolean validateForm(String username, String email, String pass, String confirmPass)
    {
        boolean valid = true;

        mUsername.setError(null);
        if (TextUtils.isEmpty(username))
        {
            mUsername.setError(getString(R.string.login_err_required));
            valid = false;
        }

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
        if (TextUtils.isEmpty(pass))
        {
            mPassword.setError(getString(R.string.login_err_required));
            valid = false;
        }

        mPasswordConfirm.setError(null);
        if (!TextUtils.equals(pass, confirmPass))
        {
            mPasswordConfirm.setError(getString(R.string.register_err_password_same));
            valid = false;
        }

        return valid;
    }
}