package com.vnspectre.waitlist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.vnspectre.waitlist.data.TestUtil;
import com.vnspectre.waitlist.data.WaitlistContract;
import com.vnspectre.waitlist.data.WaitlistDbHelper;

import static com.vnspectre.waitlist.data.WaitlistContract.*;
import static com.vnspectre.waitlist.data.WaitlistContract.WaitlistEntry.*;

public class MainActivity extends AppCompatActivity {

    private GuestListAdapter mAdapter;

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        TestUtil.insertFakeData(mDb);

        RecyclerView waitlistRecyclerView;

        // Set local attributes to corresponding views.
        waitlistRecyclerView = this.findViewById(R.id.all_guests_list_view);

        // Set layout for the RecyclerView.
        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = getAllGuests();

        // Create an adapter for that cursor to display the data
        mAdapter = new GuestListAdapter(this, cursor.getCount());

        waitlistRecyclerView.setAdapter(mAdapter);
    }

    public void addToWaitlist(View view) {

    }

    private Cursor getAllGuests() {
        return mDb.query(TABLE_NAME, null, null, null, null, null, COLUMN_TIMESTAMP);
    }

}
