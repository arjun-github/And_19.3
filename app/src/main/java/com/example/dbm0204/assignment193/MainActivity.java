package com.example.dbm0204.assignment193;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The MainActivity extends AppCompactActivity and implements TextWatcher, AdapterView.onItemClickListener
 *
 */
public class MainActivity extends AppCompatActivity implements TextWatcher, AdapterView.OnItemClickListener, View.OnClickListener {
    EditText etName,etPhone,etSearch;
    Button btnDelete;
    ListView lvContacts;
    ContentResolver resolver;
    SimpleCursorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resolver=getContentResolver();
        initializeControls();
        displayContacts();
        etSearch.requestFocus();
        etSearch.addTextChangedListener(this);
        lvContacts.setOnItemClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    private void initializeControls() {
        etName=(EditText) findViewById(R.id.etName);
        etPhone=(EditText) findViewById(R.id.etPhone);
        btnDelete=(Button) findViewById(R.id.btnDelete);
        etSearch = (EditText) findViewById(R.id.etSearch);
        lvContacts=(ListView) findViewById(R.id.lvContacts);
    }

    private void displaySearchContact()
    {
        String mSortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" asc";
        Cursor mCursor = null;

        try {
            String searchName= etSearch.getText().toString();

            String[] mColumns = new String[]
                    {
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone._ID,

                    };

            String mSelectionClause =ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like ?" ;

            String[] mSelectionArgs ={searchName+"%"};

            mCursor = resolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,   // The content URI of the contact table
                    mColumns,
                    mSelectionClause,
                    mSelectionArgs,
                    mSortOrder);

            String [] from={ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER};
            int [] to={R.id.tvContactName,R.id.tvContactPhone};
            adapter=new SimpleCursorAdapter(getApplicationContext(),R.layout.listview_design,
                    mCursor,new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER},to);
            adapter.setViewBinder(new CustomAdapter());

            lvContacts.setAdapter(adapter);

        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();

        }

    }

    private void displayContacts() {

        String mSortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" asc";
        Cursor mCursor = null;

        try {

            String[] mColumns = new String[]
                    {
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone._ID,

                    };

            String mSelectionClause = null;

            String[] mSelectionArgs = null;

            mCursor = resolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,   // The content URI of the contact table
                    mColumns,
                    mSelectionClause,
                    mSelectionArgs,
                    mSortOrder);

            String [] from={ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER};
            int [] to={R.id.tvContactName,R.id.tvContactPhone};
            adapter=new SimpleCursorAdapter(getApplicationContext(),R.layout.listview_design,
                    mCursor,from,to);
            adapter.setViewBinder(new CustomAdapter());

            lvContacts.setAdapter(adapter);

        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        displaySearchContact();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
    String name,phoneNo;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        try {
            TextView displayName = (TextView) view.findViewById(R.id.tvContactName);
            TextView number = (TextView) view.findViewById(R.id.tvContactPhone);
            name = displayName.getText().toString();
            phoneNo = number.getText().toString();
            etName.setText(name);
            etPhone.setText(phoneNo);
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),ex.toString(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        try {

            String newDisplayName 	=  etName.getText().toString();
            String newNumber 	=  etPhone.getText().toString();

            deleteContact(getApplicationContext(),newNumber);

            etName.getText().clear();
            etPhone.getText().clear();
            displayContacts();
            Toast.makeText(getApplicationContext(),"Contact deleted.", Toast.LENGTH_LONG).show();


        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();


        }
    }
    // deletes contact from phonebook using phone number
    public static boolean deleteContact(Context ctx, String phoneNumber)
    {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cur = ctx.getContentResolver().query(contactUri, null, null,
                null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    String lookupKey =
                            cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)); //gets the key by number
                    Uri uri = Uri.withAppendedPath(
                            ContactsContract.Contacts.CONTENT_LOOKUP_URI,
                            lookupKey); // get the URI of phone book
                    ctx.getContentResolver().delete(uri, null, null);
                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return false;
    }


    private class CustomAdapter implements SimpleCursorAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            switch (view.getId())
            {
                case R.id.tvContactName:
                    TextView name=(TextView) view;
                    String displayName=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    name.setText(displayName);
                    break;
                case R.id.tvContactPhone:

                    TextView number=(TextView)view;
                    String phoneNo= cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    number.setText(phoneNo);
                    break;


            }

            return false;
        }
    }



}
