package co.in.prodigyschool.passiton;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

import co.in.prodigyschool.passiton.Data.User;
import co.in.prodigyschool.passiton.util.GalleryUtil;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "USERPROFILEACTIVITY" ;

    private ImageView profilePic;
    private EditText fName, lName, year, phoneNo, uName;
    private Spinner gradeSpinner, boardSpinner, degreeSpinner;
    private FloatingActionButton saveChanges;
    private TextWatcher checkChange;
    private Menu menu;
    private int clickCount,gradeNumber,boardNumber;
    private CheckBox compExams,preferGuidedMode;
    private boolean detailsChanged = false, newUNameOK=true, tempCE;//TODO: add code to check whether new username is OK
    private TextView boardLabel;
    private String firstName, lastName, phoneNumber,userId;
    private FirebaseFirestore mFirestore;
    private User curUser;
    private Uri selectedImage;
    private ArrayAdapter<String> boardAdapter, degreeAdapter, gradeAdapter;
    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;



    private final int GALLERY_ACTIVITY_CODE=200;
    private final int RESULT_CROP = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        if(getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle("Profile");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        profilePic = findViewById(R.id.profilePic);
        preferGuidedMode = findViewById(R.id.preferGuidedMode);
        userPref = this.getSharedPreferences(getString(R.string.UserPref),0);

        fName = findViewById(R.id.firstNameProfile);
        lName = findViewById(R.id.lastNameProfile);
        uName = findViewById(R.id.usernameField);
        year = findViewById(R.id.profileYearField);
        phoneNo = findViewById(R.id.profilePhoneNumberField);

        gradeSpinner = findViewById(R.id.gradeSpinner);
        boardSpinner = findViewById(R.id.boardSpinner);
        degreeSpinner = findViewById(R.id.degreeSpinner);

        boardLabel = findViewById(R.id.boardLabel);

        saveChanges = findViewById(R.id.fab_save);
        saveChanges.hide();

        gradeAdapter = new ArrayAdapter<String>(UserProfileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.grades));
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        boardAdapter = new ArrayAdapter<String>(UserProfileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.boards));
        boardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        degreeAdapter = new ArrayAdapter<String>(UserProfileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.degrees));
        degreeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        compExams = findViewById(R.id.profileCompetitiveExams);


        checkChange = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                saveChanges.show();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        mFirestore  = FirebaseFirestore.getInstance();
        getUserPreference();
        populateUserDetails();
        fName.addTextChangedListener(checkChange);
        lName.addTextChangedListener(checkChange);
        uName.addTextChangedListener(checkChange);
        year.addTextChangedListener(checkChange);


        fName.setEnabled(false);
        lName.setEnabled(false);
        uName.setEnabled(false);
        year.setEnabled(false);
        compExams.setEnabled(false);
        gradeSpinner.setEnabled(false);
        boardSpinner.setEnabled(false);
        degreeSpinner.setEnabled(false);
        preferGuidedMode.setEnabled(false);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
            }
        });



        gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position<6) { //school grade selected

                    boardLabel.setText("Board");
                    if (boardSpinner.getAdapter()!=boardAdapter) {
                        boardSpinner.setAdapter(boardAdapter);
                    }
                    if (position==4 || position==5) {
                        compExams.setVisibility(View.VISIBLE);
                        compExams.setChecked(tempCE);
                    }
                    else {
                        compExams.setVisibility(View.GONE);
                        compExams.setChecked(false);
                    }
                }

                else { //undergrad selected
                    boardLabel.setText("Degree / course");
                    if (boardSpinner.getAdapter()!=degreeAdapter) {
                        boardSpinner.setAdapter(degreeAdapter);
                    }
                    compExams.setVisibility(View.GONE);
                    compExams.setChecked(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });
    }

    private void getUserPreference() {

            preferGuidedMode.setChecked(userPref.getBoolean(getString(R.string.preferGuidedMode),false));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile:
                if (clickCount%2==0) {
                    fName.setEnabled(true);
                    lName.setEnabled(true);
                    //uName.setEnabled(true);
                    year.setEnabled(true);
                    compExams.setEnabled(true);
                    gradeSpinner.setEnabled(true);
                    boardSpinner.setEnabled(true);
                    degreeSpinner.setEnabled(true);
                    preferGuidedMode.setEnabled(true);
                    // phoneNumber.setEnabled(true);
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_check_24px))
                            .setTitle("Save changes");
                    clickCount = clickCount + 1;
                }
                else {

                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Saving your changes");
                    progressDialog.setMessage("Please wait while we update your profile...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    // phoneNumber.setEnabled(false);
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_edit_24px))
                            .setTitle("Edit profile");
                    clickCount = clickCount + 1;

                    if (!(fName.getText().toString().length()==0 || lName.getText().toString().length()==0 || uName.getText().toString().length()==0 /*|| year.getText().toString().length()==0*/)) {

                        fName.setEnabled(false);
                        lName.setEnabled(false);
                        uName.setEnabled(false);
                        year.setEnabled(false);
                        compExams.setEnabled(false);
                        gradeSpinner.setEnabled(false);
                        boardSpinner.setEnabled(false);
                        degreeSpinner.setEnabled(false);
                        preferGuidedMode.setEnabled(false);




                        int board;
                        if (gradeSpinner.getSelectedItemPosition()>=6) {
                            board = boardSpinner.getSelectedItemPosition()+7;
                        }
                        else {
                            board = boardSpinner.getSelectedItemPosition()+1;
                        }

                        editor = userPref.edit();
                        editor.putBoolean(getString(R.string.preferGuidedMode),preferGuidedMode.isChecked());
                        editor.putString(getString(R.string.p_firstname),fName.getText().toString());
                        editor.putString(getString(R.string.p_lastname),lName.getText().toString());
//                        editor.putString(getString(R.string.p_imageurl),user.getImageUrl());
                        editor.putInt(getString(R.string.p_grade),gradeSpinner.getSelectedItemPosition() +1);
                        editor.putInt(getString(R.string.p_board),board);
                        editor.putBoolean(getString(R.string.p_competitive),compExams.isChecked());
                        editor.apply();
                        DocumentReference userReference =  mFirestore.collection("users").document(phoneNumber);
                        userReference.update("competitiveExam",compExams.isChecked(),"firstName",fName.getText().toString(),"lastName",lName.getText().toString(),"gradeNumber",gradeSpinner.getSelectedItemPosition()+1,"boardNumber",board).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(progressDialog.isShowing())
                                    progressDialog.dismiss();
                                Intent homeIntent = new Intent(UserProfileActivity.this, HomeActivity.class);
                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                homeIntent.putExtra("SNACKBAR_MSG", "Your profile has been saved");
                                startActivity(homeIntent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if(progressDialog.isShowing())
                                    progressDialog.dismiss();
                                Log.d(TAG, "onFailure: update user",e);
                                Toast.makeText(getApplicationContext(), "Update Failed", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    else {
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();

                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar.make(parentLayout, "Please fill in all fields", Snackbar.LENGTH_SHORT)
                                .setAction("OKAY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                .show();
                    }
                }
                break;
            case android.R.id.home:
                if (detailsChanged) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
                    builder.setTitle("Save your changes?");
                    builder.setMessage("Would you like to save the changes you made to your profile?");
                    builder.setPositiveButton("Save and exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent homeIntent = new Intent(UserProfileActivity.this, HomeActivity.class);
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            homeIntent.putExtra("SNACKBAR_MSG", "Your profile has been saved");
                            startActivity(homeIntent);
                        }
                    });
                    builder.setNegativeButton("Exit without saving", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishActivity1();
                        }
                    });
                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
                else {
                    finishActivity1();
                }
                break;

        }
        return true;
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    public void finishActivity1 () {
        this.finish();
    }


    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) { // connected to the internet
                // connected to the mobile provider's data plan
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true;
                } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }

    private void populateUserDetails() {

        try{
            phoneNumber = userPref.getString(getString(R.string.p_userphone),"");

                        gradeNumber = userPref.getInt(getString(R.string.p_grade),2);
                        boardNumber = userPref.getInt(getString(R.string.p_board),2);

                        firstName = userPref.getString(getString(R.string.p_firstname),"");
                        lastName = userPref.getString(getString(R.string.p_lastname),"");
                        userId = userPref.getString(getString(R.string.p_userid),"");

                        gradeSpinner.setAdapter(gradeAdapter);
                        gradeSpinner.setSelection(gradeNumber-1);

//                        Toast.makeText(getApplicationContext(),"Grade number: "+gradeNumber,Toast.LENGTH_SHORT).show();

                        if (gradeNumber>=1 && gradeNumber<=6) {

//                            Toast.makeText(getApplicationContext(),"School",Toast.LENGTH_LONG).show();

                            boardLabel.setText("Board");
                            findViewById(R.id.collegeDegreeAndYearLL).setVisibility(View.GONE);

                            if (gradeNumber==5 || gradeNumber==6) {
                                compExams.setVisibility(View.VISIBLE);
                                tempCE = userPref.getBoolean(getString(R.string.p_competitive),false);
//                                Toast.makeText(getApplicationContext(),"Competitive exam: "+tempCE,Toast.LENGTH_SHORT).show();
                                compExams.setChecked(tempCE);
                            }
                            else {
                                compExams.setVisibility(View.GONE);
                            }

                            boardSpinner.setAdapter(boardAdapter);
                            boardSpinner.setSelection(boardNumber-1);
                        }

                        else {

                            boardLabel.setText("Degree / course");

                            boardSpinner.setAdapter(degreeAdapter);
                            boardSpinner.setSelection(boardNumber-7);
                        }

                        fName.setText(firstName);
                        lName.setText(lastName);
                        uName.setText(userId);
                        phoneNo.setText(phoneNumber);


        }
        catch(Exception e){
            Log.e(TAG, "PopulateUserDetails method failed with  ",e);
        }
    }

    private void performCrop(String picUri) {
        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri

            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, RESULT_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
