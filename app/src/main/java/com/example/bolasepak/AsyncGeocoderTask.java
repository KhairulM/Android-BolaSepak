package com.example.bolasepak;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AsyncGeocoderTask extends AsyncTask<String, Integer, List<Address>> {
    public AsyncGeocoderResponse delegate = null;
    public Context context;
    public Geocoder gc;
    public List<Address> addressList;

    public AsyncGeocoderTask(Context context) {
        this.context = context;
        gc = new Geocoder(context, Locale.getDefault());
    }

    @Override
    protected List<Address> doInBackground(String... strings) {
        List<Address> addressList = new ArrayList<>();

        try{
             addressList = gc.getFromLocationName(strings[0], 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.addressList = addressList;
        return addressList;
    }

    @Override
    protected void onPostExecute(List<Address> addressList){
        delegate.onGeocoderFinished(addressList);
    }
}
