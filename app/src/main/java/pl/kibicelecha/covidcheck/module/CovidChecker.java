package pl.kibicelecha.covidcheck.module;

import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;

import com.developer.kalert.KAlertDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.kibicelecha.covidcheck.R;
import pl.kibicelecha.covidcheck.model.Place;
import pl.kibicelecha.covidcheck.model.PlaceSerializable;
import pl.kibicelecha.covidcheck.model.User;
import pl.kibicelecha.covidcheck.util.TimeProvider;

import static pl.kibicelecha.covidcheck.module.Database.DB_COLLECTION_PLACES;
import static pl.kibicelecha.covidcheck.module.Database.DB_COLLECTION_USERS;

public class CovidChecker
{
    private static final long CHECK_MINUTES = 3L;
    private static final double CHECK_DISTANCE = 15.0;
    private final List<User> userList = new ArrayList<>();
    private final List<Place> ownLastPlaces = new ArrayList<>();
    private final List<Place> othersLastPlaces = new ArrayList<>();
    private final User currentUser;

    public CovidChecker(User user)
    {
        this.currentUser = user;
        Database.getRef().addValueEventListener(loadData());
    }

    public void switchInfectionStatus(boolean status)
    {
        currentUser.setInfected(status);
        if (currentUser.isInfected())
        {
            currentUser.setInDanger(false);
        }
        currentUser.setLastUpdate(TimeProvider.nowEpoch());
        Database.getCurrentUserRef().setValue(currentUser);
    }

    private ValueEventListener loadData()
    {
        return new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                userList.clear();
                snapshot.child(DB_COLLECTION_USERS).getChildren().forEach(dataSnapshot ->
                {
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                });

                ownLastPlaces.clear();
                othersLastPlaces.clear();
                snapshot.child(DB_COLLECTION_PLACES).getChildren().forEach(dataSnapshot ->
                {
                    PlaceSerializable placeSerializable = dataSnapshot.getValue(PlaceSerializable.class);
                    User user = new User();
                    user.setId(placeSerializable.getUserId());

                    Place place = new Place(placeSerializable, userList.get(userList.indexOf(user)));

                    LocalDateTime placeTime = TimeProvider.fromEpoch(place.getTimestamp());
                    if (placeTime.isAfter(TimeProvider.now().minusDays(7)))
                    {
                        if (place.getUser().equals(currentUser))
                        {
                            ownLastPlaces.add(place);
                        }
                        else if (place.getUser().isInfected())
                        {
                            othersLastPlaces.add(place);
                        }
                    }
                });

                checkDanger(ownLastPlaces, othersLastPlaces);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                System.err.println(error);
            }
        };
    }

    private void checkDanger(List<Place> ownLastPlaces, List<Place> othersLastPlaces)
    {
        boolean inDanger = isUserInDanger(currentUser, ownLastPlaces, othersLastPlaces);
        if (currentUser.isInDanger() != inDanger)
        {
            currentUser.setInDanger(inDanger);
            Database.getCurrentUserRef().setValue(currentUser);
        }
    }

    private boolean isUserInDanger(User user, List<Place> ownLastPlaces, List<Place> othersLastPlaces)
    {
        if (user.isInfected())
        {
            return false;
        }

        for (Place ownPlace : ownLastPlaces)
        {
            for (Place otherPlace : othersLastPlaces)
            {
                double distance = distanceBetween(ownPlace.getLatitude(), ownPlace.getLongitude(), otherPlace.getLatitude(), otherPlace.getLongitude());
                long minutes = minutesBetween(ownPlace.getTimestamp(), otherPlace.getTimestamp());
                if (distance <= CHECK_DISTANCE && minutes <= CHECK_MINUTES)
                {
                    return true;
                }
            }
        }
        return false;
    }

    private double distanceBetween(double x1, double y1, double x2, double y2)
    {
        Location first = new Location("");
        first.setLatitude(x1);
        first.setLongitude(y1);
        Location second = new Location("");
        second.setLatitude(x2);
        second.setLongitude(y2);
        return first.distanceTo(second);
    }

    private long minutesBetween(long seconds1, long seconds2)
    {
        return TimeUnit.SECONDS.toMinutes(Math.abs(seconds1 - seconds2));
    }
}
