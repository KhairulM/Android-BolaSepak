package com.example.bolasepak;

import android.location.Address;

import java.util.List;

public interface AsyncGeocoderResponse {
    void onGeocoderFinished(List<Address>addressList);
}
