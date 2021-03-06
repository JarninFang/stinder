package com.parse.starter;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashleyzhao on 2/28/16.
 */
public class MatchedPortfolio extends AppCompatActivity {
    private int MAX_PROFILES = 5;

    int numMatched;

    private ParseFile uploadedPic;

    private Button backButton;
    private Button logoutButton;

    //Created image and button arrays
    Button[] profiles = new Button[MAX_PROFILES];
    ParseImageView[] pictures = new ParseImageView[MAX_PROFILES];
    ImageView[] nonPictures = new ImageView[MAX_PROFILES];

    String[] objectId;// = new String[MAX_PROFILES];
    String ID;
    private String currentUserId;
    private ArrayAdapter<String> namesArrayAdapter;
    private ArrayList<String> names;
    private ListView usersListView;
    private ProgressDialog progressDialog;
    private BroadcastReceiver receiver = null;

    List<ParseObject> matchedList = new ArrayList<ParseObject>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        showSpinner();

        //Back Button for before viewing profile
        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchedPortfolio.this, PreProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    //display clickable a list of all users
    private void setMatchedList() {
        ParseUser user = ParseUser.getCurrentUser();
        currentUserId = ParseUser.getCurrentUser().getObjectId();
        names = new ArrayList<String>();
        matchedList = (List<ParseObject>) user.get("MatchedProfiles");
        objectId = new String[matchedList.size()];
        for (int i = 0; i < matchedList.size(); i++) {
            try {
                ParseQuery<ParseObject> myQuery = ParseQuery.getQuery("_User");
                myQuery.whereEqualTo("objectId", matchedList.get(i).getObjectId());
                names.add(myQuery.getFirst().getString("Name"));
                objectId[i] = matchedList.get(i).getObjectId();
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }


        usersListView = (ListView) findViewById(R.id.usersListView);
        namesArrayAdapter =
                new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.user_list_item, names);
        usersListView.setAdapter(namesArrayAdapter);

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                updateViewProfile(i);
            }
        });
        //this.setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_list_users, R.id.name, names));
    }

    public void updateViewProfile(int j) {

        //Obtain the object Id from MatchedProfile List
        ID = objectId[j];

        //Switching to view only profile
        Intent intent = new Intent(MatchedPortfolio.this, ViewActivity.class);
        intent.putExtra("ID", ID);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Matched Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.parse.starter/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);*/
    }

    //show a loading spinner while the sinch client starts
    private void showSpinner() {
        /*progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        */
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean success = intent.getBooleanExtra("success", false);
                //progressDialog.dismiss();
                if (!success) {
                    Toast.makeText(getApplicationContext(), "Messaging service failed to start", Toast.LENGTH_LONG).show();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("com.parse.starter.ViewActivity"));
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
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Matched Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.parse.starter/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();*/
    }

    @Override
    public void onResume() {
        setMatchedList();
        super.onResume();
    }
}
