package com.manuj.userdairy;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class Enroll_Fragment extends Fragment {

    JSONArray jsonCountryArray;
     Spinner countrySpinner;
     Spinner stateSpinner;
     Spinner citySpinner;
     ImageView imageView;
     User user;
     Button Add;
    private Uri filePath;
    boolean userSaved=false;
    public static MyUserAdapter myUserAdapter;


    EditText FName,LName,Dob,phone,Gender;
    public static Calendar chosenDate;
    private final int PICK_IMAGE_REQUEST = 22;
    int TAKE_IMAGE_CODE = 10001;
  String RandomUid;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_enroll_, container, false);

        countrySpinner = view.findViewById(R.id.country);
        stateSpinner = view.findViewById(R.id.state);
        citySpinner = view.findViewById(R.id.city);
        FName=view.findViewById(R.id.displayNameEditText);
        LName=view.findViewById(R.id.lastname);
        Dob=view.findViewById(R.id.dob);
        phone=view.findViewById(R.id.phone);
        imageView=view.findViewById(R.id.profileImageView);
        Add=view.findViewById(R.id.updateProfileButton);
        Gender=view.findViewById(R.id.gender);
        populateSpinner();
        user=new User();

         RandomUid= UUID.randomUUID().toString();

        chosenDate=Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

      Add.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              int countryPosition = countrySpinner.getSelectedItemPosition();
              int statePosition = stateSpinner.getSelectedItemPosition();
              int cityPosition = citySpinner.getSelectedItemPosition();
         user.FirstName=FName.getText().toString();
         user.LastName=LName.getText().toString();
         user.DateOfBirth=Dob.getText().toString();
         user.Phone=phone.getText().toString();
         user.Gender=Gender.getText().toString();
         user.ImageUid=RandomUid;
         user.Country=jsonCountryArray
                 .optJSONObject(countryPosition)
                 .optString("name");
         user.State=jsonCountryArray
                 .optJSONObject(statePosition)
                 .optString("name");
         user.City=jsonCountryArray
                 .optJSONObject(cityPosition)
                 .optString("name");

              if(user.FirstName.isEmpty()){
                  FName.setText("");
                  FName.setFocusable(true);
                  FName.setError("First Name cannot be empty");
                  return;
              }
              if(user.LastName.isEmpty()){
                  LName.setText("");
                  LName.setFocusable(true);
                  LName.setError("Last Name cannot be empty");
                  return;
              }
              if (user.Phone.isEmpty() && user.Phone.length()!=10 ) {
                  phone.setError("Enter a valid mobile");
                  phone.setFocusable(true);
                  phone.setText("");
                  return;
              }
              if (user.DateOfBirth.isEmpty() ) {
                  Dob.setError("Enter a valid DOB");
                  Dob.setFocusable(true);
                  Dob.setText("");
                  return;
              }
              if (user.Gender.isEmpty()) {
                  Gender.setError("Please specify Gender(M/F/O)");
                  Gender.setFocusable(true);
                  Gender.setText("");
                  return;
              }


         saveUserInFirebase();
          }
      });

      Dob.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                  @SuppressLint("ResourceAsColor")
                  public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                      StringBuilder sb = new StringBuilder();
                      sb.append(day);
                      String str = "/";
                      sb.append(str);
                      sb.append(month + 1);
                      sb.append(str);
                      sb.append(year);
                      Dob.setText(sb.toString());
                      Dob.setTextColor(R.color.colorBlack);
                      chosenDate.set(year, month, day);
                      Log.i("chosenDate", "" + chosenDate);
                  }
              }, year, month, dayOfMonth);
              datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
              datePickerDialog.show();
          }
              });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });


        return view;
    }

    private void SelectImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }


    private void handleUpload (Bitmap bitmap){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);

        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(RandomUid + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       getDownloadUrl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }


    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //setUserProfileUrl(uri);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(bitmap);
                          handleUpload(bitmap);


            }
        }
        else if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                final Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getActivity().getContentResolver(),
                                filePath);

                    handleUpload(bitmap);


                Glide.with(getActivity()).load(filePath).into(imageView);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }
    public void handleImageClick(View view) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }
    }
    private void showPictureDialog(){

        AlertDialog.Builder picDialog = new AlertDialog.Builder(getActivity());
        picDialog.setTitle("Choose an Action");
        String[] picDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        picDialog.setItems(picDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                switch(which){

                    case 0:
                        SelectImage();
                        break;
                    case 1:
                        handleImageClick(imageView);
//
                        break;
                }
            }
        });
        picDialog.show();

    }


    void saveUserInFirebase() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                userSaved=true;
                Toast.makeText(getActivity(), "User Added Successfully!", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Something Went Wrong !!", Toast.LENGTH_LONG).show();
                    }
                });

    }
    private void populateSpinner() {
        try {
            jsonCountryArray = new JSONObject(loadJSONFromAsset()).optJSONArray("country");

            ArrayList<String> countryList = new ArrayList<>();

            for (int i = 0; i < jsonCountryArray.length(); i++) {
                countryList.add(jsonCountryArray.optJSONObject(i).optString("name"));
            }

            ArrayAdapter<String> countryListAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, countryList);

            countrySpinner.setAdapter(countryListAdapter);

            countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ArrayList<String> stateArray = new ArrayList<>();

                    final JSONArray jsonStateArray = jsonCountryArray.optJSONObject(position).optJSONArray("state");

                    for (int i = 0; i < jsonStateArray.length(); i++) {
                        stateArray.add(jsonStateArray.optJSONObject(i).optString("name"));
                    }

                    ArrayAdapter<String> stateListAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, stateArray);

                    stateSpinner.setAdapter(stateListAdapter);

                    stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            final ArrayList<String> cityArray = new ArrayList<>();

                            final JSONArray jsonCityArray = jsonStateArray.optJSONObject(position).optJSONArray("city");

                            for (int i = 0; i < jsonCityArray.length(); i++) {
                                cityArray.add(jsonCityArray.optJSONObject(i).optString("name"));
                            }

                            ArrayAdapter<String> cityListAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, cityArray);

                            citySpinner.setAdapter(cityListAdapter);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getActivity().getAssets().open("country.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


}