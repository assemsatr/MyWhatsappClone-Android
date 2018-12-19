package com.assemalturifi.whatsappfirebase;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainChatActivity extends AppCompatActivity {

    //   member variables here:
    private ImageButton sendBtn;
    private EditText inputText;
    private ListView chatList;

    private String displayName;
    // in order to talk to the Firebase database we need databaserefrence
    private DatabaseReference databaseReference;

    //step42
    private ChatListAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);


        bindViews();

        //step17
        setUpUserName();

        //step18
        // in order to talk to the Firebase database we need this
        //to get a Firebase database object
        //and this i wil be storing in databaseRefrence member veriable
        //this will represent a particular location in our cloud database
        //this database reference is used for reading and writing data to that location in the database
        databaseReference=FirebaseDatabase.getInstance().getReference();

        //step19
        //this is for the user when the actually send the chat messages when they use the app
        //users write whatever they want to editText, then they press to the imageBtn,
        //we want to save the chat messages from the input to the fireBase dataBase
        //to implement this functionality, we need to listen for an event from the editTex
        //setting  onEditor listener on the inputtext that
        // fires the sendBtn method when the enterbtn is pressed on the soft keyboard
        //Also set onClickListener on the sendBtn so that sendMessage method is fired whe the btn is clicked

        //step20
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                sendMessage();
                return true;
            }
        });

        //step21, the next step in InstantMessage class
        //now we've got a refrence to our database and have the UI working and now we can concentrate on creating
        //a model class that will represent an individual chat message
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });




    }

    //step23, the next step is in ChatListAdapter class
    private void sendMessage() {
        Log.d("whatsapp", "I sent something");
        //  Grab the text the user typed in and push the message to Firebase

        String input = inputText.getText().toString();

        //if the user message chat is not empty
        if(!input.equals("")){
            InstantMessage chat = new InstantMessage(input, displayName);

            //here we are finally saving the first chat message to the cloud and on firebase
            //this is where the firebase database refrence object comes into play
            //remember, dataBaseeRefrence is a particular location in the database.
            //here we use the database reference chil() to specify that all our chat messages are to be stored
            //in a place called messages
            //next we use the push() to get a reference to this child location.
            //finally we call setValue() to actually write the data in our chat object to the database
            databaseReference.child("messages").push().setValue(chat);

            //here we are resetting the input, after the user clicked on send
            inputText.setText("");
        }

    }
    private void bindViews() {
        chatList = findViewById(R.id.chat_list_view);
        inputText = findViewById(R.id.messageInput);
        sendBtn = findViewById(R.id.sendButton);
    }

    //step16
    //retriving the data that we saved locally on the device, namely the userName saved under sharedPrefrences
    private void setUpUserName() {

        //we got out sharedPrefrences object
        SharedPreferences prefs = getSharedPreferences(RegisterActivity.CHAT_PREFS,MODE_PRIVATE);

        //retriving the data, im storing the info that im pulling out of the Shared Prefrences in DiplayName

        displayName = prefs.getString(RegisterActivity.DISPLAY_NAME_KEY, null);

        if (displayName == null) {
            displayName = "Anonymous";
        }

    }

    //step43
    @Override
    protected void onStart() {
        super.onStart();
                                            //this here is the activity
        adapter = new ChatListAdapter(this, databaseReference, displayName);
        chatList.setAdapter(adapter);
    }

    //step44
    // we have to free up resources in the Android lifecycle when the whatsapp app is no longer needed and
    // freeing up resources involves telling the adopter to stop checking for updates on the firebase database

    //next step is in chatListAdapter
    @Override
    protected void onStop() {
        super.onStop();

        adapter.cleanup();
    }


}
