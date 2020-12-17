package pl.kibicelecha.covidcheck.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.developer.kalert.KAlertDialog;

import pl.kibicelecha.covidcheck.R;

public class MainActivity extends BaseActivity {

    private TextView mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEmail = findViewById(R.id.email_home_txt);
        mEmail.setText(auth.getCurrentUser().getEmail());
    }

    public void showAccountDialog(View view) {
//        Dialog accountDialog = new Dialog(this);
//        accountDialog.setContentView(R.layout.account_popup);
//        WindowManager.LayoutParams lp = accountDialog.getWindow().getAttributes();
//        lp.dimAmount = 0.4F;
//        accountDialog.getWindow().setAttributes(lp);
//        accountDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        accountDialog.getWindow()
//                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.85);
//        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.6);
//        accountDialog.getWindow().setLayout(width, height);
//
//        TextView mAccountUsername = accountDialog.findViewById(R.id.username_account_txt);
//        TextView mAccountEmail = accountDialog.findViewById(R.id.email_account_txt);
//        mAccountUsername.setText(auth.getCurrentUser().getDisplayName());
//        mAccountEmail.setText(auth.getCurrentUser().getEmail());
//        ((ViewGroup) accountDialog.getWindow().getDecorView())
//                .getChildAt(0).startAnimation(AnimationUtils.loadAnimation(
//                this, android.R.anim.fade_in));
//        accountDialog.show();
        KAlertDialog locationDialog = new KAlertDialog(this, KAlertDialog.CUSTOM_IMAGE_TYPE);
        String email = auth.getCurrentUser().getEmail();
        String username = auth.getCurrentUser().getDisplayName();
        String accountInfo = "Nazwa użytkownika: " + System.lineSeparator() +
                username + System.lineSeparator() + System.lineSeparator()
                + "Adres email: " + System.lineSeparator() + email;
        locationDialog.setContentText(accountInfo)
                .setTitleText("Twoje konto")
                .setCustomImage(R.drawable.ic_baseline_account_circle_24)
                .setCancelText("Wyloguj się")
                .setConfirmText("Ok")
                .confirmButtonColor(R.color.success_stroke_color)
                .cancelButtonColor(R.color.chestnut_rose)
                .setCancelClickListener(kAlertDialog -> showLogoutDialog(view))
                .show();
        locationDialog.setCanceledOnTouchOutside(true);
    }

    public void showLocationDialog(View view) {
        KAlertDialog locationDialog = new KAlertDialog(this, KAlertDialog.CUSTOM_IMAGE_TYPE);
        locationDialog.setTitleText("Automatyczna Lokalizacja")
                .setContentText("Czy chcesz udostępnić swoją obecną lokalizację?")
                .setCustomImage(R.drawable.location)
                .setCancelText("Nie")
                .setConfirmText("Tak")
                .cancelButtonColor(R.color.chestnut_rose)
                .confirmButtonColor(R.color.success_stroke_color)
                .setCancelClickListener(
                        kAlertDialog -> startActivity(MainActivity.this, MapActivity.class))
                .setConfirmClickListener(
                        kAlertDialog -> locationDialog.dismissWithAnimation())
                .show();
        locationDialog.setCanceledOnTouchOutside(true);
    }

    public void showLogoutDialog(View view) {
        KAlertDialog logoutDialog = new KAlertDialog(this, KAlertDialog.WARNING_TYPE);
        logoutDialog.setTitleText("Wyloguj się")
                .setContentText("Czy na pewno chcesz się wylogować?")
                .setConfirmText("Pozostań z nami")
                .setCancelText("Wyloguj się")
                .cancelButtonColor(R.color.chestnut_rose)
                .confirmButtonColor(R.color.success_stroke_color)
                .setCancelClickListener(kAlertDialog -> logout(view))
                .setConfirmClickListener(kAlertDialog -> logoutDialog.dismissWithAnimation())
                .show();
        logoutDialog.setCanceledOnTouchOutside(true);
    }

    public void logout(View view) {
        auth.signOut();
        startActivityAndFinish(this, LoginActivity.class);
    }
}