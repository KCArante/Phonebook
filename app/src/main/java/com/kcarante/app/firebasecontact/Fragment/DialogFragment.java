package com.kcarante.app.firebasecontact.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kcarante.app.firebasecontact.Constructor.Contacts;
import com.kcarante.app.firebasecontact.R;

import java.util.Iterator;


/**
 * Created by SARJ on 8/3/2016.
 */

public class DialogFragment extends android.app.DialogFragment {

    private Contacts temp;
    private EditText mName;
    private EditText mContactNumber;

    private DatabaseReference contactReference;
    private FirebaseDatabase contactDatabase;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Input new contact");
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.input_contact, null);

        builder.setView(dialogView)
                .setPositiveButton("Update contact", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mName = (EditText) dialogView.findViewById(R.id.name);
                        mContactNumber = (EditText) dialogView.findViewById(R.id.contact);


                            Query deleteQuery = contactReference.orderByChild("name").equalTo(temp.getName());
                            deleteQuery.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                                    Iterator<DataSnapshot> iterator= snapshotIterator.iterator();

                                    while(iterator.hasNext()){
                                        DataSnapshot snapshot = iterator.next();
                                        Contacts contacts = snapshot.getValue(Contacts.class);

                                            String key = snapshot.getKey();
                                            contactReference.child(key).child("name").setValue(mName.getText().toString());
                                            contactReference.child(key).child("contactNumber").setValue(mContactNumber.getText().toString());

                                            Toast.makeText(getActivity(), "Updated "+ mName.getText(), Toast.LENGTH_SHORT).show();

                                        }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public void setContactDatabase(FirebaseDatabase firebaseDatabase){
        this.contactDatabase = firebaseDatabase;
    }
    public void setContactReference(DatabaseReference contactReference){
        this.contactReference = contactReference;
    }
    public void setContacts(Contacts temp){
        this.temp = temp;
    }

}
