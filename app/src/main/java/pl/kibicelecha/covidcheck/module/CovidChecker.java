package pl.kibicelecha.covidcheck.module;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pl.kibicelecha.covidcheck.model.Place;
import pl.kibicelecha.covidcheck.model.PlaceSerializable;
import pl.kibicelecha.covidcheck.model.User;
import pl.kibicelecha.covidcheck.util.TimeProvider;

public class CovidChecker
{
    private final DatabaseReference refPlaces;
    private final DatabaseReference refUsers;
    private final DatabaseReference refCurrentUser;
    private final List<User> userList = new ArrayList<>();
    private final List<Place> placeList = new ArrayList<>();
    private User currentUser;

    public CovidChecker(FirebaseUser user, DatabaseReference refPlaces, DatabaseReference refUsers)
    {
        this.refPlaces = refPlaces;
        this.refUsers = refUsers;
        this.refCurrentUser = refUsers.child(user.getUid());
        this.refUsers.addValueEventListener(getUsers());
        this.refPlaces.addValueEventListener(getPlaces());
    }

    public void switchInfectionStatus(boolean status)
    {
        currentUser.setInfected(status);
        currentUser.setLastUpdate(TimeProvider.nowEpoch());
        refCurrentUser.setValue(currentUser);
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
                currentUser = snapshot.getValue(User.class);
                infectionSwitch.setChecked(currentUser.isInfected());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                System.err.println(error);
            }
        };
    }

    private ValueEventListener getUsers()
    {
        return new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                userList.clear();
                snapshot.getChildren().forEach(dataSnapshot ->
                {
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                System.err.println(error);
            }
        };
    }

    private ValueEventListener getPlaces()
    {
        return new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                placeList.clear();
                snapshot.getChildren().forEach(dataSnapshot ->
                {
                    PlaceSerializable placeSerializable = dataSnapshot.getValue(PlaceSerializable.class);
                    refUsers.child(placeSerializable.getUserId()).addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            Place place = new Place(placeSerializable, snapshot.getValue(User.class));
                            placeList.add(place);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {
                            System.err.println(error);
                        }
                    });
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                System.err.println(error);
            }
        };
    }
}
