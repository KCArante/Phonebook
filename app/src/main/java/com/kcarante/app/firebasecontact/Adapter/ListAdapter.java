package com.kcarante.app.firebasecontact.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kcarante.app.firebasecontact.Constructor.Contacts;
import com.kcarante.app.firebasecontact.R;

import java.util.ArrayList;

/**
 * Created by SARJ on 8/3/2016.
 */
public class ListAdapter extends ArrayAdapter<Contacts>{

    private Context context;
    private int resource;
    private ArrayList<Contacts> objects;

    public ListAdapter(Context context, int resource, ArrayList<Contacts> objects) {
        super( context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contacts contacts = getItem(position);

        if(convertView == null){

            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            TextView mName= (TextView) convertView.findViewById(R.id.name);
            TextView mContact = (TextView) convertView.findViewById(R.id.contactNumber);

            mName.setText(contacts.getName());
            mContact.setText(contacts.getContactNumber());
        }

        return convertView;
    }

}
