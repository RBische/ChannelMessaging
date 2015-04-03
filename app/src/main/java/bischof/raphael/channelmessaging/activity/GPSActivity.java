package bischof.raphael.channelmessaging.activity;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by biche on 27/02/2015.
 */
public class GPSActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    public Location mCurrentLocation;
    private boolean mEverRequestingLocationUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mGoogleApiClient.isConnected()&&!mEverRequestingLocationUpdate){
            startUpdates();
        }
    }

    private void startUpdates(){
        mEverRequestingLocationUpdate = true;
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        onLocationChanged(lastLocation);
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        //Correspond à l’intervalle moyen de temps entre chaque mise à jour des coordonnées
        mLocationRequest.setFastestInterval(5000);
        //Correspond à l’intervalle le plus rapide entre chaque mise à jour des coordonnées
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //Définit la demande de mise à jour avec un niveau de précision maximal
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        mEverRequestingLocationUpdate = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()&&!mEverRequestingLocationUpdate){
            startUpdates();
        }
    }
}
