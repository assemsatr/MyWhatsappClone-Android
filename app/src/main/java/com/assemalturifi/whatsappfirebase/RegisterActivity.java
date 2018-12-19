package com.assemalturifi.whatsappfirebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity {

    // Constants
    static final String CHAT_PREFS = "ChatPrefs";
    static final String DISPLAY_NAME_KEY = "username";


    // UI references.
    private AutoCompleteTextView emailView;
    private AutoCompleteTextView usernameView;
    private EditText passwordView;
    private EditText confirmPasswordView;

    //step4
    // Firebase instance variables
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bindViews();


        // Keyboard sign in action,//when the enter key is pressed
        confirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.register_form_finished || id == EditorInfo.IME_NULL) {

                    return true;
                }
                return false;
            }
        });




    }

    public void bindViews() {
        emailView = findViewById(R.id.register_email);
        passwordView = findViewById(R.id.register_password);
        confirmPasswordView = findViewById(R.id.register_confirm_password);
        usernameView = findViewById(R.id.register_username);


    }




    //step2
    private boolean isEmailValid(String email){
        return email.contains("@");
    }
    //step3
    private boolean isPasswordValid(String password){
        String confirmPassword = confirmPasswordView.getText().toString();

        return confirmPassword.equals(password) && password.length() > 4;

    }

    private void attemptRegistration() {
        // Reset errors displayed in the form.
        emailView.setError(null);
        passwordView.setError(null);


        // Store values at the time of the login attempt.
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //step6
            //  Call create FirebaseUser() here
            createFireBaseUser();

        }

    }
    //step5
    //Creating a FireBase user
    private void createFireBaseUser(){
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        //returns on object of type task, im using this task to triger an event,
        // namely if creating the firebase user was successful
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("whatsapp", "createUser on Firebase onComplete: " + task.isSuccessful());

                if(!task.isSuccessful()){
                    Log.d("whatsapp", "user creating in Firebase failed: " );

                    //step8
                    showErrorDialog("Registration attempt failed!");
                }
                //step10,the next step in LoginActivity
                else{
                    saveUserName();

                    //directing the user to the log in page
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });

    }
    //  step9
    private void saveUserName() {
        String userName = usernameView.getText().toString();
        //creating shared pref to sava user name locally on the database cloud
        SharedPreferences prefs = getSharedPreferences(CHAT_PREFS, 0);

        //informating sharedfrefs that the data saved will be edited
        // with prefs.edit(), it is ready to accept some data
        //with prefs.edit().putString(), this method will actually edits this data
        //with apply, adds the data, and saves the information
        prefs.edit().putString(DISPLAY_NAME_KEY,userName).apply();

    }


    //when sign up btn clicked
    public void signUp(View view) {

    }
    //step7
    private void showErrorDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
}