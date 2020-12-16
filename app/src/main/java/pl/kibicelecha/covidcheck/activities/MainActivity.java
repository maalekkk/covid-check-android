package pl.kibicelecha.covidcheck.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;

import pl.kibicelecha.covidcheck.R;
import pl.kibicelecha.covidcheck.model.Place;
import pl.kibicelecha.covidcheck.model.User;

public class MainActivity extends BaseActivity
{
    private DatabaseReference refPlaces;
    private DatabaseReference refUsers;

    private TextView mEmail;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEmail = ((TextView) findViewById(R.id.email_home_txt));

        refUsers = FirebaseDatabase.getInstance().getReference().child(DB_COLLECTION_USERS);
        auth.addAuthStateListener(firebaseAuth ->
        {
            if (auth.getCurrentUser() != null)
            {
                getCurrentUser(refUsers);
            }
            else
            {
                startActivityAndFinish(this, LoginActivity.class);
            }
        });

        refPlaces = FirebaseDatabase.getInstance().getReference().child(DB_COLLECTION_PLACE);
        refPlaces.keepSynced(true);

        ListView recent_locations = findViewById(R.id.recent_locations_list);
        String[] months = new DateFormatSymbols().getMonths();
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, months);
        recent_locations.setAdapter(monthAdapter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        location.beginUpdates();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        location.endUpdates();
    }

    public void showSaveLocationOptions(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose saving mode");

        String[] options = {"Automatic", "Manual"};
        builder.setItems(options, (dialog, which) ->
        {
            switch (which)
            {
                case 0:
                {
                    refPlaces.push().setValue(new Place(auth.getCurrentUser().getUid(), location.getPosition(), ServerValue.TIMESTAMP))
                            .addOnSuccessListener(this, task ->
                                    Toast.makeText(this, "Added location!", Toast.LENGTH_SHORT).show());
                }
                case 1:
                {

                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void logout(View view)
    {
        auth.signOut();
    }

    private void getCurrentUser(DatabaseReference reference)
    {
        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    if (dataSnapshot.getKey().equals(auth.getCurrentUser().getUid()))
                    {
                        currentUser = dataSnapshot.getValue(User.class);
                        mEmail.setText(currentUser.getUsername());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
            }
        });
    }
}