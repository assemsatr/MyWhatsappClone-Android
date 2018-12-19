package com.assemalturifi.whatsappfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    //  Add member variables here:

    private AutoCompleteTextView emailView;
    private EditText passwordView;


    //To authenticate the user from FireBase Auth
    private FirebaseAuth auth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        upViews();


        //when the enter key is pressed
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 100 || actionId == EditorInfo.IME_NULL) {

                    //step12
                    attemptLogIn();
                    return true;
                }
                return false;
            }
        });


    }
    private void upViews() {
        emailView = findViewById(R.id.login_email);
        passwordView = findViewById(R.id.login_password);
        auth = FirebaseAuth.getInstance();
    }

    //step1
    // Executed when Register button pressed
    public void registerNewUser(View view){
        Intent intent=new Intent(this,com.assemalturifi.whatsappfirebase.RegisterActivity.class);
        finish();
        startActivity(intent);


    }

    // Executed when Sign in button pressed
    public void signInExistingUser(View view) {

        //step 15, the next step in MainChatActivity
        attemptLogIn();

    }

    //step11
    private void attemptLogIn() {
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        if (email.equals("") || password.equals("")) {
            //we dont continue to log in
            return;
        }
        else{
            Toast.makeText(getApplicationContext(),"Login in Progress...",Toast.LENGTH_SHORT).show();

            //returns a TAsk Object, we are adding the addOnCompleteListener so we can see if the user has been signed in
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("whatsaa", "witnInWithEmail() onComplete: " + task.isSuccessful());

                    //this is triggered when the task is not successful
                    if (!task.isSuccessful()) {
                        Log.d("whatsaa", "Problem signing in:  " + task.getException());

                        //step14

                        showErrorDialog("There was a problem signing in!");

                    }
                    else{
                        Intent inten = new Intent(LoginActivity.this, MainChatActivity.class);
                        finish();
                        startActivity(inten);
                    }

                }

            });

        }

    }
    //step13
    private void showErrorDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }


}