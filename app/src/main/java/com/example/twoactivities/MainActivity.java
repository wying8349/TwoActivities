package com.example.twoactivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final int TEXT_REQUEST = 1;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_MESSAGE =
            "com.example.twoactivities.extra.MESSAGE";
    private static final String TEXT_STATE = "currentText";

    private EditText mMessageEditText;
    private EditText mUsernameEditText;
    private TextView mReplyHeadTextView;
    private TextView mReplyTextView;
    private TextView mTextViewMainStart;
    private EditText mWebsiteEditText;
    private EditText mLocationEditText;
    private EditText mShareTextEditText;

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openHelper = new DatabaseHelper(this);

        mMessageEditText = findViewById(R.id.editText_main);
        mUsernameEditText = findViewById(R.id.editText_main_username);
        mReplyHeadTextView = findViewById(R.id.text_header_reply);
        mReplyTextView = findViewById(R.id.text_message_reply);
        mTextViewMainStart = findViewById(R.id.textView_main_start);
        if(savedInstanceState!=null){
            mTextViewMainStart.setText(savedInstanceState.getString(TEXT_STATE));
        }
        mWebsiteEditText = findViewById(R.id.website_edittext);
        mLocationEditText = findViewById(R.id.location_edittext);
        mShareTextEditText = findViewById(R.id.share_edittext);

    }

    public void launchSecondActivity(View view) {
        db = openHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Button clicked!");
        Intent intent = new Intent(this, SecondActivity.class);
        String message = mMessageEditText.getText().toString();
        String uname = mUsernameEditText.getText().toString().trim();

        if (message.isEmpty() || uname.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
        } else {
            insertData(uname,message);
            Log.d(LOG_TAG,"Data Inserted!");
            startActivity(intent);
            Log.d(LOG_TAG,"activity Started!");
            Toast.makeText(MainActivity.this, "Message Received", Toast.LENGTH_SHORT).show();
        }
    }

    public void insertData(String uname,String message){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COL_2,uname);
        contentValues.put(DatabaseHelper.COL_3,message);
        long id = db.insert(DatabaseHelper.TABLE_NAME,null,contentValues);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == TEXT_REQUEST) {
            if (resultCode == RESULT_OK) {
                String reply = data.getStringExtra(SecondActivity.EXTRA_REPLY);
                mReplyHeadTextView.setVisibility(View.VISIBLE);
                mReplyTextView.setText(reply);
                mReplyTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void startTask(View view) {
        mTextViewMainStart.setText(R.string.napping);
        new SimpleAsyncTask(mTextViewMainStart).execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the state of the TextView
        outState.putString(TEXT_STATE,
                mTextViewMainStart.getText().toString());
    }

    public void openWebsite(View view) {
        String url = mWebsiteEditText.getText().toString();
        // Parse the URI and create the intent.
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        // Find an activity to hand the intent and start that activity.
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d("ImplicitIntents", "Can't handle this intent!");
        }
    }

    public void openLocation(View view) {
        // Get the string indicating a location. Input is not validated; it is
        // passed to the location handler intact.
        String loc = mLocationEditText.getText().toString();
        // Parse the location and create the intent.
        Uri addressUri = Uri.parse("geo:0,0?q=" + loc);
        Intent intent = new Intent(Intent.ACTION_VIEW, addressUri);
        // Find an activity to handle the intent, and start that activity.
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d("ImplicitIntents", "Can't handle this intent!");
        }
    }

    public void shareText(View view) {
        String txt = mShareTextEditText.getText().toString();
        String mimeType = "text/plain";
        ShareCompat.IntentBuilder
                    .from(this)
                    .setType(mimeType)
                    .setChooserTitle(R.string.share_text_with)
                    .setText(txt)
                    .startChooser();
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            String uri_string = getString(R.string.uri_label)
                    + uri.toString();
            TextView textView = findViewById(R.id.text_uri_message);
            textView.setText(uri_string);
        }
    }
}
