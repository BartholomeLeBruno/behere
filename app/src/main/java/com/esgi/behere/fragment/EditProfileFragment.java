package com.esgi.behere.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.error.VolleyError;
import com.esgi.behere.LoginActivity;
import com.esgi.behere.R;
import com.esgi.behere.utils.ApiUsage;
import com.esgi.behere.utils.InformationMessage;
import com.esgi.behere.utils.PopupAchievement;
import com.esgi.behere.utils.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.FacebookSdk.getCacheDir;

public class EditProfileFragment extends Fragment {

    private static final String TAG = "voila";
    private SharedPreferences sharedPreferences;
    private VolleyCallback mResultCallback = null;
    private ApiUsage mVolleyService;
    private TextView tvEmail, tvName, tvSurname, tvNamePerson, tvDescription;
    private Button btnBirthDate, btnUploadPhoto;
    private static final int PICK_IMAGE = 1;
    private Button btnDeleteAccount;
    ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);
        tvEmail = rootView.findViewById(R.id.tvEmail);
        tvName = rootView.findViewById(R.id.tvNameGroup);
        tvSurname = rootView.findViewById(R.id.tvSurname);
        btnUploadPhoto = rootView.findViewById(R.id.btnUploadPhoto);
        tvDescription = rootView.findViewById(R.id.tvDescription);
        btnDeleteAccount = rootView.findViewById(R.id.btnDeleteAccount);
        imageView = getActivity().findViewById(R.id.ivProfile);
        tvNamePerson = Objects.requireNonNull(getActivity()).findViewById(R.id.tvNamePerson);
        btnBirthDate = rootView.findViewById(R.id.btnEditBirthDate);
        Button btnupdate = rootView.findViewById(R.id.btnUpdate);
        sharedPreferences = rootView.getContext().getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        prepareGetUser();
        mVolleyService = new ApiUsage(mResultCallback, rootView.getContext());
        mVolleyService.getUser(sharedPreferences.getLong(getString(R.string.prefs_id), 0));

        btnBirthDate.setOnClickListener(this::showDatePickerDialog);

        btnupdate.setOnClickListener((View v) -> {
            prepareUpdateUser();
            mVolleyService = new ApiUsage(mResultCallback, rootView.getContext());
            mVolleyService.updateUser(sharedPreferences.getLong(getString(R.string.prefs_id), 0),
                    tvEmail.getText().toString(), tvName.getText().toString(),
                    tvSurname.getText().toString(), btnBirthDate.getText().toString(), tvDescription.getText().toString(), sharedPreferences.getString(getString(R.string.access_token), ""));
        });
        btnUploadPhoto.setOnClickListener(v -> showFileChooser());
        btnDeleteAccount.setOnClickListener(this::onButtonShowPopupWindowClick);
        return rootView;
    }

    public void showFileChooser() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            assert uri != null;
            String filename = getFileName(uri);

            String extension = filename.substring(filename.indexOf(".") + 1);
            String path_temp = getCacheDir() + filename;
            File file = new File(path_temp);
            FileOutputStream fos = null;
            try {
                byte[] buffer = new byte[1024];
                fos = new FileOutputStream(file);
                InputStream is = getContext().getContentResolver().openInputStream(uri);
                assert is != null;
                int len = is.read(buffer);
                while (len != -1) {
                    fos.write(buffer, 0, len);
                    len = is.read(buffer);
                }
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Error while opening file", e);
            } catch (IOException e) {
                Log.e(TAG, "Error while opening file", e);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error while closing output stream", e);
                    }
                }
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void prepareGetUser() {
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
                        btnBirthDate.setText(objres.getString("birthDate").substring(0, 10));
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

    private void prepareUpdateUser() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                InformationMessage.createToastInformation(Objects.requireNonNull(getActivity()), getLayoutInflater(), getApplicationContext(), R.drawable.ic_insert_emoticon_blue_24dp,
                        "We love you my love");
                prepareGetUser();
                mVolleyService = new ApiUsage(mResultCallback, getContext());
                mVolleyService.getUser(sharedPreferences.getLong(getString(R.string.prefs_id), 0));
            }

            @Override
            public void onError(VolleyError error) {
                if (error.networkResponse.statusCode == 500) {
                    new PopupAchievement().popupAuthentification(getView());
                }
            }
        };
    }

    private void prepareEmty() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
            }

            @Override
            public void onError(VolleyError error) {
                if (error.networkResponse.statusCode == 500) {
                    new PopupAchievement().popupAuthentification(getView());
                }
            }
        };
    }

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_confirmation_delete_account, null);
        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window token
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        Button btnConfirmDelete = popupView.findViewById(R.id.btnConfirm);
        Button btnCancelDelete = popupView.findViewById(R.id.btnCancel);
        btnCancelDelete.setOnClickListener(v -> popupWindow.dismiss());
        btnConfirmDelete.setOnClickListener(v -> {
            prepareDeleteUser();
            mVolleyService = new ApiUsage(mResultCallback, getApplicationContext());
            mVolleyService.deleteUser(sharedPreferences.getLong(getString(R.string.prefs_id), 0), sharedPreferences.getString(getString(R.string.access_token), ""));
        });

    }

    private void prepareDeleteUser() {
        mResultCallback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (!(boolean) response.get("error")) {
                        Intent next = new Intent(getApplicationContext(), LoginActivity.class);
                        sharedPreferences.edit().clear().apply();
                        next.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(next);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                if (error.networkResponse.statusCode == 500) {
                    new PopupAchievement().popupAuthentification(getView());
                }
            }
        };
    }

    private void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        if (getFragmentManager() != null)
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
            if (getActivity() != null) {
                return new DatePickerDialog(getActivity(),
                        R.style.CustomDatePickerDialogTheme, this, year, month, day);
            }
            throw new RuntimeException();
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            month = month + 1;
            if (calculateAge(year, month, day) < 18)
                InformationMessage.createToastInformation(Objects.requireNonNull(getActivity()), getLayoutInflater(), Objects.requireNonNull(getContext()), R.drawable.ic_child_friendly_blue_24dp, "We accept minor with pickaxe, no minor with baby bottle !");
            else
                ((Button) Objects.requireNonNull(getActivity()).findViewById(R.id.btnEditBirthDate)).setText(year + "-" + month + "-" + day);

        }

        private int calculateAge(int year, int month, int day) {
            LocalDate birthDate = LocalDate.of(year, month, day);
            return Period.between(birthDate, LocalDate.now()).getYears();
        }
    }
}