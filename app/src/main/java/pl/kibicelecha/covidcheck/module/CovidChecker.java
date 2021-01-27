package pl.kibicelecha.covidcheck.module;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
    private static final long CHECK_MINUTES = 3L;
    private static final double CHECK_DISTANCE = 15.0;
    private final Context context;
    private final List<User> userList = new ArrayList<>();
    private final List<Place> placeList = new ArrayList<>();
    private User currentUser;

    public CovidChecker(Context context, User currentUser)
    {
        this.context = context;
        this.currentUser = currentUser;
        Database.getRef().addValueEventListener(loadData());
    }

    public void switchInfectionStatus(boolean status)
    {
        currentUser.setInfected(status);
        currentUser.setLastUpdate(TimeProvider.nowEpoch());
        Database.getUserRef(currentUser.getId()).setValue(currentUser);
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
                        .filter(user -> user.getId().equals(currentUser.getId()))
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
        if (currentUser == null || currentUser.isInfected())
        {
            return false;
        }

        List<Place> ownLastPlaces = new ArrayList<>();
        List<Place> othersLastPlaces = new ArrayList<>();
        for (Place place : placeList)
        {
            if (place.getUser().getId().equals(currentUser.getId()))
            {
                ownLastPlaces.add(place);
            }
            else if (place.getUser().isInfected())
            {
                othersLastPlaces.add(place);
            }
        }

        for (Place ownPlace : ownLastPlaces)
        {
            for (Place otherPlace : othersLastPlaces)
            {
                double distance = distanceBetween(ownPlace.getLatitude(), ownPlace.getLongitude(), otherPlace.getLatitude(), otherPlace.getLongitude());
                long minutes = minutesBetween(ownPlace.getTimestamp(), otherPlace.getTimestamp());
                if (distance <= CHECK_DISTANCE && minutes < CHECK_MINUTES)
                {
                    Toast.makeText(context, "ZAGROŻONY", Toast.LENGTH_LONG).show();
                    return true;
                }
            }
        }
        return false;
    }

    private double distanceBetween(double x1, double y1, double x2, double y2)
    {
        Location first = new Location("first");
        first.setLatitude(x1);
        first.setLongitude(y1);
        Location second = new Location("second");
        second.setLatitude(x2);
        second.setLongitude(y2);
        return first.distanceTo(second);
    }

    private long minutesBetween(long seconds1, long seconds2)
    {
        return TimeUnit.SECONDS.toMinutes(Math.abs(seconds1 - seconds2));
    }
}
