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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import pl.kibicelecha.covidcheck.R;
import pl.kibicelecha.covidcheck.model.PlaceSerializable;
import pl.kibicelecha.covidcheck.module.Database;
import pl.kibicelecha.covidcheck.util.GeoProvider;
import pl.kibicelecha.covidcheck.util.TimeProvider;

public class MapActivity extends BaseActivity implements OnMapReadyCallback
{

    private static final String DATE_PATTERN = "dd.MM.yyyy";
    private static final String TIME_PATTERN = "HH:mm";
    private static final float CAMERA_ZOOM_VALUE = 14.0f;
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

        geoProvider = new GeoProvider(this);
        localDateTime = TimeProvider.now();

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
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(geoProvider.getLocationName(latLng.latitude, latLng.longitude));
            googleMap.clear();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_ZOOM_VALUE));
            googleMap.addMarker(markerOptions);
            this.latLng = latLng;
        });
    }

    public void showDateDialog(View view)
    {
        DatePickerDialog.OnDateSetListener listener = (datePicker, year, month, day) ->
        {
            localDateTime = localDateTime.withYear(year).withMonth(month + 1).withDayOfMonth(day);
            checkDateCorrectness(localDateTime);
            mDate.setText(getDateAsString(DATE_PATTERN));
            mTime.setText(getDateAsString(TIME_PATTERN));
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, listener,
                localDateTime.getYear(),
                localDateTime.getMonthValue(),
                localDateTime.getDayOfMonth());
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public void showTimeDialog(View view)
    {
        TimePickerDialog.OnTimeSetListener listener = (timePicker, hour, minute) ->
        {
            localDateTime = localDateTime.withHour(hour).withMinute(minute);
            checkDateCorrectness(localDateTime);
            mTime.setText(getDateAsString(TIME_PATTERN));
        };
        new TimePickerDialog(this, listener,
                localDateTime.getHour(),
                localDateTime.getMinute(),
                true)
                .show();
    }

    public void addLocation(View view)
    {
        if (latLng != null)
        {
            PlaceSerializable place = new PlaceSerializable(auth.getUid(),
                    latLng.latitude,
                    latLng.longitude,
                    TimeProvider.toEpoch(localDateTime));
            Database.getPlacesRef().push().setValue(place)
                    .addOnSuccessListener(this, task ->
                            Toast.makeText(this,
                                    R.string.main_txt_added_loc,
                                    Toast.LENGTH_SHORT).show());
            finish();
        }
        else
        {
            Toast.makeText(this, R.string.global_err_location_null,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String getDateAsString(String pattern)
    {
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    private void checkDateCorrectness(LocalDateTime localDateTime)
    {
        if (TimeProvider.checkFutureDate(localDateTime))
        {
            setCurrentTime();
        }
    }

    private void setCurrentTime()
    {
        localDateTime = TimeProvider.now();
        Toast.makeText(this, R.string.map_info_bad_time, Toast.LENGTH_SHORT).show();
    }
}