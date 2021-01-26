package pl.kibicelecha.covidcheck.module;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.kibicelecha.covidcheck.model.Place;
import pl.kibicelecha.covidcheck.model.PlaceSerializable;
import pl.kibicelecha.covidcheck.model.User;
import pl.kibicelecha.covidcheck.util.TimeProvider;

public class CovidChecker
{
    private final Context context;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final DatabaseReference refCurrentUser;
    private final List<User> userList = new ArrayList<>();
    private final List<Place> placeList = new ArrayList<>();
    private User currentUser;

    public CovidChecker(Context context, FirebaseUser user, DatabaseReference refUsers)
    {
        this.context = context;
        this.refCurrentUser = refUsers.child(user.getUid());
        FirebaseDatabase.getInstance().getReference().addValueEventListener(loadData());
    }

    public void switchInfectionStatus(boolean status)
    {
        if (currentUser != null)
        {
            currentUser.setInfected(status);
            currentUser.setLastUpdate(TimeProvider.nowEpoch());
            refCurrentUser.setValue(currentUser);
        }
    }

    public void checkInfectionStatus(SwitchCompat infectionSwitch)
    {
        refCurrentUser.addListenerForSingleValueEvent(checkUser(infectionSwitch));
    }

    private ValueEventListener checkUser(SwitchCompat infectionSwitch)
    {
        return new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                infectionSwitch.setChecked(snapshot.getValue(User.class).isInfected());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                System.err.println(error);
            }
        };
    }

    private ValueEventListener loadData()
    {
        return new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                userList.clear();
                snapshot.child("users").getChildren().forEach(dataSnapshot ->
                {
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                });

                userList.stream()
                        .filter(user -> user.getId().equals(auth.getUid()))
                        .findFirst()
                        .ifPresent(user -> currentUser = user);

                placeList.clear();
                snapshot.child("places").getChildren().forEach(dataSnapshot ->
                {
                    PlaceSerializable placeSerializable = dataSnapshot.getValue(PlaceSerializable.class);
                    Place place = new Place(placeSerializable, userList.stream()
                            .filter(user -> user.getId().equals(placeSerializable.getUserId()))
                            .findFirst()
                            .orElseThrow(IllegalStateException::new));

                    LocalDateTime placeTime = TimeProvider.fromEpoch(place.getTimestamp());
                    if (placeTime.isAfter(TimeProvider.now().minusDays(7)))
                    {
                        placeList.add(place);
                    }
                });

                checkCovid();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                System.err.println(error);
            }
        };
    }

    private boolean checkCovid()
    {
        if (currentUser != null && currentUser.isInfected())
        {
            return false;
        }

        List<Place> ownLastPlaces = new ArrayList<>();
        List<Place> othersLastPlaces = new ArrayList<>();
        for (Place place : placeList)
        {
            if (place.getUser().getId().equals(auth.getUid()))
            {
                ownLastPlaces.add(place);
            }
            else if (place.getUser().isInfected())
            {
                othersLastPlaces.add(place);
            }
        }

        float[] distance = new float[3];
        for (Place ownPlace : ownLastPlaces)
        {
            for (Place otherPlace : othersLastPlaces)
            {
                Location.distanceBetween(ownPlace.getLatitude(), ownPlace.getLongitude(),
                        otherPlace.getLatitude(), otherPlace.getLongitude(), distance);
                if (distance[0] <= 15.0f && TimeUnit.SECONDS.toMinutes(Math.abs(ownPlace.getTimestamp() - otherPlace.getTimestamp())) < 3)
                {
                    Toast.makeText(context, "ZAGROŻONY", Toast.LENGTH_LONG).show();
                    return true;
                }
            }
        }
        //Toast.makeText(context, "NIE ZAGROŻONY", Toast.LENGTH_LONG).show();
        return false;
    }
}
