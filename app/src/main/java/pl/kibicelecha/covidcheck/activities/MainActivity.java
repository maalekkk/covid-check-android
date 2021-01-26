package pl.kibicelecha.covidcheck.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import com.developer.kalert.KAlertDialog;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.time.format.DateTimeFormatter;

import pl.kibicelecha.covidcheck.R;
import pl.kibicelecha.covidcheck.model.PlaceSerializable;
import pl.kibicelecha.covidcheck.module.CovidChecker;
import pl.kibicelecha.covidcheck.util.GeoProvider;
import pl.kibicelecha.covidcheck.util.TimeProvider;

public class MainActivity extends BaseActivity
{
    private static final String USER_ID = "userId";
    private static final String DATETIME_PATTERN = "HH:mm, dd.MM.yyyy";
    private static final String GEO = "geo:0,0?q=";
    private static final String COLON = ": ";
    private static final int RECENT_PLACES_LIMIT = 10;
    private DatabaseReference refPlaces;
    private DatabaseReference refUsers;
    private CovidChecker covidChecker;
    private GeoProvider geoProvider;
    private SwitchCompat mInfectionSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        geoProvider = new GeoProvider(this);

        refPlaces = database.getReference().child(DB_COLLECTION_PLACE);
        refPlaces.keepSynced(true);
        refUsers = database.getReference().child(DB_COLLECTION_USERS);
        refUsers.keepSynced(true);
        covidChecker = new CovidChecker(auth.getCurrentUser(), refPlaces, refUsers);

        TextView mNickname = findViewById(R.id.nickname_home_txt);
        mNickname.setText(auth.getCurrentUser().getDisplayName());

        mInfectionSwitch = findViewById(R.id.infection_switch);
        covidChecker.checkInfectionStatus(mInfectionSwitch);
        mInfectionSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (mInfectionSwitch.isPressed())
            {
                covidChecker.switchInfectionStatus(isChecked);
            }
        });

        auth.addAuthStateListener(firebaseAuth ->
        {
            if (firebaseAuth.getCurrentUser() == null)
            {
                startActivityAndFinish(this, LoginActivity.class);
            }
        });

        initLocationsList();
    }

    public void showLocationDialog(View view)
    {
        getLocation();
        KAlertDialog locationDialog = new KAlertDialog(this, KAlertDialog.CUSTOM_IMAGE_TYPE);
        locationDialog.setTitleText(getString(R.string.main_txt_auto_loc))
                .setContentText(getString(R.string.main_info_share_your_loc))
                .setCustomImage(R.drawable.location)
                .setCancelText(getString(R.string.main_info_reject))
                .setConfirmText(getString(R.string.global_txt_yes))
                .cancelButtonColor(R.color.chestnut_rose)
                .confirmButtonColor(R.color.success_stroke_color)
                .setConfirmClickListener(kAlertDialog ->
                {
                    if (location != null)
                    {
                        refPlaces.push().setValue(new PlaceSerializable(auth.getCurrentUser().getUid(),
                                location.getLatitude(),
                                location.getLongitude(),
                                TimeProvider.nowEpoch()))
                                .addOnSuccessListener(this, task ->
                                        Toast.makeText(this, R.string.main_txt_added_loc, Toast.LENGTH_SHORT).show());
                    }
                    else
                    {
                        Toast.makeText(this, R.string.global_err_location_null, Toast.LENGTH_LONG).show();
                    }
                    kAlertDialog.dismissWithAnimation();
                })
                .setCancelClickListener(kAlertDialog ->
                {
                    startActivity(this, MapActivity.class);
                    kAlertDialog.dismissWithAnimation();
                })
                .show();
        locationDialog.setCanceledOnTouchOutside(true);
    }

    public void showAccountDialog(View view)
    {
        KAlertDialog locationDialog = new KAlertDialog(this, KAlertDialog.CUSTOM_IMAGE_TYPE);
        String email = auth.getCurrentUser().getEmail();
        String username = auth.getCurrentUser().getDisplayName();
        String accountInfo = getString(R.string.global_txt_username) + COLON + System.lineSeparator() +
                username + System.lineSeparator() + System.lineSeparator()
                + getString(R.string.global_txt_email) + COLON + System.lineSeparator() + email;
        locationDialog.setContentText(accountInfo)
                .setTitleText(getString(R.string.main_txt_your_acc))
                .setCustomImage(R.drawable.ic_baseline_account_circle_24)
                .setCancelText(getString(R.string.main_txt_logout))
                .setConfirmText(getString(R.string.global_txt_ok))
                .confirmButtonColor(R.color.success_stroke_color)
                .cancelButtonColor(R.color.chestnut_rose)
                .setCancelClickListener(kAlertDialog -> showLogoutDialog(view))
                .setConfirmClickListener(KAlertDialog::dismissWithAnimation)
                .show();
        locationDialog.setCanceledOnTouchOutside(true);
    }

    public void showLogoutDialog(View view)
    {
        KAlertDialog logoutDialog = new KAlertDialog(this, KAlertDialog.WARNING_TYPE);
        logoutDialog.setTitleText(getString(R.string.main_txt_logout))
                .setContentText(getString(R.string.main_info_ask_logout))
                .setConfirmText(getString(R.string.main_txt_stay))
                .setCancelText(getString(R.string.main_txt_logout))
                .cancelButtonColor(R.color.chestnut_rose)
                .confirmButtonColor(R.color.success_stroke_color)
                .setCancelClickListener(kAlertDialog -> logout(view))
                .setConfirmClickListener(KAlertDialog::dismissWithAnimation)
                .show();
        logoutDialog.setCanceledOnTouchOutside(true);
    }

    public void logout(View view)
    {
        auth.signOut();
    }

    private void initLocationsList()
    {
        Query refUserPlaces = refPlaces.orderByChild(USER_ID).equalTo(auth.getCurrentUser().getUid()).limitToLast(RECENT_PLACES_LIMIT);
        ListView listView = findViewById(R.id.recent_locations_list);
        FirebaseListOptions<PlaceSerializable> options = new FirebaseListOptions.Builder<PlaceSerializable>()
                .setLayout(R.layout.place_list_row)
                .setQuery(refUserPlaces, PlaceSerializable.class)
                .setLifecycleOwner(this)
                .build();

        listView.setAdapter(new FirebaseListAdapter<PlaceSerializable>(options)
        {
            @Override
            protected void populateView(@NonNull View v, @NonNull PlaceSerializable model, int position)
            {
                String address = geoProvider.getLocationName(model.getLatitude(), model.getLongitude());
                ((TextView) v.findViewById(android.R.id.text1)).setText(address);
                ((TextView) v.findViewById(android.R.id.text2))
                        .setText(TimeProvider.fromEpoch(model.getTimestamp())
                                .format(DateTimeFormatter.ofPattern(DATETIME_PATTERN)));
            }

            @NonNull
            @Override
            public PlaceSerializable getItem(int position)
            {
                return super.getItem(getCount() - 1 - position);
            }
        });

        listView.setOnItemClickListener((adapterView, view, i, l) ->
        {
            PlaceSerializable placeSerializable = (PlaceSerializable) adapterView.getItemAtPosition(i);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GEO + placeSerializable)));
        });
    }
}