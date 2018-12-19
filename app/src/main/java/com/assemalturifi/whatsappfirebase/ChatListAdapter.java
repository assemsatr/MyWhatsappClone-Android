package com.assemalturifi.whatsappfirebase;

//in this class, i m gonna prepare the app that we can retrieve our messages from the fireBase and display them
// in the chat. But we first have to get to the grips(ustesinden gelmek) with how to display the messages.
//after we've done that i will write the code that queries the messages from the cloud database.
//ListView: it is job to display a list of scrollable items.  You use it to display information.
//Example: if you pull up the settings menu from android phone or iphone, then the UI component
// that you will be interacting with has a listView. In this case, the individual rows of the
// ListView are composed of (olusturmak) the settings option.
// And in terms of styling we see that occasionally there is a separator between the list view items, like between
// device category and the personal category. However for the most part we don’t see
// a separator and we actually have to tap on the road to make the individual cell visible.
// So the design of the style of an individual cell is actually separated from the list view component itself.
// So if you want to make an individual cell you have to create another layout. For example,
// remember the whatsapp you have done, for the main_chat_activity, you have another layout for the cell,
// called chat_msg_row to style itsel.
//But the styling of the individual rows isn’t the only thing that we have to get to grips with(ustesinden gelmek).
//  The listViews is actually a tricky beast because it doesn’t like to talk to the underlying data directly.
// In other words, we need a middleman between the listView and the chat message data.
//And that middleman is so-called adapter. The adapter will serve up the data for the individual row to be displayed In the listView.

// this class will have the job of providing the data to the listView
// first you have to extend(inherit) BaseAdapter. This will act as a bridge between the chat message
// data from fireBase and the ListView that needs to display the messages.

//BaseAdapter will act as the template(kaplanmis, seffaf evha) that we're going to build our ChatListAdapter on top of.

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

//step24
public class ChatListAdapter extends BaseAdapter {


    //step26, these member variables
    //activity for our activity
    private Activity mActivity;
    //database refrence
    private DatabaseReference mDatabaseReference;
    private String mDisplayName;

    //DataSnapshot is nothing other than a type used by fireBase for passing our data back to our app. Every time we
    //read from the cloud database, we receive our data in the form of a dataSnapshot
    private ArrayList<DataSnapshot> mSnapshotList;




