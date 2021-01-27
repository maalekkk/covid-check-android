package pl.kibicelecha.covidcheck.module;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import pl.kibicelecha.covidcheck.model.User;

public final class Database
{
    private static final String DB_COLLECTION_PLACES = "places";
    private static final String DB_COLLECTION_USERS = "users";
    private static final String DB_PLACES_USER_ID = "userId";
    private static FirebaseDatabase database;

    public static FirebaseDatabase getInstance()
    {
        if (database == null)
        {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
        }
        return database;
    }

    public static DatabaseReference getRef()
    {
        return getInstance().getReference();
    }

    public static DatabaseReference getUsersRef()
    {
        return getRef().child(DB_COLLECTION_USERS);
    }

    public static DatabaseReference getPlacesRef()
    {
        return getRef().child(DB_COLLECTION_PLACES);
    }

    public static DatabaseReference getUserRef(String userId)
    {
        return getUsersRef().child(userId);
    }

    public static Query getPlacesByUserId(String userId)
    {
        return getPlacesRef().orderByChild(DB_PLACES_USER_ID).equalTo(userId);
    }

    public static DatabaseReference getCurrentUserRef()
    {
        return getUserRef(FirebaseAuth.getInstance().getUid());
    }

    public static void getCurrentUser(UserCallback callback)
    {
        getCurrentUserRef().addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                User user = snapshot.getValue(User.class);
                callback.onUserGet(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                System.err.println(error);
            }
        });
    }

    public interface UserCallback
    {
        void onUserGet(User user);
    }
}
