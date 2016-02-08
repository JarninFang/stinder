package com.parse.starter;

/**
 * Created by Ken on 2/1/2016.
 */



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.support.v4.view.GestureDetectorCompat;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

public class MatchActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private GestureDetectorCompat gestureDetector;
    private Button likeButton;
    private Button dislikeButton;
    private TextView name;
    private TextView classes;
    private ImageView profilePicture;
    private int counter;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private int[] nameID;
    private int[] photoID;
    private int[] classesID;

    protected void onCreate(Bundle savedInstanceState) {
        //Set Content View?

        super.onCreate(savedInstanceState);
        setContentView(R.layout.matching);
        Intent intent = getIntent();
        counter = 0;
        counter = intent.getIntExtra("currentCount", 0);

        if(counter >= 2 ) {
            Intent nextIntent = new Intent(getApplicationContext(), BlankActivity.class);
            startActivity(nextIntent);
        }
        nameID = new int[]{R.id.nameText1, R.id.nameText2, R.id.nameText3};
        photoID = new int[]{R.id.profilePicture1, R.id.profilePicture2, R.id.profilePicture3};
        classesID = new int[]{R.id.classesText1, R.id.classesText2, R.id.classesText3};

        name = (TextView) findViewById(nameID[counter]);
        classes = (TextView) findViewById(classesID[counter]);
        profilePicture = (ImageView) findViewById(photoID[counter]);
        name.setVisibility(View.VISIBLE);
        classes.setVisibility(View.VISIBLE);
        profilePicture.setVisibility(View.VISIBLE);
        //Set up a method to update basic profile info: name, classes, image for each new profile
        likeButton = (Button) findViewById(R.id.newButton);
        dislikeButton = (Button) findViewById(R.id.dislikeButton);
        gestureDetector = new GestureDetectorCompat(this, this);


        //TODO: Possibly split this Match class into two fragments: Bottom Layer: Has Profiles, Top Layer: Has Button/Swipe Function

        likeButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //Do something when button is clicked
                        //name.setText("Testing1");
                        //likeButton.setText("Yay");
                        Intent nextIntent = new Intent(getApplicationContext(), MatchActivity.class );

                        nameID = null;
                        classesID = null;
                        photoID = null;

                        name.setVisibility(View.INVISIBLE);
                        classes.setVisibility(View.INVISIBLE);
                        profilePicture.setVisibility(View.INVISIBLE);
                        //WOW I VIOLATED DRY PRINCIPLE IMMA FAIL CS110
                        nextIntent.putExtra("currentCount", counter+1);
                        startActivity(nextIntent);
                        //TODO: Provide an indicator that the displayed person got liked
                        //ToDO: Send information that the current user is interested in current displayed person
                        //ToDO: Figure out data structure to hold profiles, are we going to just add to end of
                        //TODO: data structure each time? Or are we able to delete viewable profiles?
                    }
                }
        );

        dislikeButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View w) {
                        //Do something when button is clicked
                        //name.setText("Testing2");
                        //dislikeButton.setText("Yay");
                        Intent nextIntent = new Intent(getApplicationContext(), MatchActivity.class );

                        nameID = null;
                        classesID = null;
                        photoID = null;

                        name.setVisibility(View.INVISIBLE);
                        classes.setVisibility(View.INVISIBLE);
                        profilePicture.setVisibility(View.INVISIBLE);

                        nextIntent.putExtra("currentCount", counter+1);
                        startActivity(nextIntent);
                        //TODO: Provide an indicator that the displayed person got disliked
                    }
                }
        );




        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();
        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight();
                } else {
                    onSwipeLeft();
                }
                result = true;
            }
        }
        return result;
    }

    public void onSwipeRight() {
         // name.setText("Testing");
        Intent nextIntent = new Intent(getApplicationContext(), MatchActivity.class );

        nameID = null;
        classesID = null;
        photoID = null;

        name.setVisibility(View.INVISIBLE);
        classes.setVisibility(View.INVISIBLE);
        profilePicture.setVisibility(View.INVISIBLE);

        nextIntent.putExtra("currentCount", counter+1);
        startActivity(nextIntent);
    }

    public void onSwipeLeft() {
        Intent nextIntent = new Intent(getApplicationContext(), MatchActivity.class );

        nameID = null;
        classesID = null;
        photoID = null;

        name.setVisibility(View.INVISIBLE);
        classes.setVisibility(View.INVISIBLE);
        profilePicture.setVisibility(View.INVISIBLE);

        nextIntent.putExtra("currentCount", counter+1);
        startActivity(nextIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Match Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.parse.starter/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Match Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.parse.starter/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
