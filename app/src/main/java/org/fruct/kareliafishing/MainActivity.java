package org.fruct.kareliafishing;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;

import ru.yandex.yandexmapkit.*;
import ru.yandex.yandexmapkit.map.*;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;


public class MainActivity extends ActionBarActivity
		implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	private Intent toObjectsList;
	private static MapView mMap = null;
	private static MapController mMapController;
	private static OverlayManager mOverlayManager;
	private static Overlay mOverlay = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment)
				getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

		mMap = (MapView)findViewById(R.id.mapView);
		mMapController = mMap.getMapController();
		mMapController.addMapListener(new MapListener());
		mOverlayManager = mMapController.getOverlayManager();

		try
		{
			if (!Parser.isInitialized())
				Parser.initialize(this);

			if (!ApplicationData.isInitialized())
				ApplicationData.initialize(this);

			if (!NetworkData.isInitialized())
				NetworkData.initialize(this);
		}
		catch (Exception ex)
		{
			Log.e("MainActivity:onCreate()", ex.getMessage());
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();

		mMapController.hideBalloon();
		if (null != mOverlay)
			mOverlayManager.removeOverlay(mOverlay);

		refreshMap();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
				.commit();
	}

	public void onSectionAttached(int number) {
		try {
			switch (number) {
				case 1:
					mTitle = getString(R.string.hostels);

					break;
				case 2:
					mTitle = getString(R.string.shops);

					break;
				case 3:
					mTitle = getString(R.string.lakes);

					break;
				case 4:
					mTitle = getString(R.string.type_of_fish);
					break;
				default:
					return;
			}
		}
		catch (Exception ex)
		{
			Log.e("onSectionAttached()", ex.toString());
		}

	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			//getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(
					getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}

	private void refreshMap()
	{
		ObjectData processingObject;
		double latitude, longitude;
		OverlayItem newItem;
		BalloonItem newBalloonItem;
		Iterator<ObjectData> it;

		if (ApplicationData.mapObjects().isEmpty())
			return;

		mOverlay = new Overlay(mMapController);

		for (it = ApplicationData.mapObjects().iterator(); it.hasNext();)
		{
			processingObject = it.next();

			if (processingObject.getType() != ObjectData.LAKE)
			{
				latitude = Double.parseDouble(processingObject.getInfo("latitude"));
				longitude = Double.parseDouble(processingObject.getInfo("longitude"));

				Log.e("refreshMap()", processingObject.getName());
				Log.e("latitude", String.valueOf(latitude));
				Log.e("longitude", String.valueOf(longitude));

				newItem = new OverlayItem(new GeoPoint(latitude, longitude), null);

				if (ObjectData.SHOP == processingObject.getType())
					newItem.setDrawable(this.getResources().getDrawable(R.drawable.shop));
				else if (ObjectData.HOSTEL == processingObject.getType())
					newItem.setDrawable(this.getResources().getDrawable(R.drawable.hostel));

				newItem.setVisible(true);

				newBalloonItem = new BalloonItem(this, new GeoPoint(latitude, longitude));
				newBalloonItem.setText(processingObject.getName());
				newItem.setBalloonItem(newBalloonItem);

				mOverlay.addOverlayItem(newItem);
			}
		}

		mOverlayManager.addOverlay(mOverlay);
	}

	public void onClick(View view) throws Exception
	{
		NetworkData.addTask(new UpdateTask());

	}

	class MapListener implements OnMapListener
	{
		@Override
		public void onMapActionEvent( MapEvent mapEvent )
		{

		}
	}
}
