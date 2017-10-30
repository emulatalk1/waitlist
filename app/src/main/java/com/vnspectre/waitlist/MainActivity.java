package com.vnspectre.waitlist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.vnspectre.waitlist.data.TestUtil;
import com.vnspectre.waitlist.data.WaitlistContract;
import com.vnspectre.waitlist.data.WaitlistDbHelper;

import static com.vnspectre.waitlist.data.WaitlistContract.*;
import static com.vnspectre.waitlist.data.WaitlistContract.WaitlistEntry.*;

public class MainActivity extends AppCompatActivity {

    private GuestListAdapter mAdapter;

    private SQLiteDatabase mDb;

    private EditText mNewGuestNameEditText;
    private EditText mNewPartySizeEditText;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        //TestUtil.insertFakeData(mDb);

        RecyclerView waitlistRecyclerView;

        mNewGuestNameEditText = findViewById(R.id.person_name_edit_text);
        mNewPartySizeEditText = findViewById(R.id.party_count_edit_text);

        // Set local attributes to corresponding views.
        waitlistRecyclerView = this.findViewById(R.id.all_guests_list_view);

        // Set layout for the RecyclerView.
        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = getAllGuests();

        // Create an adapter for that cursor to display the data
        mAdapter = new GuestListAdapter(this, cursor);

        waitlistRecyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long id = (long) viewHolder.itemView.getTag();
                removeGuest(id);
                mAdapter.swapCursor(getAllGuests());
            }
        }).attachToRecyclerView(waitlistRecyclerView);
    }

    public void addToWaitlist(View view) {

        if (mNewPartySizeEditText.getText().length() == 0 || mNewGuestNameEditText.getText().length() == 0) {
            return;
        }

        int partySize = 1;
        try {
            partySize = Integer.parseInt(mNewPartySizeEditText.getText().toString());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to parse party size text to number: " + e.getMessage());
        }

        addGuest(mNewGuestNameEditText.getText().toString(), partySize);

        mAdapter.swapCursor(getAllGuests());

        mNewGuestNameEditText.getText().clear();
        mNewPartySizeEditText.getText().clear();

    }

    private Cursor getAllGuests() {
        return mDb.query(TABLE_NAME, null, null, null, null, null, COLUMN_TIMESTAMP);
    }

    public long addGuest(String name, int partySize) {
        ContentValues cv = new ContentValues();
        cv.put(WaitlistEntry.COLUMN_GUEST_NAME, name);
        cv.put(WaitlistEntry.COLUMN_PARTY_SIZE, partySize);

        return mDb.insert(WaitlistEntry.TABLE_NAME, null, cv);
    }

    public boolean removeGuest(long id) {
        return mDb.delete(WaitlistEntry.TABLE_NAME, WaitlistEntry._ID + " = " + id, null) > 0;
    }

}
