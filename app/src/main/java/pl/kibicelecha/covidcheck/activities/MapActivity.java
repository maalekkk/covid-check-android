package pl.kibicelecha.covidcheck.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import pl.kibicelecha.covidcheck.R;
import pl.kibicelecha.covidcheck.model.PlaceSerializable;
import pl.kibicelecha.covidcheck.util.GeoProvider;
import pl.kibicelecha.covidcheck.util.TimeProvider;

public class MapActivity extends BaseActivity implements OnMapReadyCallback
{

    private static final String DATE_PATTERN = "dd.MM.yyyy";
    private static final String TIME_PATTERN = "HH:mm";
    private DatabaseReference refPlaces;
    private GeoProvider geoProvider;
    private LatLng latLng;
    private TextView mDate;
    private TextView mTime;
    private LocalDateTime localDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        refPlaces = database.getReference().child(DB_COLLECTION_PLACE);
        refPlaces.keepSynced(true);
        geoProvider = new GeoProvider(getApplicationContext());

        localDateTime = LocalDateTime.now();
        mDate = findViewById(R.id.date_picker_txt);
        mTime = findViewById(R.id.time_picker_txt);
        mDate.setText(getDateAsString(DATE_PATTERN));
        mTime.setText(getDateAsString(TIME_PATTERN));
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        googleMap.setOnMapClickListener(latLng ->
        {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(geoProvider.getLocationName(latLng.latitude, latLng.longitude));
            googleMap.clear();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            googleMap.addMarker(markerOptions);
            this.latLng = latLng;
        });
    }

    public void showDateDialog(View view)
    {
        DatePickerDialog.OnDateSetListener onDateSetListener = (datePicker, year, month, day) ->
        {
            localDateTime = localDateTime.withYear(year).withMonth(month + 1).withDayOfMonth(day);
            mDate.setText(getDateAsString(DATE_PATTERN));
            if (ifFutureTime())
            {
                setTimeToNow();
                mTime.setText(getDateAsString(TIME_PATTERN));
                Toast.makeText(this, R.string.map_info_bad_time, Toast.LENGTH_SHORT).show();
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                onDateSetListener,
                localDateTime.getYear(),
                localDateTime.getMonthValue(),
                localDateTime.getDayOfMonth());
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public void showTimeDialog(View view)
    {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, hour, minute) ->
        {
            localDateTime = localDateTime.withHour(hour).withMinute(minute);
            if (ifFutureTime())
            {
                setTimeToNow();
                Toast.makeText(this, R.string.map_info_bad_time, Toast.LENGTH_SHORT).show();
            }
            mTime.setText(getDateAsString(TIME_PATTERN));
        };
        new TimePickerDialog(this,
                onTimeSetListener,
                localDateTime.getHour(),
                localDateTime.getMinute(),
                true).show();
    }

    public void addLocation(View view)
    {
        if (latLng != null)
        {
            refPlaces.push().setValue(new PlaceSerializable(
                    auth.getUid(),
                    latLng.latitude,
                    latLng.longitude,
                    TimeProvider.toEpoch(localDateTime)))
                    .addOnSuccessListener(this, task ->
                            Toast.makeText(this,
                                    R.string.main_txt_added_loc,
                                    Toast.LENGTH_SHORT).show());
        }
        finish();
    }

    private String getDateAsString(String pattern)
    {
        return TimeProvider.fromEpoch(localDateTime.toEpochSecond(ZoneOffset.UTC))
                .format(DateTimeFormatter.ofPattern(pattern));
    }

    private boolean ifFutureTime()
    {
        return localDateTime.isAfter(LocalDateTime.now());
    }

    private void setTimeToNow()
    {
        LocalDateTime now = LocalDateTime.now();
        localDateTime = localDateTime.withHour(now.getHour()).withMinute(now.getMinute());
    }
}