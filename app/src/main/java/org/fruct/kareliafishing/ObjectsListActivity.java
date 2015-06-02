package org.fruct.kareliafishing;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class ObjectsListActivity extends ActionBarActivity {

	private List<ListElement> listElements = null;
	private LinearLayout listLayout = null;
	private Spinner sortingTypeSpinner = null;
	private Button showOnMapButton = null;
	private Intent toObjectInfo = null;
	protected static String title = null;
	private Activity activity = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Log.e("ObjListAct", "onResume()");

		if (null != ObjectsListActivity.title)
			setTitle(ObjectsListActivity.title);

		if (null == ApplicationData.objectsData() || ApplicationData.objectsData().isEmpty())
		{
			if (null != listLayout)
				listLayout.removeAllViews();

			return;
		}

		switch (ApplicationData.objectsData().get(0).getType())
		{
			case ObjectData.BEHAVIOR_RULE:
			case ObjectData.FISHING_RULE:
				setContentView(R.layout.activity_objects_list_rule);
				break;
			case ObjectData.RECIPE:
				setContentView(R.layout.activity_objects_list_recipe);
				break;
			case ObjectData.LAKE:
				setContentView(R.layout.activity_objects_list_lake);
				break;
			case ObjectData.SHOP:
				setContentView(R.layout.activity_objects_list_shop);
				break;
			case ObjectData.FISH:
				setContentView(R.layout.activity_objects_list_fish);
				break;
			case ObjectData.HOSTEL:
				setContentView(R.layout.activity_objects_list_hostel);
				break;
		}

		switch (ApplicationData.objectsData().get(0).getType())
		{
			case ObjectData.LAKE:
			case ObjectData.SHOP:
			case ObjectData.HOSTEL:
				sortingTypeSpinner = (Spinner)findViewById(R.id.sortingTypeSpinner);
				sortingTypeSpinner.setOnItemSelectedListener(new ItemSelectedListener());
				showOnMapButton = (Button)findViewById(R.id.showOnMapButton);
				break;
			default:
				break;
		}

		toObjectInfo = new Intent(this, ObjectInfoActivity.class);
		listLayout = (LinearLayout)findViewById(R.id.listLayout);

		int i;
		ListElement tmp;

		if (listElements == null)
			listElements = new ArrayList<>();

		listLayout.removeAllViews();
		listElements.clear();

		for (i = 0; i < ApplicationData.objectsData().size(); i++)
		{
			tmp = new ListElement(this, ApplicationData.objectsData().get(i));
			if(i % 2==0)
			{
				tmp.setBackgroundColor(Color.rgb(223, 223, 223));
			}
			else
			{
				tmp.setBackgroundColor(Color.rgb(247,247,247));
			}
			listElements.add(tmp);
			listLayout.addView(listElements.get(i));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.menu_objects_list, menu);
		return true;
	}

	@Override
	public void onBackPressed()
	{
		Log.e("onBackPressed", "ha-ha");
		//finish();
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
	}

	/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		//int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	*/

	class ListElement extends LinearLayout
	{
		private TextView txtView;
		private ObjectData object;					// Existing object to associate with

		public ListElement(Context _context, ObjectData _obj)
		{
			super(_context);
			setOrientation(HORIZONTAL);
			object = _obj;

			txtView = new TextView(_context);          // Creating TextView for object's name
			txtView.setTextAppearance(_context, R.style.Base_TextAppearance_AppCompat_Large);
			txtView.setText(_obj.getName());           // Put object's name to TextView's text
			//txtView.setOnLongClickListener(new LongClickListener());
			this.setOnLongClickListener(new LongClickListener(this));

			addView(txtView);                          // Put TextView on layer
		}

		class LongClickListener implements OnLongClickListener
		{
			private ListElement element = null;

			public LongClickListener(ListElement _element)
			{
				element = _element;
			}

			@Override
			public boolean onLongClick(View v)
			{
				Log.e("LongClick", element.getAssociatedObject().getName());
				toObjectInfo.putExtra("title", getAssociatedObject().getName());
				ApplicationData.setObjectForInfo(getAssociatedObject());
				startActivity(toObjectInfo);
				return false;
			}
		}

		public ObjectData getAssociatedObject()
		{
			return object;
		}
	}

	class ItemSelectedListener implements AdapterView.OnItemSelectedListener
	{
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View itemView, int itemPosition, long itemId)
		{
			int i, j, size, pos;
			double x1, x2, y1, y2, distance, max_distance;
			ListElement max, tmp;
			Collator collator;
			Log.e("itemPosition", Integer.toString(itemPosition));
			Log.e("itemId", Long.toString(itemId));

			size = listElements.size();
			collator = Collator.getInstance(new Locale("ru", "RU"));

			// сортировка элементов списка по алфавиту
			if (itemPosition == 0)
			{
				for (i = 0; i < size - 1; i++)
				{
					max = listElements.get(i);
					pos = i;

					for (j = i + 1; j < size; j++)
					{
						if (collator.compare(max.getAssociatedObject().getName(), listElements.get(j).getAssociatedObject().getName()) > 0)
						{
							max = listElements.get(j);
							pos = j;
						}
					}

					tmp = listElements.set(i, max);
					listElements.set(pos, tmp);
				}
			}
			// сортировка по удаленности
			else if (itemPosition == 1)
			{
				for (i = 0; i < size - 1; i++)
				{
					if (null == listElements.get(i).getAssociatedObject().getInfo("latitude"))
						continue;

					if (null == listElements.get(i).getAssociatedObject().getInfo("longitude"))
						continue;

					if (null == ApplicationData.getSetting("my_latitude") || null == ApplicationData.getSetting("my_longitude"))
						continue;

					x1 = Double.parseDouble(ApplicationData.getSetting("my_latitude"));
					y1 = Double.parseDouble(ApplicationData.getSetting("my_longitude"));

					max = listElements.get(i);
					max_distance = 0;
					pos = i;

					for (j = i + 1; j < size; j++)
					{
						x2 = Double.parseDouble(listElements.get(i).getAssociatedObject().getInfo("latitude"));
						y2 = Double.parseDouble(listElements.get(i).getAssociatedObject().getInfo("longitude"));

						distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
						Log.e(listElements.get(i).getAssociatedObject().getName(), String.valueOf(distance));

						if (max_distance < distance)
						{
							max = listElements.get(j);
							max_distance = distance;
							pos = j;
						}
					}

					tmp = listElements.set(i, max);
					listElements.set(pos, tmp);
				}
			}

			listLayout.removeAllViews();

			for (i = 0; i < size; i++) {
				if(i % 2==0)
				{
					listElements.get(i).setBackgroundColor(Color.rgb(223, 223, 223));
				}
				else
				{
					listElements.get(i).setBackgroundColor(Color.rgb(247,247,247));
				}
				listLayout.addView(listElements.get(i));
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView) { }
	}

	public void onShowOnMapButtonClick(View view)
	{
		int i;
		int size = listLayout.getChildCount();
		ListElement tmp;

		if (0 == size)
			return;

		ApplicationData.mapObjects().clear();

		for (i = 0; i < size; i++)
		{
			tmp = (ListElement) listLayout.getChildAt(i);
			ApplicationData.mapObjects().add(tmp.getAssociatedObject());
		}

		//finish();
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
	}
}
