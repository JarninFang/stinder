package com.parse.starter;

/**
 * Created by Ken on 2/1/2016.
 */


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MatchActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private GestureDetectorCompat gestureDetector;
    private Button likeButton;
    private Button dislikeButton;
    private ImageView backButton;
    private ImageView messageButton;
    private TextView name;
    private TextView classes;
    private ParseImageView profilePicture;
    private ImageView nonParsePic;
    private ParseFile uploadedPic;
    private int counter;
    private ParseObject[] profiles;
    private int[] matchedClasses;
    private ParseObject[] filteredProfiles;
    private ParseObject[] shortFilteredProfiles;
    private int[] visibility;
    private int profileCounter = 0;
    private int infinityCheck = 0;
    private List<ParseObject> matchedList = new ArrayList<ParseObject>();
    ParseUser user;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    protected void onCreate(Bundle savedInstanceState) {
        //Set Content View?

        super.onCreate(savedInstanceState);
        setContentView(R.layout.matching);
        Intent intent = getIntent();

        name = (TextView) findViewById(R.id.nameText);
        classes = (TextView) findViewById(R.id.classesList);
        user = ParseUser.getCurrentUser();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereNotEqualTo("objectId", user.getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
                                   @Override
                                   public void done(List<ParseObject> objects, ParseException e) {
           profiles = new ParseObject[objects.size()];
           matchedClasses = new int[objects.size()];
           filteredProfiles = new ParseObject[objects.size()];
           if (e == null && objects.size() > 0) {
               for (int i = 0; i < objects.size(); i++) {
                   profiles[i] = objects.get(i);
               }
           }

           counter = 0;
           //Loop over every user in the array
           while (counter < objects.size()) {
               int match = 0;
               int totalClasses = user.getInt("currentClass");
               //System.err.println(totalClasses);
               //For each user, loop over each of our classes to compare strings
               while (totalClasses > 0) {
                   //For each of our strings, loop over each of their strings to check
                   //for same classes
                   String currentClass = user.getString("class" + (totalClasses - 1));
                   int classesToCheck = profiles[counter].getInt("currentClass");
                   //System.err.println(currentClass);
                   for (int i = 0; i < classesToCheck; i++) {
                       String matchingClass = profiles[counter].getString("class" + ((classesToCheck - i) - 1));
                       //System.err.println("matchingClass =" + matchingClass);
                       if (currentClass.trim().equalsIgnoreCase(matchingClass.trim())) {
                           match++;
                       }

                   }
                   totalClasses--;
               }
               //After checking all their classes against all our classes, store in array
               matchedClasses[counter] = match;
               //System.err.println("match#" + match + " totalClasses = " + totalClasses );
               match = 0;
               counter++;
           }

           //Now we have the array of matched classes corresponding to the parse user array

           counter = 0;
           int filteredCounter = 0;
           int totalClasses = user.getInt("currentClass");
           //Loop to find users with 3 classes in common, then 2, then 1, etc.
           while (totalClasses > 0) {
               //Loop over every user to find their corresponding number
               for (int i = 0; i < objects.size(); i++) {
                   if (matchedClasses[i] == totalClasses) {
                       filteredProfiles[filteredCounter] = profiles[i];
                       filteredCounter++;
                   }
               }
               totalClasses--;
           }
           //Trims the array
           shortFilteredProfiles = new ParseObject[filteredCounter];
           System.arraycopy(filteredProfiles, 0, shortFilteredProfiles, 0, filteredCounter);

           //Now we have an array of filtered profiles
           visibility = new int[filteredCounter];
           //And a blank array of flags to be set
           for (int i = 0; i < filteredCounter; i++) {
               visibility[i] = 1; //All users are initially visible
           }

           int numMatched = 0;

           //Null check for matchedprofiles
           List<ParseObject> file = (List<ParseObject>) user.get("MatchedProfiles");
           if(file != null) {
               matchedList = (List<ParseObject>) user.get("MatchedProfiles");
               numMatched = matchedList.size();
               for (int i = 0; i < filteredCounter; i++) {
                   for (int k = 0; k < numMatched; k++) {
                       if (shortFilteredProfiles[i].getObjectId() == matchedList.get(k).getObjectId()) {
                           visibility[i] = 0;
                       }
                   }

               }
           }
           displayNextProfile();
           makeUIVisible();
       }
   }
        );
        //Set up a method to update basic profile info: name, classes, image for each new profile
        likeButton = (Button) findViewById(R.id.likeButton);
        dislikeButton = (Button) findViewById(R.id.dislikeButton);
        gestureDetector = new GestureDetectorCompat(this, this);
        backButton = (ImageView) findViewById(R.id.backButton);
        messageButton = (ImageView) findViewById(R.id.MessageButton);

        //TODO: Possibly split this Match class into two fragments: Bottom Layer: Has Profiles, Top Layer: Has Button/Swipe Function

        likeButton.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View v) {
              setMatch();
              displayNextProfile();
          }
                                      }
        );

        dislikeButton.setOnClickListener(new View.OnClickListener() {
                                             public void onClick(View w) {
             profileCounter++;
             displayNextProfile();
             //TODO: Provide an indicator that the displayed person got disliked
                }
            }
        );

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchActivity.this, PreProfileActivity.class);
                startActivity(intent);
            }
        });
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchActivity.this, MatchedPortfolio.class);
                startActivity(intent);
            }
        });
    }

    public void makeUIVisible() {
        likeButton.setVisibility(View.VISIBLE);
        dislikeButton.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);
        messageButton.setVisibility(View.VISIBLE);

    }

    public void displayNextProfile() {
        //If we are at the end of the list, go to beginning
        if( profileCounter >= visibility.length) {
            profileCounter = 0;
        }
        boolean matched = false;
        for(int i = 0; i< matchedClasses.length; i++) {
            if(matchedClasses[i] != 0) {
                matched = true;
                break;
            }
        }

        //If we check the whole list, go to blank page
        if(!matched || infinityCheck == visibility.length) {
            Intent intent = new Intent(this, BlankActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else {
            //If the next person is visible, display the person
            if( visibility[profileCounter] == 1 ) {
                //name.setVisibility(View.INVISIBLE);
                //classes.setVisibility(View.INVISIBLE);
                //System.err.println("profilecount" + profileCounter);
                //System.err.println(shortFilteredProfiles[profileCounter]);
                String linkedClasses = "";
                for( int i = 0; i < shortFilteredProfiles[profileCounter].getInt("currentClass"); i++ ) {
                    linkedClasses += shortFilteredProfiles[profileCounter].getString("class" + i);
                    linkedClasses += ", ";
                }
                //System.err.println( "linkedClasses is" + linkedClasses);
                String shortenedLinkedClasses = linkedClasses.substring(0, linkedClasses.length()-2);
                name.setText(shortFilteredProfiles[profileCounter].getString("Name") );
                classes.setText(shortenedLinkedClasses);
                name.setVisibility(View.VISIBLE);
                classes.setVisibility(View.VISIBLE);
                infinityCheck = 0;
    
                uploadedPic = shortFilteredProfiles[profileCounter].getParseFile("ProfPic");
                System.out.println("Am i null? " + (uploadedPic == null));
                if (uploadedPic != null) {
                    profilePicture = (ParseImageView) findViewById(R.id.profilepic);
                    profilePicture.setParseFile(uploadedPic);
                    profilePicture.loadInBackground(new GetDataCallback() {
                        public void done(byte[] data, ParseException e) {
                            System.out.println("yay it loaded");
                            // The image is loaded and displayed!
                        }
                    });
                }
                else {
                    nonParsePic = (ImageView) findViewById(R.id.profilepic);
                    nonParsePic.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_profpic));
                }
            }
            //If person is not visible, go to next person in list
            else{
                profileCounter++;
                infinityCheck++;
                displayNextProfile();
            }
        }
    }

    //Move a person to the matched list and set their flag to not visible
    public void setMatch() {
        visibility[profileCounter] = 0;
        matchedList.add(filteredProfiles[profileCounter]);

        user.add("MatchedProfiles", filteredProfiles[profileCounter]);
        user.saveInBackground();
    }

    public ParseObject[] getFilteredList() {
        return shortFilteredProfiles;
    }

    public int getProfileCounter() {
        return profileCounter;
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
        setMatch();
        displayNextProfile();
    }

    public void onSwipeLeft() {
        profileCounter++;
        displayNextProfile();
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
        if (id == R.id.action_logout) {
            stopService(new Intent(getApplicationContext(), MessageService.class));
            ParseUser.logOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
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
