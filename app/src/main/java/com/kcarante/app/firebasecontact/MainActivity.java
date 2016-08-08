package com.kcarante.app.firebasecontact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kcarante.app.firebasecontact.Adapter.ListAdapter;
import com.kcarante.app.firebasecontact.Constructor.Contacts;
import com.kcarante.app.firebasecontact.Fragment.DialogFragment;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private EditText mName;
    private EditText mContactNumber;
    private Button mAddContact;
    private Button mDeleteContact;
    private Button mCallContact;
    private Button mUpdateContact;

    private ListViewCompat mList;

    private DatabaseReference contactReference;
    private FirebaseDatabase contactDatabase;

    private Contacts temp = new Contacts();
    private ArrayList<Contacts> contactList;
    private ArrayList<Contacts> tempContactList;
    private ArrayList<Contacts> contactsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mName = (EditText) findViewById(R.id.name);
        mContactNumber = (EditText) findViewById(R.id.contactNumber);
        mAddContact = (Button) findViewById(R.id.addContact);
        mDeleteContact = (Button) findViewById(R.id.deleteContact);
        mCallContact = (Button) findViewById(R.id.callContact);
        mUpdateContact = (Button) findViewById(R.id.updateContact);
        mList = (ListViewCompat) findViewById(R.id.list);

        contactDatabase = FirebaseDatabase.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        contactReference = contactDatabase.getReference("Contacts");

        contactReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contactList = new ArrayList<>();

                Iterable<DataSnapshot> snapshotIterable = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterable.iterator();

                while(iterator.hasNext()) {
                    Contacts contacts = iterator.next().getValue(Contacts.class);
                    contactList.add(contacts);
                    contactsArrayList.add(contacts);

                    Toast.makeText(MainActivity.this,
                            "Contact " + contacts.getName() + " is viewed!",
                            Toast.LENGTH_SHORT).show();
                }
                    ListAdapter adapter = new ListAdapter(MainActivity.this, R.layout.list_view, contactList);
                    mList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mAddContact.setOnClickListener(new View.OnClickListener() {
            String name = mName.getText().toString();
            String number = mContactNumber.getText().toString();

            @Override
            public void onClick(View view) {
                temp = checkInputs();
                if(!(temp.getName().isEmpty())) {
                    if(!(temp.getContactNumber().isEmpty())){
                        tempContactList = findItem();
                        int counter = 0;
                        for(int i = 0; i < contactList.size(); i++){
                            if(contactList.get(i).getName().equals(name)){
                                if(contactList.get(i).getContactNumber().equals(number)){
                                    counter++;
                                }
                            }
                        }
                        if(counter == 0){
                            newContact(temp);
                        }
                        else {
                            mName.setText("");
                            mContactNumber.setText("");
                            Toast.makeText(MainActivity.this, "Contact already exists. Please try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Invalid input of name and contact number. Try again!", Toast.LENGTH_SHORT).show();
                    }
                }
        });
        mDeleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = checkInputs();
                deleteCOntact(temp);
            }
        });
        mUpdateContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = checkInputs();
                DialogFragment dialogFragment = new DialogFragment();
                dialogFragment.show(getFragmentManager(), "DialogFragment");
                dialogFragment.setContactDatabase(contactDatabase);
                dialogFragment.setContactReference(contactReference);
                dialogFragment.setContacts(temp);
            }
        });
        mCallContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + mContactNumber.getText().toString()));

                try{
                    startActivity(intent);
                }
                catch (Exception ex){
                    Toast.makeText(getApplicationContext(),"Call can't be made!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public Contacts checkInputs() {

        Contacts temp = new Contacts();
        String name = mName.getText().toString();
        String contactNumber = mContactNumber.getText().toString();

        temp.setContactNumber(contactNumber);
        temp.setName(name);
        if(TextUtils.isEmpty(name)){
            mName.setError("Name must not be empty!");
        }

        if(TextUtils.isEmpty(mContactNumber.getText().toString())){
            mContactNumber.setError("Contact Number must not be empty!");
        }

        return temp;
    }

    private void newContact(Contacts temp){

        Contacts contacts = new Contacts(temp.getContactNumber(), temp.getName());
        String key = contactReference.push().getKey();
        contactReference.child(key).setValue(contacts);

        Toast.makeText(this,
                "Contact "+ contacts.getName()+ " is added successfully!",
                Toast.LENGTH_SHORT).show();
    }

    public void deleteCOntact(Contacts temp){
        final Contacts match = temp;
        Query deleteQuery = contactReference.orderByChild("name").equalTo(temp.getName());
        deleteQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator= snapshotIterator.iterator();

                while(iterator.hasNext()){
                    DataSnapshot snapshot = iterator.next();
                    Contacts contacts = snapshot.getValue(Contacts.class);

                    if(contacts.getName().equals(match.getName())){
                        String key = snapshot.getKey();
                        contactReference.child(key).removeValue();
                    }
                }

                Toast.makeText(MainActivity.this,
                        "User : "+ match.getName()+ ", successfully deleted",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public ArrayList<Contacts> findItem(){

        final ArrayList<Contacts> contactsArrayList = new ArrayList<>();

        contactReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> snapshotIterable = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterable.iterator();

                while(iterator.hasNext()) {
                    DataSnapshot snapshot = iterator.next();
                    Contacts contacts = snapshot.getValue(Contacts.class);

                    contactsArrayList.add(contacts);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return contactsArrayList;
    }
}

