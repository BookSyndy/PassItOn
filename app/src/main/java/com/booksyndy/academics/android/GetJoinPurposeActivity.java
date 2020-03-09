package com.booksyndy.academics.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.booksyndy.academics.android.Data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GetJoinPurposeActivity extends AppCompatActivity {

    private static final String default_pic_url = "https://firebasestorage.googleapis.com/v0/b/booksyndy-e8ef6.appspot.com/o/default_photos%2Fdefault_user_dp.png?alt=media&token=23b43df7-8143-4ad7-bb87-51e49da095c6";

    private boolean isParent, competitiveExam;
    private TextView reasonsQuestion;
    private RadioGroup reasons;
    private String firstName, lastName, username, phoneNumber, date;
    private int gradeNumber, boardNumber, yearNumber;
    private Intent startMainActivity;
    private User curFirebaseUser;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private int userType;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_join_purpose);
        getSupportActionBar().setTitle("Sign up");
        reasonsQuestion = findViewById(R.id.reasonQuestionTV);
        reasons = findViewById(R.id.reasonsButtonList);

        registerUser();
//        startMainActivity.putExtra("IS_PARENT", isParent);
//        startMainActivity.putExtra("FIRST_NAME",firstName);
//        startMainActivity.putExtra("LAST_NAME",lastName);
//        startMainActivity.putExtra("GRADE_NUMBER",gradeNumber);
/*        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab7);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // This is the last activity in which we ask the user for information.
                // After this, on clicking the next button, the user account creation is completed and the user is taken
                // to the main activity.
                // Firebase implementation is required here.
                reason = reasons.getCheckedRadioButtonId();
                if (reason == -1) {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Please select an option", Snackbar.LENGTH_SHORT)
                            .setAction("OKAY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();
                } else if (reason == R.id.toSell) {
                    toSell = true;
                } else if (reason == R.id.toBuy) {
                    toSell = false;
                }
//                 put firebase-related code here
                registerUser();
            }
        });*/
    }

    private void registerUser() {
        try {

            calendar = Calendar.getInstance();
            dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            date = dateFormat.format(calendar.getTime());

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Finishing up");
            progressDialog.setTitle("Just a sec...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            competitiveExam = getIntent().getBooleanExtra("COMPETITIVE_EXAM", false);
            isParent = getIntent().getBooleanExtra("IS_PARENT", false);
            firstName = getIntent().getStringExtra("FIRST_NAME");
            lastName = getIntent().getStringExtra("LAST_NAME");
            gradeNumber = getIntent().getIntExtra("GRADE_NUMBER", 4);
            boardNumber = getIntent().getIntExtra("BOARD_NUMBER", 6);
            boardNumber = getIntent().getIntExtra("DEGREE_NUMBER", boardNumber);
            yearNumber = getIntent().getIntExtra("YEAR_NUMBER", 0);
            username = getIntent().getStringExtra("USERNAME");
            userType = getIntent().getIntExtra("USER_TYPE",1);

            if (gradeNumber<3 || gradeNumber>6) {
                competitiveExam=false;
            }
            phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            curFirebaseUser = new User(firstName, lastName, phoneNumber, isParent, gradeNumber, boardNumber,competitiveExam, username, default_pic_url);
            curFirebaseUser.setYear(yearNumber);
            curFirebaseUser.setUserType(userType);
            curFirebaseUser.setCreationDate(date);
            db = FirebaseFirestore.getInstance();

            // Add a new document with a generated ID
            db.collection("users").document(phoneNumber)
                    .set(curFirebaseUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
//                    Toast.makeText(getApplicationContext(), "User Registered Successfully " + phoneNumber, Toast.LENGTH_LONG).show();
                    // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    saveToken(curFirebaseUser.getPhone());
                    startMain();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    //Log.w(TAG, "Error adding document", e);
                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "User Register Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void saveToken(final String userId) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        FirebaseFirestore.getInstance().collection("users").document(userId).update("token",token);
                    }
                });
    }


    private void startMain(){
        startMainActivity = new Intent(GetJoinPurposeActivity.this, MainActivity.class);
        startMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startMainActivity.putExtra("SNACKBAR_MSG","Hey there! Thanks for signing up!");
        startActivity(startMainActivity);
        finish();
    }
}
