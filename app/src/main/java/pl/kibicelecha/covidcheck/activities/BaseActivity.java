package pl.kibicelecha.covidcheck.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity
{
    protected FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        auth.useAppLanguage();
    }

    protected void startActivity(Context context, Class<?> activityClass)
    {
        startActivity(new Intent(context, activityClass));
    }

    protected void startActivityAndFinish(Context context, Class<?> activityClass)
    {
        startActivity(context, activityClass);
        finish();
    }
}