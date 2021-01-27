package pl.kibicelecha.covidcheck.module;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.developer.kalert.KAlertDialog;
import com.google.firebase.database.ChildEventListener;
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
    private final Context context;
    private final List<User> userList = new ArrayList<>();
    private final List<Place> ownLastPlaces = new ArrayList<>();
    private final List<Place> othersLastPlaces = new ArrayList<>();
    private final User currentUser;

    public CovidChecker(Context context, User currentUser)
    {
        this.context = context;
        this.currentUser = currentUser;
        Database.getRef().addValueEventListener(loadData());
        Database.getUsersRef().addValueEventListener(checkDangerValueListener());
        Database.getPlacesRef().addChildEventListener(checkDangerChildListener());
    }

    public void switchInfectionStatus(boolean status)
    {
        currentUser.setInfected(status);
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
                        else
                        {
                            othersLastPlaces.add(place);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                System.err.println(error);
            }
        };
    }

    private ValueEventListener checkDangerValueListener()
    {
        return new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (isUserInDanger())
                {
                    dangerDialog().show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                System.err.println(error);
            }
        };
    }

    private ChildEventListener checkDangerChildListener()
    {
        return new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                if (isUserInDanger())
                {
                    dangerDialog().show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)
            {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        };
    }

    private KAlertDialog dangerDialog()
    {
        KAlertDialog dangerDialog = new KAlertDialog(context, KAlertDialog.WARNING_TYPE);
        dangerDialog.setTitleText("Zagrożenie")
                .setContentText("Wykryliśmy Twój prawdopodobny kontakt z osobą zarażoną koronawirusem!")
                .showCancelButton(false)
                .setConfirmText(context.getString(R.string.global_txt_yes))
                .confirmButtonColor(R.color.success_stroke_color)
                .show();
        dangerDialog.setCanceledOnTouchOutside(true);
        return dangerDialog;
    }


    private boolean isUserInDanger()
    {
        if (currentUser.isInfected())
        {
            return false;
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
