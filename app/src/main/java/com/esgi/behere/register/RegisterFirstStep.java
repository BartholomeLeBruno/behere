package com.esgi.behere.register;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.esgi.behere.R;
import com.esgi.behere.actor.User;
import com.esgi.behere.utils.InformationMessage;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Objects;

public class RegisterFirstStep extends AppCompatActivity {

    private EditText name;
    private EditText surname;
    private EditText email;
    private Button btnBirthDate;
    private EditText password;
    private EditText checkPassword;
    public User newUser = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register_first_step);
        name = findViewById(R.id.pseudoPubPro);
        surname = findViewById(R.id.surname);
        email = findViewById(R.id.email);
        btnBirthDate = findViewById(R.id.btnBirthDate);
        password = findViewById(R.id.password);
        checkPassword = findViewById(R.id.checkpassword);

        Button continueStep = findViewById(R.id.continueStep);

        continueStep.setOnClickListener((View v)  -> {
                if (password.getText().toString().equals(checkPassword.getText().toString()))
                {
                    if (!name.getText().toString().equals("")
                            && !surname.getText().toString().equals("")
                            && !email.getText().toString().equals("")
                            && !btnBirthDate.getText().toString().equals("")
                            && !btnBirthDate.getText().toString().toUpperCase().equals("BIRTHDATE")
                            && !password.getText().toString().equals("")
                            && !checkPassword.getText().toString().equals("")) {
                        newUser.setName(name.getText().toString());
                        newUser.setSurname(surname.getText().toString());
                        newUser.setEmail(email.getText().toString());
                        newUser.setBirthDate(btnBirthDate.getText().toString());
                        newUser.setPassword(password.getText().toString());
                        newUser.setCheckPassword(checkPassword.getText().toString());
                        Intent nextStep = new Intent(RegisterFirstStep.this, RegisterSecondStep.class);
                        nextStep.putExtra("User", newUser);
                        startActivity(nextStep);
                    }
            }
                else{
                    InformationMessage.createToastInformation(RegisterFirstStep.this, getLayoutInflater(), getApplicationContext() ,R.drawable.ic_highlight_off_red_24dp, "Both Password can't be the same moldu !");
                }
        });
    }
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
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
            return new DatePickerDialog(Objects.requireNonNull(getActivity()),
                    R.style.CustomDatePickerDialogTheme, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            month = month + 1;
            if(calculateAge(year,month,day) < 18)
                InformationMessage.createToastInformation(getActivity(), getLayoutInflater(), Objects.requireNonNull(getContext()),R.drawable.ic_child_friendly_blue_24dp, "We accept minor with pickaxe, no minor with baby bottle !");
            else
                ((Button) Objects.requireNonNull(getActivity()).findViewById(R.id.btnBirthDate)).setText(year + "-" + month + "-" + day);

        }
        public int calculateAge(int year, int month, int day) {
            LocalDate birthDate = LocalDate.of(year, month, day);
            return Period.between(birthDate, LocalDate.now()).getYears();
        }
    }
    }


