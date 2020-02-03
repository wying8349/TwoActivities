package com.example.twoactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final String EXTRA_REPLY = "com.example.twoactivities.extra.REPLY";
    private EditText mReply;
    private EditText mUname;

    private SQLiteDatabase db;
    private SQLiteOpenHelper openHelper;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        openHelper = new DatabaseHelper(this);
        db = openHelper.getReadableDatabase();

        mReply = findViewById(R.id.editText_second);
        Intent intent = getIntent();
    }


    public void returnReply(View view) {
        String reply = mReply.getText().toString();
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, reply);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    public void LookForName(View view) {
        Log.d(LOG_TAG, "button query clicked");
        mUname = findViewById(R.id.editText_2_username);
        String uname = mUname.getText().toString();
        Log.d(LOG_TAG, "uname = " + uname);
        TextView textView = findViewById(R.id.text_message);
        cursor = db.rawQuery("SELECT *FROM " + DatabaseHelper.TABLE_NAME + " WHERE " + DatabaseHelper.COL_2 + "=? ", new String[]{uname});
        Log.d(LOG_TAG, cursor.toString());
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                Log.d(LOG_TAG, "have result");
                    do {
                        textView.setText("Cursor to message: "+cursor.toString());
                    } while (cursor.moveToNext());
                }
                //textView.setText(cursor.getString(cursor.getColumnIndex("uName")));
            else {
                Toast.makeText(getApplicationContext(), "No result", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
