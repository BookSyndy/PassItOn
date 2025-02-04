package com.booksyndy.academics.android;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.booksyndy.academics.android.Data.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CustNameActivity extends AppCompatActivity {

    private boolean isValidUsername, isAvailableUsername=true, phoneNumberPublic;
    private EditText firstNameField, lastNameField, userIdField;
    private TextInputLayout fnf,lnf,uif;
    private String firstName, lastName, username;
    private TextWatcher pUsername;

    private static String TAG = "CUSTNAMEACTIVITY";
    private FirebaseFirestore mFireStore;
    private List<String> userNamesList;
    private View parentLayout;
    private CheckBox pnpcb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_name);
        getSupportActionBar().setTitle("Sign up");

        parentLayout = findViewById(android.R.id.content);
//        isParent = getIntent().getBooleanExtra("IS_PARENT",false);
//        userType = getIntent().getIntExtra("USER_TYPE",1);

        userIdField = (EditText) findViewById(R.id.usernameField);
        firstNameField = (EditText) findViewById(R.id.firstName);
        lastNameField = (EditText) findViewById(R.id.lastName);
        uif = findViewById(R.id.usernameTIL);
        pnpcb = findViewById(R.id.publicPhoneCB);

        pnpcb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                phoneNumberPublic = isChecked;
            }
        });
        pUsername = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userIdField.setText((firstNameField.getText().toString().toLowerCase()+lastNameField.getText().toString().toLowerCase()).replaceAll("\\s", ""));
                uif.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        userIdField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                uif.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    lastNameField.requestFocus();
                }
                return false;
            }
        });
        lastNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    userIdField.requestFocus();
                }
                return false;
            }
        });

        firstNameField.addTextChangedListener(pUsername);
        lastNameField.addTextChangedListener(pUsername);

        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab4);
        userNamesList = new ArrayList<>();
        initFireStore();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),"Type: "+userType,Toast.LENGTH_SHORT).show();

                firstName = firstNameField.getText().toString().trim();
                lastName = lastNameField.getText().toString().trim();
                username = userIdField.getText().toString().trim().toLowerCase();
                phoneNumberPublic = pnpcb.isChecked();

                // todo: last char can't be a dot.
                isValidUsername = false;
                isValidUsername = (username != null) && username.matches("[A-Za-z0-9_.]+");
                isAvailableUsername = checkUserName(username);
                if (firstName.length()==0 || lastName.length()==0) {
                    showSnackbar("Please fill in both name fields");
                }

                else if (!(isValidUsername && isAvailableUsername)) {
                    if (!isValidUsername) {
                        uif.setError("The username you entered is not valid.");
                    }
                    else if (!isAvailableUsername) {
                        uif.setError("This username is taken. Please try another.");
                    }
                }

                else if (username.length()<4) {
                    uif.setError("This username is too short.");
                }

                else {

                    Intent getParOrStud = new Intent(CustNameActivity.this, GetModeActivity.class);
                    getParOrStud.putExtra("FIRST_NAME", firstName);
                    getParOrStud.putExtra("LAST_NAME", lastName);
                    getParOrStud.putExtra("USERNAME", username);
                    getParOrStud.putExtra("PUBLIC_PHONE",phoneNumberPublic);
                    startActivity(getParOrStud);
                }
            }
        });
    }

    private boolean checkUserName(String username) {
        if(username != null){
            for(String userId:userNamesList){
                if(userId.equalsIgnoreCase(username)){
                    return false;
                }
            }
        }
        return true;
    }

    private void initFireStore() {
        mFireStore = FirebaseFirestore.getInstance();
        mFireStore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.e(TAG, "onEvent: usernames fetch error",e );
                }
                if(queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){
                    for (User user:queryDocumentSnapshots.toObjects(User.class)){
                        userNamesList.add(user.getUserId());
                    }

                }

            }
        });
    }
    private void showSnackbar(String message) {
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT)
                .setAction("OKAY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }
}