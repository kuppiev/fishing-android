package org.fruct.kareliafishing;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ObjectInfoActivity extends ActionBarActivity {
	private TextView description_txtView = null;
	private ImageView imageView = null;
	private ObjectData object = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		object = ApplicationData.getObjectForInfo();

		if (object == null)
			return;

		switch (object.getType())
		{
			case ObjectData.FISH:
				setContentView(R.layout.activity_object_info_fish);
				break;
			case ObjectData.LAKE:
				setContentView(R.layout.activity_object_info_lake);
				break;
			case ObjectData.HOSTEL:
				setContentView(R.layout.activity_object_info_hostel);
				break;
			case ObjectData.SHOP:
				setContentView(R.layout.activity_object_info_shop);
				break;
			default:
				setContentView(R.layout.activity_object_info_fish);
				break;
		}

		description_txtView = (TextView)findViewById(R.id.description_textView);
		imageView = (ImageView)findViewById(R.id.imageView);

		setTitle(getIntent().getStringExtra("title"));

		description_txtView.setText(object.getDescription());
		Integer img_id = getResources().getIdentifier(object.getId(), "drawable", getPackageName());

		if (img_id == 0)
			imageView.setImageDrawable(getResources().getDrawable(R.drawable.unavailable));
		else
			imageView.setImageDrawable(getResources().getDrawable(img_id));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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

	public void onToListOfLakesButtonClick(View view)
	{
		Integer i, fish_count;
		boolean found;
		ObjectData processingLake;
		String currentFishId;
		ArrayList<ObjectData> lakes;
		Iterator<ObjectData> it;

		lakes = new ArrayList<>();
		currentFishId = ApplicationData.getObjectForInfo().getId();

		for (it = ApplicationData.getLakesData().iterator(); it.hasNext();)
		{
			processingLake = it.next();
			fish_count = Integer.parseInt(processingLake.getInfo("fish_count"));
			found = false;

			for (i = 1; i <= fish_count; i++)
			{
				Log.e("fish" + i.toString(), processingLake.getInfo("fish" + i.toString()));

				if (processingLake.getInfo("fish" + i.toString()).equals(currentFishId)) {
					found = true;
					break;
				}
			}

			if (found)
				lakes.add(processingLake);
		}

		ApplicationData.objectsData().clear();
		ApplicationData.show(lakes);

		Intent intent = new Intent(this, ObjectsListActivity.class);
		intent.putExtra("title", this.getResources().getStringArray(R.array.menu_items)[2]);
		startActivity(intent);
	}

	public void onToListOfFishButtonClick(View view)
	{
		Integer fish_count, i;
		ObjectData currentLake;
		ObjectData processingFish;
		ArrayList<ObjectData> fish;
		Iterator<ObjectData> it;

		currentLake = ApplicationData.getObjectForInfo();
		fish_count = Integer.parseInt(currentLake.getInfo("fish_count"));
		fish = new ArrayList<>();

		for (it = ApplicationData.getFishData().iterator(); it.hasNext();)
		{
			processingFish = it.next();

			for (i = 1; i <= fish_count; i++)
			{
				if (processingFish.getId().equals(currentLake.getInfo("fish" + i.toString())))
				{
					fish.add(processingFish);
					break;
				}
			}
		}

		ApplicationData.objectsData().clear();
		ApplicationData.show(fish);

		Intent intent = new Intent(this, ObjectsListActivity.class);
		intent.putExtra("title", this.getResources().getStringArray(R.array.menu_items)[3]);
		startActivity(intent);
	}
}