    //step 35
    // after all, we will retrieve our list of chat messages from the fireBase and display it to the user.
    // to detect there a new chat message in firebases server we will have to use a listener
    // the listener that we will use firebases child event listener
    //this is the listener that we will get notified if there have been any changes to the database
    // for example, when someone sends a chat message and new data gets added to our database that qualifies as a change, and
    //our listener will report back
    private ChildEventListener mListener = new ChildEventListener() {


        //step 36
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            // the onChildAdded callback will get fired when a new chat message is added to the database and when
            //onChild added is triggered we will receive a data snapshot from firebase. The data snapshot actually
            // comes in the form of Jason and contains our chat messages data. Thats why we will use our ArrayList

            //step 37
            // we will add the data snapshot that we received through the callback to our collection of snapshots in the arrayList.
            //calling the add method appends(sonuna eklemek) a new item to the arrayList.
            mSnapshotList.add(dataSnapshot);

            //after each addition to the arrayList we need to notify the listView that it needs to refresh
            // itself because there is new information available and we do this by calling the notifydatasetChnaged().
            notifyDataSetChanged();
            // so now we're created our listener and we've written the instruction that should be executed in the callbacks, we need to
            //attach our listener to something and that something is our database reference.
            //the next step(38 is i the constructor)

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    //step27-constructor
    public ChatListAdapter(Activity activity, DatabaseReference ref, String name) {

        mActivity = activity;
        mDisplayName = name;
        // common error: typo in the db location. Needs to match what's in MainChatActivity.
        mDatabaseReference = ref.child("messages");

        //step 38
        // now we've written the code that retrieves the data from firebase, we still need to make sure
        // our adapter can provide the correct message data to the listView
        // in another words we have to get the relevant instant message out of the list of snapshots, we will do this in the getItem()
        mDatabaseReference.addChildEventListener(mListener);

        mSnapshotList = new ArrayList<>();
    }
    //step28
    //this is a subclass/inner class
    //remember how i set an individual row in the chat that will have its own layout,
    //this is a helper class, thats acts as a nice little package for an individual row
    //Viewholder will hold all the views in a single chat row
    private static class ViewHolder{
        TextView authorName;// for the auther name
        TextView body;// for the chat body
        //this is for styling our messages row programmatically at some point
        // this is to get the layout parameters
        LinearLayout.LayoutParams params;
    }

    //step25, all these @overwritten methods, these methods from the abstract class that we extended(BaseAdapter)
    @Override
    public int getCount() {
        ///step 40
        return mSnapshotList.size();
        //step41 is down the page
    }

    //step31
    //InstantMessage method was returning an Object type by default, i changed it to InstantMessage
    @Override
    public InstantMessage getItem(int position) {

        ///step 39
        DataSnapshot snapshot = mSnapshotList.get(position);
        return snapshot.getValue(InstantMessage.class);
        //getItem() now is returning an instant message object every time it gets called inside getView

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //step29
    // lets say we have a list of 50 itmes, the screen will only be big enough to display between say 5-7items at any gien time.
    //and as we scroll up and down this list one row will move off the screen while a new roll appears.
    // here is the problem, creating an individual row from scratch is expensive and it would be a crappy(berbat, rezil) user experience
    //if our phones started lagging(gecikme, geri kalma) as we're scrolling through the list
    // one way to avoid the lag is to load up the entire list into memory.
    //However, if we have a list of 1000 items we cannot possibly load all these rows at once, we need to use a trick
    //as soon as a row scrolls out of sight we need to keep hold of the views that make up that row.
    // and when a new row scrolls onto the screen we will supply that row with the views that we've used before
    //but will also populate(nufus artirmak,yerlestirmek) each view with new data. That means that when we're working with a list view
    //we got to be like a swedish recycling champion, refurbishing(yeniden dosemek,yenilemek) old  rows in our list and passing them off as
    //as brand new items to our users.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //here we are checking if there is an existing row that can be reused
        //convert view represents the list items, if convertview is empty we have to create a new row from the layoutFile,
        // and we need LayoutInflater for that, it will create a new view for us
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // inflate() will create a new view for us
            convertView = inflater.inflate(R.layout.chat_msg_row, parent, false);

            //remember, this is the inner helper class that will hold onto all the things that make up an individual chat message row
            final ViewHolder holder = new ViewHolder();
            holder.authorName = (TextView) convertView.findViewById(R.id.author);
            holder.body = (TextView) convertView.findViewById(R.id.message);
            //to get the layout parameters, we will call the Layoutparams method on the textView that we stored under the auther name.
            holder.params = (LinearLayout.LayoutParams) holder.authorName.getLayoutParams();

            //we need to give the adapter a way of storing our View holder for a short period of time  so we can reuse it later
            // using the Viewholder will allow us to avoid calling that findviewById again.
            //setTag() stores our View holder in the convertView.
            convertView.setTag(holder);

        }
        //step30

        //now we need to make sure that we're showing the correct message text and author in our listItem

        //We're holding of chatMessage at the position in the list, we are doing this by calling getItem()
        final InstantMessage message = getItem(position);
        //go step31

        //step32
        //this will reuse the viewHolder when we created a new ViewHolder we stored it inside the converter view with settag(),
        // now with getTag() we are getting it in order to retrieve the view Holder that temporarily saved  in the convertView
        final ViewHolder holder = (ViewHolder) convertView.getTag();

        //the setTag() and getTag() methods allow us to recycle our viewHolders for each row in the list.

//        boolean isMe = message.getAuthor().equals(mDisplayName);
//        setChatRowAppearance(isMe, holder);


         //step46
        //first lets determine if the author of the chat messages matches the displayname

        boolean isMe = message.getAuthor().equals(mDisplayName);
        setChatRowApperance(isMe,holder);




        //step 33
        //the viewHolder that we just fetched from the convertView() is
        // still going to have the old data in it from the previous time that itwas used. so we're going
        // to change that by replacing the stale data. we gotta retrieve the author for the current item in the list from the instant message
        String author = message.getAuthor();
        //and then set the text of the author name textview in the view holder with the new info
        holder.authorName.setText(author);

        //step 34
// and we will do the same with the chat message text
        String msg = message.getMessage();
        holder.body.setText(msg);
        // now when the user scrolls up and down the list, no new layout will have to be created unnecessarily


        //convertView represents listItems
        return convertView;
    }

    //step45
    // so till now, one thing you will notice is that even though i logged in with the same username, the sender
    //of these new chat messages is listed as anonymous and that's because the data saved under the shared preferences
    //doesnt survive a fresh install of the app
    // if we look at our setupDisplay() in mainChatActivity, we can see that if calling getString on our shared preferences
    //return null. our displayName is set to anonymous instead.

    //at the moment the whatsapp app appearance is not very flashy
    //the layout of our messages in our chat room doesnt differentiate between the user and their chat partners
    // this method will do all the stying of the chat messages for us
    private void setChatRowApperance (boolean isItMe, ViewHolder holder){

        //step47

        if (isItMe) {//if the messages belongs to the user
            holder.params.gravity = Gravity.END;
            holder.authorName.setTextColor(Color.GREEN);

        }
        else{
            holder.params.gravity = Gravity.START;
            holder.authorName.setTextColor(Color.BLUE);

        }

        holder.authorName.setLayoutParams(holder.params);
        holder.body.setLayoutParams(holder.params);

        holder.body.setBackgroundResource(R.drawable.bubble1);

    }


    ///step 41
/// this method is to stop checking for new events on the database
    //the reason we're going to create this method is so that we can free up resources when we dont need them anymore.
    // it removes the firebase evenListener
    //when the app leaves the foreground we can call to stop the adopter from checking for events from the firebase database
    // next step i mainChatACtivity
    void cleanup() {

        mDatabaseReference.removeEventListener(mListener);
    }


}
