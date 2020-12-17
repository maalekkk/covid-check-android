package pl.kibicelecha.covidcheck.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import im.delight.android.location.SimpleLocation.Point;
import pl.kibicelecha.covidcheck.R;
import pl.kibicelecha.covidcheck.model.Place;
import pl.kibicelecha.covidcheck.model.User;

public class MainActivity extends BaseActivity
{
    private DatabaseReference refPlaces;
    private Query refUserPlaces;
    private DatabaseReference refUsers;

    private TextView mEmail;

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
                setCurrentUser();
            }
            else
            {
                startActivityAndFinish(this, LoginActivity.class);
            }
        });

        refPlaces = FirebaseDatabase.getInstance().getReference().child(DB_COLLECTION_PLACE);
        refUserPlaces = refPlaces.orderByChild("userId").equalTo(auth.getCurrentUser().getUid());
        refUserPlaces.keepSynced(true);

        ListView recent_locations = findViewById(R.id.recent_locations_list);
        FirebaseListOptions<Place> options = new FirebaseListOptions.Builder<Place>()
                .setLayout(R.layout.custom_textview)
                .setQuery(refUserPlaces, Place.class)
                .setLifecycleOwner(this)
                .build();
        recent_locations.setAdapter(new FirebaseListAdapter<Place>(options)
        {
            @Override
            protected void populateView(@NonNull View v, @NonNull Place model, int position)
            {
                ((TextView) v.findViewById(android.R.id.text1)).setText(model.toString());
            }
        });
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
                    Point point = location.getPosition();
                    refPlaces.push().setValue(new Place(auth.getCurrentUser().getUid(), point.latitude, point.longitude))
                            .addOnSuccessListener(this, task ->
                                    Toast.makeText(this, "Added location!", Toast.LENGTH_SHORT).show());
                }
                case 1:
                {
                    //TODO MANUAL
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

    private void setCurrentUser()
    {
        refUsers.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                User user = snapshot.getValue(User.class);
                mEmail.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
            }
        });
    }
}