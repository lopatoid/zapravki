package vc.qc.zapravki;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSV;
import au.com.bytecode.opencsv.CSVReadProc;

public class MainActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
		OnInfoWindowClickListener {

	private GoogleMap mMap;
	private LocationClient mLocationClient;
	public static List<Zapravka> zapravki = new ArrayList<Zapravka>(0);
	private final List<Marker> zapravkiMarkers = new ArrayList<Marker>();

	// These settings are the same as the settings for the map. They will in
	// fact give you updates
	// at the maximal rates currently possible.
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (!isOnline()) {
			Toast.makeText(this, "Нет доступа к интернету", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		try {
			readCSV();
		} catch (IOException e) {
		}


	}
	
	private void readCSV() throws IOException {
		CSV csv = CSV.separator(';') // delimiter of fields
		.quote('"') // quote character
		.skipLines(1)
		.create();
		csv.read(getAssets().open("good.csv"), new CSVReadProc() {
			public void procRow(int rowIndex, String... values) {
				System.out.println(rowIndex + ": "
						+ Arrays.asList(values));
				zapravki.add(new Zapravka(true, values));
			}
		});
		csv.read(getAssets().open("bad.csv"), new CSVReadProc() {
			public void procRow(int rowIndex, String... values) {
				System.out.println(rowIndex + ": "
						+ Arrays.asList(values));
				zapravki.add(new Zapravka(false, values));
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				mMap.setMyLocationEnabled(true);
				// mMap.setOnMyLocationButtonClickListener(this);
				addMarkersToMap();
				// Set listeners for marker events. See the bottom of this class
				// for their behavior.
				mMap.setOnInfoWindowClickListener(this);
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.75, 37.616667), 9));
			}
		}
	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getApplicationContext(), this, // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
	}
	
	/**
	 * 
	 * Indicates whether network connectivity exists and it is possible to establish connections and pass data.
	 * 
	 * @return true if network connectivity exists, false otherwise.
	 */
	public boolean isOnline() {
		try {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			return cm.getActiveNetworkInfo().isConnected();
		} catch (Exception e) {
			return false;
		}
	}

	// /**
	// * Button to get current Location. This demonstrates how to get the
	// current
	// * Location as required without needing to register a LocationListener.
	// */
	// public void showMyLocation(View view) {
	// if (mLocationClient != null && mLocationClient.isConnected()) {
	// String msg = "Location = " + mLocationClient.getLastLocation();
	// Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
	// .show();
	// }
	// }

	/**
	 * Implementation of {@link LocationListener}.
	 */
	@Override
	public void onLocationChanged(Location location) {

	}

	/**
	 * Callback called when connected to GCore. Implementation of
	 * {@link ConnectionCallbacks}.
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(REQUEST, this); // LocationListener
	}

	/**
	 * Callback called when disconnected from GCore. Implementation of
	 * {@link ConnectionCallbacks}.
	 */
	@Override
	public void onDisconnected() {
		// Do nothing
	}

	/**
	 * Implementation of {@link OnConnectionFailedListener}.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Do nothing
	}

	private void addMarkersToMap() {
		for (Zapravka zapravka: zapravki) {
			zapravkiMarkers.add(mMap.addMarker(new MarkerOptions()
					.position(zapravka.latLng)
//							new LatLng(55.738642,37.635524))
					.title(zapravka.compName)
					.snippet(zapravka.address)
					.icon(BitmapDescriptorFactory
							.defaultMarker(zapravka.isGood?BitmapDescriptorFactory.HUE_GREEN:
								BitmapDescriptorFactory.HUE_RED))));
		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
//		Toast.makeText(getBaseContext(), "Click Info Window " + marker.getId(),
//				Toast.LENGTH_SHORT).show();
		startActivity(new Intent(this, ZapravkaActivity.class).putExtra(Intent.EXTRA_UID, Integer.parseInt(marker.getId().substring(1))));
	}
}
