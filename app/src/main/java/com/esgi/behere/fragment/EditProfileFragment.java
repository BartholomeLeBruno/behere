package com.esgi.behere.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.esgi.behere.LoginActivity;
import com.esgi.behere.R;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.InformationMessage;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class EditProfileFragment extends Fragment {

    private static final String PREFS = "PREFS";
    private static final String PREFS_ID = "USER_ID";
    private static final String PREFS_ACCESS_TOKEN = "ACCESS_TOKEN";
    private SharedPreferences sharedPreferences;
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    private TextView tvEmail, tvName, tvSurname;
    private Button btnBirthDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);

        tvEmail = rootView.findViewById(R.id.tvEmail);
        tvName = rootView.findViewById(R.id.tvNameGroup);
        tvSurname = rootView.findViewById(R.id.tvSurname);
        btnBirthDate = rootView.findViewById(R.id.btnEditBirthDate);
        Button btnupdate = rootView.findViewById(R.id.btnUpdate);

        sharedPreferences = rootView.getContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        prepareGetUser();
        mVolleyService = new ApiUsage(mResultCallback,rootView.getContext());
        mVolleyService.getUser(sharedPreferences.getLong(PREFS_ID,0));

        btnBirthDate.setOnClickListener(this::showDatePickerDialog);

        btnupdate.setOnClickListener((View v) -> {
                prepareUpdateUser();
                mVolleyService = new ApiUsage(mResultCallback,rootView.getContext());
                mVolleyService.updateUser(sharedPreferences.getLong(PREFS_ID,0),
                        tvEmail.getText().toString(), tvName.getText().toString(),
                        tvSurname.getText().toString(), btnBirthDate.getText().toString(), sharedPreferences.getString(PREFS_ACCESS_TOKEN,""));
        });


        return rootView;
    }



    private void prepareGetUser(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String name;
                    String surname;
                    String email;
                    if (!(boolean) response.get("error")) {
                        JSONObject objres = (JSONObject) new JSONTokener(response.get("user").toString()).nextValue();
                        name = objres.getString("name");
                        surname = objres.getString("surname");
                        email = objres.getString("email");
                        tvName.setText(name);
                        tvSurname.setText(surname);
                        tvEmail.setText(email);
                        btnBirthDate.setText(objres.getString("birthDate").substring(0,10));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onError(VolleyError error) {
                Intent loginActivity = new Intent(getContext(), LoginActivity.class);
                sharedPreferences.getAll().clear();
                startActivity(loginActivity);
            }
        };
    }

    private void prepareUpdateUser(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                InformationMessage.createToastInformation(Objects.requireNonNull(getActivity()), getLayoutInflater(), getApplicationContext() ,R.drawable.ic_insert_emoticon_blue_24dp,
                        "We love you my love");
                prepareGetUser();
                mVolleyService = new ApiUsage(mResultCallback,getContext());
                mVolleyService.getUser(sharedPreferences.getLong(PREFS_ID,0));
            }
            @Override
            public void onError(VolleyError error) {
                Intent loginActivity = new Intent(getContext(), LoginActivity.class);
                sharedPreferences.getAll().clear();
                startActivity(loginActivity);
            }
        };
    }
    private void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        if(getFragmentManager() != null)
            newFragment.show(getFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {


        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            if(getActivity() != null) {
                return new DatePickerDialog(getActivity(),
                        R.style.CustomDatePickerDialogTheme, this, year, month, day);
            }
            return null;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            month = month + 1;
            if(calculateAge(year,month,day) < 18)
                InformationMessage.createToastInformation(Objects.requireNonNull(getActivity()), getLayoutInflater(), Objects.requireNonNull(getContext()),R.drawable.ic_child_friendly_blue_24dp, "We accept minor with pickaxe, no minor with baby bottle !");
            else
                ((Button) Objects.requireNonNull(getActivity()).findViewById(R.id.btnEditBirthDate)).setText(year + "-" + month + "-" + day);

        }
        private int calculateAge(int year, int month, int day) {
            LocalDate birthDate = LocalDate.of(year, month, day);
            return Period.between(birthDate, LocalDate.now()).getYears();
        }
    }
}