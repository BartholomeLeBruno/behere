package com.esgi.behere.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.esgi.behere.LoginActivity;
import com.esgi.behere.R;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.InformationMessage;
import com.esgi.behere.utils.PopupAchievement;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class EditProfileFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    private TextView tvEmail, tvName, tvSurname, tvNamePerson, tvDescription;
    private Button btnBirthDate, btnUploadPhoto;
    private   static final int PICK_IMAGE =1;
    ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);
        tvEmail = rootView.findViewById(R.id.tvEmail);
        tvName = rootView.findViewById(R.id.tvNameGroup);
        tvSurname = rootView.findViewById(R.id.tvSurname);
        btnUploadPhoto = rootView.findViewById(R.id.btnUploadPhoto);
        imageView = rootView.findViewById(R.id.imageViewtest);
        tvDescription = rootView.findViewById(R.id.tvDescription);
        btnUploadPhoto.setOnClickListener(v -> {
            Intent intent=new Intent(Intent.ACTION_PICK);
            // Sets the type as image/*. This ensures only components of type image are selected
            intent.setType("image/*");
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
            // Launching the Intent
            startActivityForResult(intent,PICK_IMAGE);
        });
        tvNamePerson = Objects.requireNonNull(getActivity()).findViewById(R.id.tvNamePerson);
        btnBirthDate = rootView.findViewById(R.id.btnEditBirthDate);
        Button btnupdate = rootView.findViewById(R.id.btnUpdate);

        sharedPreferences = rootView.getContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        prepareGetUser();
        mVolleyService = new ApiUsage(mResultCallback,rootView.getContext());
        mVolleyService.getUser(sharedPreferences.getLong(getString(R.string.prefs_id),0));

        btnBirthDate.setOnClickListener(this::showDatePickerDialog);

        btnupdate.setOnClickListener((View v) -> {
                prepareUpdateUser();
                mVolleyService = new ApiUsage(mResultCallback,rootView.getContext());
                mVolleyService.updateUser(sharedPreferences.getLong(getString(R.string.prefs_id),0),
                        tvEmail.getText().toString(), tvName.getText().toString(),
                        tvSurname.getText().toString(), btnBirthDate.getText().toString(), tvDescription.getText().toString(), sharedPreferences.getString(getString(R.string.access_token),""));
        });


        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_IMAGE)
        {
            Bitmap bitmap = null;
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            // Get the cursor
            Cursor cursor =getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();
            //Get the column index of MediaStore.Images.Media.DATA
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            //Gets the String value in the column
            String imgDecodableString = cursor.getString(columnIndex);
            btnUploadPhoto.setText(imgDecodableString);
            cursor.close();
            prepareEmty();
            File file = new File(imgDecodableString);
            if(file.exists())
                Log.d("exist",file.getAbsolutePath());


            mVolleyService = new ApiUsage(mResultCallback,getContext());
            mVolleyService.uploadPictureUser(file, sharedPreferences.getLong(getString(R.string.prefs_id),0),sharedPreferences.getString(getString(R.string.access_token),""));
            imageView.setImageBitmap(bitmap);

            }
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
                        tvDescription.setText(objres.getString("description"));

                        tvNamePerson.setText(String.format("%s %s", name, surname));
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
                mVolleyService.getUser(sharedPreferences.getLong(getString(R.string.prefs_id),0));
            }
            @Override
            public void onError(VolleyError error) {
                if(error.networkResponse.statusCode == 500)
                {
                    new PopupAchievement().popupAuthentification(getView());
                }
            }
        };
    }

    private void prepareEmty(){
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
            }
            @Override
            public void onError(VolleyError error) {
               Toast.makeText(getApplicationContext(),error.getLocalizedMessage()+"",Toast.LENGTH_LONG).show();
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


        @NonNull
        @Override
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
            throw new RuntimeException();
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