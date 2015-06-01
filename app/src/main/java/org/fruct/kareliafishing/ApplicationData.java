package org.fruct.kareliafishing;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by doomgiver on 04.04.15.
 */
class ApplicationData
{
	private static List<ObjectData> objects = null; // List of objects to be shown at ObjectsListActivity
	private static List<ObjectData> mapObjects = null;
	private static List<ObjectData> fish = null;    // List of all available fish;
	private static List<ObjectData> hostels = null; // List of all available hostels
	private static List<ObjectData> lakes = null;   // List of all available lakes
	private static List<ObjectData> shops = null;   // List of all available shops
	private static List<ObjectData> behaviour_rules = null;   // List of all available shops
	private static List<ObjectData> fishing_rules = null;   // List of all available shops
	private static List<ObjectData> recipes = null;   // List of all available shops
	private static ObjectData object = null;		// Info on this object will be shown on ObjectInfoActivity
	private static HashMap<String, String> settings = null;
	private static boolean init = false;
	private static Activity activity = null;

	private ApplicationData(){}

	public static void initialize(Activity _activity)
	{
		if (_activity == null)
			return;

		activity = _activity;
		settings = new HashMap<>();
		objects = new ArrayList<>();
		mapObjects = new ArrayList<>();
		loadSettings();

		try
		{
			if (Parser.isInitialized())
			{
				setLakesData(Parser.parseLakes());
				setFishData(Parser.parseFish());
				setHostelsData(Parser.parseHostels());
				setShopsData(Parser.parseShops());
				setBehaviourRulesData(Parser.parseBehaviourRules());
				setFishingRulesData(Parser.parseFishingRules());
				setRecipesData(Parser.parseRecipes());

				init = true;
			}
		}
		catch (Exception ex)
		{
			Log.e("AppData:init()", ex.toString());
		}

	}

	public static boolean isInitialized() { return init; }

	public static List<ObjectData> getFishData() { return fish; }
	public static void setFishData(ArrayList<ObjectData> _arg) { fish = _arg; }

	public static List<ObjectData> getHostelsData() { return hostels; }
	public static void setHostelsData(ArrayList<ObjectData> _arg)
	{
		hostels = _arg;
	}

	public static List<ObjectData> getLakesData() { return lakes; }
	public static void setLakesData(ArrayList<ObjectData> _arg)
	{
		lakes = _arg;
	}

	public static List<ObjectData> getShopsData() {
		return shops;
	}
	public static void setShopsData(ArrayList<ObjectData> _arg)
	{
		shops = _arg;
	}

	public static List<ObjectData> getBehaviourRulesData() {
		return behaviour_rules;
	}
	public static void setBehaviourRulesData(ArrayList<ObjectData> _arg)
	{
		behaviour_rules = _arg;
	}

	public static List<ObjectData> getFishingRulesData() {
		return fishing_rules;
	}
	public static void setFishingRulesData(ArrayList<ObjectData> _arg)
	{
		fishing_rules = _arg;
	}

	public static List<ObjectData> getRecipesData() { return recipes; }
	public static void setRecipesData(ArrayList<ObjectData> _arg)
	{
		recipes = _arg;
	}

	public static List<ObjectData> objectsData() {
		return objects;
	}
	public static List<ObjectData> mapObjects() {
		return mapObjects;
	}

	public static void showAll( int _type )
	{
		List<ObjectData> tmp;

		switch (_type)
		{
			case ObjectData.FISH:
				tmp = fish;
				break;
			case ObjectData.HOSTEL:
				tmp = hostels;
				break;
			case ObjectData.LAKE:
				tmp = lakes;
				break;
			case ObjectData.SHOP:
				tmp = shops;
				break;
			case ObjectData.BEHAVIOR_RULE:
				tmp = behaviour_rules;
				break;
			case ObjectData.FISHING_RULE:
				tmp = fishing_rules;
				break;
			case ObjectData.RECIPE:
				tmp = recipes;
				break;
			default:
				return;
		}

		objects.addAll(tmp);
	}

	public static void show(ArrayList<ObjectData> list)
	{
		objects = list;
	}

	public static void setObjectForInfo(ObjectData _object)
	{
		object = _object;
	}
	public static ObjectData getObjectForInfo()
	{
		return object;
	}

	public static synchronized void addSetting(String _key, String _value)
	{
		if (_key == null || _value == null)
			return;

		settings.put(_key, _value);
		saveSettings();
	}

	public static String getSetting(String _key)
	{
		if (_key == null || !ApplicationData.isInitialized())
			return null;

		if (!settings.containsKey(_key))
			return null;

		return settings.get(_key);
	}

	private static void saveSettings()
	{
		String path;
		String key, value;
		Set<String> keySet;
		Iterator<String> it;
		FileOutputStream oStream;
		byte[] buffer;

		try
		{
			//buffer = new byte[2048];
			path = activity.getString(R.string.app_settings);
			oStream = activity.openFileOutput(path, Activity.MODE_PRIVATE);
			keySet = settings.keySet();

			value = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
			value += "<settings>\n";
			buffer = value.getBytes();
			oStream.write(buffer);

			it = keySet.iterator();
			while (it.hasNext())
			{
				key = it.next();
				value = settings.get(key);
				buffer = String.format("<%s>%s</%s>\n", key, value, key).getBytes();
				oStream.write(buffer);
			}

			value = "</settings>";

			buffer = value.getBytes();
			oStream.write(buffer);
			oStream.close();
		}
		catch (Exception ex)
		{
			Log.e("AppData:saveSettings()", ex.toString());
		}
	}

	private static void loadSettings()
	{
		File f;
		FileInputStream iStream;
		BufferedReader reader;
		String path, data, key, value;
		int startIndex, endIndex;

		try
		{
			path = activity.getString(R.string.app_settings);
			f = new File(activity.getFilesDir().getAbsolutePath() + "/" + path);
			if (!f.exists())
				return;
			iStream = activity.openFileInput(path);
			reader = new BufferedReader(new InputStreamReader(iStream));

			while (true)
			{
				data = reader.readLine();

				if (data == null)
					break;
				if (data.startsWith("<?xml") || data.endsWith("settings>"))
					continue;

				startIndex = data.indexOf("<");
				endIndex = data.indexOf(">");
				key = data.substring(startIndex + 1, endIndex);
				value = Parser.getTextValue(data, key);
				settings.put(key, value);
			}
		}
		catch (Exception ex)
		{
			Log.e("AppData:loadSettings()", ex.toString());
		}
	}
}