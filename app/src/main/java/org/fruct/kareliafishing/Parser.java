package org.fruct.kareliafishing;

import android.app.Activity;
import android.text.Html;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;


/**
 * Created by doomgiver on 26.03.15.
 */
public abstract class Parser
{
	private static boolean init = false;
	protected static Activity activity = null;
	private static String[] source_files = null;
	private static XmlPullParserFactory xppf = null;
	private static XmlPullParser xpp = null;

	public static void initialize(Activity _activity)
	{
		try
		{
			activity = _activity;
			source_files = activity.getResources().getStringArray(R.array.source_files);

			if (xppf == null)
				xppf = XmlPullParserFactory.newInstance();

			if (xpp == null)
				xpp = xppf.newPullParser();

			File f;

			for (Integer i = 0; i < source_files.length - 3; i++)
			{
				f = new File(activity.getFilesDir().getAbsolutePath() + "/" + source_files[i]);

				if (!f.exists()) {
					Log.e("Info", activity.getFilesDir().getAbsolutePath() + "/" + source_files[i]);
					saveXmlInDeviceMemory(i + 1);
				}
			}

			init = true;
		}
		catch (Exception ex)
		{
			Log.e("Parser:init()", ex.toString());
		}
	}

	public static boolean isInitialized() {
		return init;
	}

	private static void saveXmlInDeviceMemory(Integer _type) throws Exception
	{
		InputStream istream;
		byte[] buffer = new byte[2048];

		switch (_type)
		{
			case ObjectData.FISH:
				istream = activity.getResources().openRawResource(R.raw.fishinfo);
				break;
			case ObjectData.LAKE:
				istream = activity.getResources().openRawResource(R.raw.lakes);
				break;

			case ObjectData.HOSTEL:
				istream = activity.getResources().openRawResource(R.raw.hostels);
				break;

			case ObjectData.SHOP:
				istream = activity.getResources().openRawResource(R.raw.shops);
				break;
			default:
				throw new Exception("Unknown object's type");
		}

		FileOutputStream ostream = activity.openFileOutput(source_files[_type - 1], Activity.MODE_PRIVATE);

		while (-1 != istream.read(buffer))
			ostream.write(buffer);

		istream.close();
		ostream.close();

		Log.e("info", "File \'" + source_files[_type - 1] + "\' was created");
	}

	public static ArrayList<ObjectData> parseHostels()
	{
		ArrayList<ObjectData> retValue = null;
		Integer i = 0;
		InputStream istream = null;
		String processingTag = null;
		ObjectData processingObject = null;

		try
		{
			retValue = new ArrayList<ObjectData>();
			istream = activity.openFileInput(source_files[ObjectData.HOSTEL - 1]);
			Log.e("parser", source_files[ObjectData.HOSTEL - 1]);
			xpp.setInput(new InputStreamReader(istream));

			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						processingTag = xpp.getName();

						if (processingTag.equals("hostel"))
							processingObject = new ObjectData(ObjectData.HOSTEL);

						break;

					case XmlPullParser.END_TAG:

						if (xpp.getName().equals("hostel"))
							retValue.add(processingObject);

						processingTag = "";

						break;

					case XmlPullParser.TEXT:

						switch (processingTag)
						{
							case "hostels":
							case "hostel":
								break;
							case "id":
								processingObject.setId(xpp.getText());
								break;
							case "name":
								processingObject.setName(xpp.getText());
								break;
							case "description":
								processingObject.setDescription(xpp.getText());
								break;
							default:
								processingObject.setInfo(processingTag, xpp.getText());
								break;
						}

						break;

					default:
						Log.e("i", Integer.toString(xpp.getEventType()));
						break;
				}

				xpp.next();
			}
		}
		catch (Exception ex)
		{
			Log.e("parseHostels()", ex.toString());
		}

		return retValue;
	}

	public static ArrayList<ObjectData> parseShops()
	{
		ArrayList<ObjectData> retValue = null;
		Integer i = 0;
		InputStream istream = null;
		String processingTag = null;
		ObjectData processingObject = null;

		try
		{
			retValue = new ArrayList<ObjectData>();
			istream = activity.openFileInput(source_files[ObjectData.SHOP - 1]);
			Log.e("parser", source_files[ObjectData.SHOP - 1]);
			xpp.setInput(new InputStreamReader(istream));

			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						processingTag = xpp.getName();

						if (processingTag.equals("shop"))
							processingObject = new ObjectData(ObjectData.SHOP);

						break;

					case XmlPullParser.END_TAG:

						if (xpp.getName().equals("shop"))
							retValue.add(processingObject);

						processingTag = "";

						break;

					case XmlPullParser.TEXT:

						switch (processingTag)
						{
							case "shops":
							case "shop":
								break;
							case "id":
								processingObject.setId(xpp.getText());
								break;
							case "name":
								processingObject.setName(xpp.getText());
								break;
							case "description":
								processingObject.setDescription(xpp.getText());
								break;
							default:
								processingObject.setInfo(processingTag, xpp.getText());
								break;
						}

						break;

					default:
						Log.e("i", Integer.toString(xpp.getEventType()));
						break;
				}

				xpp.next();
			}
		}
		catch (Exception ex)
		{
			Log.e("parseShops()", ex.toString());
		}

		return retValue;
	}

	public static ArrayList<ObjectData> parseFish()
	{
		ArrayList<ObjectData> retValue = null;
		InputStream istream;
		String processingTag = null;
		ObjectData processingObject = null;

		try
		{
			retValue = new ArrayList<>();
			istream = activity.openFileInput(source_files[ObjectData.FISH - 1]);
			Log.e("parser", source_files[ObjectData.FISH - 1]);
			xpp.setInput(new InputStreamReader(istream));

			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						processingTag = xpp.getName();

						if (processingTag.equals("fish"))
							processingObject = new ObjectData(ObjectData.FISH);

						break;

					case XmlPullParser.END_TAG:

						if (xpp.getName().equals("fish"))
							retValue.add(processingObject);

						processingTag = "";

						break;

					case XmlPullParser.TEXT:

						if (null == processingTag)
							break;

						switch (processingTag)
						{
							case "fishinfo":
							case "fish":
								break;
							case "id":
								processingObject.setId(xpp.getText());
								break;
							case "name":
								processingObject.setName(xpp.getText());
								break;
							case "description":
								processingObject.setDescription(xpp.getText());
								break;
							default:
								processingObject.setInfo(processingTag, xpp.getText());
								break;
						}

						break;

					default:
						Log.e("i", Integer.toString(xpp.getEventType()));
						break;
				}

				xpp.next();
			}
		}
		catch (Exception ex)
		{
			Log.e("ParseFish()", ex.toString());
		}

		return retValue;
	}

	public static ArrayList<ObjectData> parseLakes()
	{
		ArrayList<ObjectData> retValue = null;
		Integer points_count = 0, fish_count = 0;
		InputStream istream;
		String processingTag = null;
		ObjectData processingObject = null;

		try
		{
			retValue = new ArrayList<>();
			istream = activity.openFileInput(source_files[ObjectData.LAKE - 1]);
			Log.e("parser", source_files[ObjectData.LAKE - 1]);
			xpp.setInput(new InputStreamReader(istream));

			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						processingTag = xpp.getName();

						if (processingTag.equals("lake")) {
							processingObject = new ObjectData(ObjectData.LAKE);
							points_count = 0;
							fish_count = 0;
						}

						break;

					case XmlPullParser.END_TAG:

						if (xpp.getName().equals("lake"))
						{
							processingObject.setInfo("points_count", points_count.toString());
							processingObject.setInfo("fish_count", fish_count.toString());
							retValue.add(processingObject);
						}

						processingTag = "";

						break;

					case XmlPullParser.TEXT:

						if (null == processingTag)
							break;

						switch (processingTag)
						{
							case "lakes":
							case "lake":
							case "info":
							case "coordinates":
							case "fishinfo":
								break;
							case "point":
								points_count++;
								break;
							case "latitude":
								processingObject.setInfo("latitude" + points_count.toString(), xpp.getText());
								Log.e("parseLakes()", "latitude" + points_count.toString());
								Log.e("parseLakes()", xpp.getText());
								break;
							case "longitude":
								processingObject.setInfo("longitude" + points_count.toString(), xpp.getText());
								break;
							case "fish":
								processingObject.setInfo("fish" + (++fish_count).toString(), xpp.getText());
								break;
							case "id":
								processingObject.setId(xpp.getText());
								break;
							case "name":
								processingObject.setName(xpp.getText());
								break;
							case "description":
								processingObject.setDescription(xpp.getText());
								break;
							default:
								processingObject.setInfo(processingTag, xpp.getText());
								break;
						}

						break;

					default:
						Log.e("i", Integer.toString(xpp.getEventType()));
						break;
				}

				xpp.next();
			}
		}
		catch (Exception ex)
		{
			//Log.e("parseLakes()", ex.toString());
		}

		return retValue;
	}

	public static ArrayList<ObjectData> parseBehaviourRules()
	{
		ArrayList<ObjectData> retValue = null;
		String processingTag = null;
		ObjectData processingObject = null;
		XmlPullParser xpp = activity.getResources().getXml(R.xml.behaviour_rules);

		try
		{
			retValue = new ArrayList<>();

			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						processingTag = xpp.getName();

						if (processingTag.equals("rule"))
							processingObject = new ObjectData(ObjectData.BEHAVIOR_RULE);

						break;

					case XmlPullParser.END_TAG:

						if (xpp.getName().equals("rule"))
							retValue.add(processingObject);

						processingTag = "";

						break;

					case XmlPullParser.TEXT:

						if (null == processingTag)
							break;

						switch (processingTag)
						{
							case "behaviour_rules":
							case "rule":
								break;
							case "id":
								processingObject.setId(xpp.getText());
								break;
							case "name":
								processingObject.setName(xpp.getText());
								break;
							case "description":
								processingObject.setDescription(xpp.getText());
								break;
							default:
								processingObject.setInfo(processingTag, xpp.getText());
								break;
						}

						break;

					default:
						Log.e("i", Integer.toString(xpp.getEventType()));
						break;
				}

				xpp.next();
			}
		}
		catch (Exception ex)
		{
			Log.e("parseBehaviourRules()", ex.toString());
		}

		return retValue;
	}

	public static ArrayList<ObjectData> parseFishingRules()
	{
		ArrayList<ObjectData> retValue = null;
		String processingTag = null;
		ObjectData processingObject = null;
		XmlPullParser xpp = activity.getResources().getXml(R.xml.fishing_rules);

		try
		{
			retValue = new ArrayList<>();

			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						processingTag = xpp.getName();

						if (processingTag.equals("rule"))
							processingObject = new ObjectData(ObjectData.FISHING_RULE);

						break;

					case XmlPullParser.END_TAG:

						if (xpp.getName().equals("rule"))
							retValue.add(processingObject);

						processingTag = "";

						break;

					case XmlPullParser.TEXT:

						if (null == processingTag)
							break;

						switch (processingTag)
						{
							case "fishing_rules":
							case "rule":
								break;
							case "id":
								processingObject.setId(xpp.getText());
								break;
							case "name":
								processingObject.setName(xpp.getText());
								break;
							case "description":
								processingObject.setDescription(xpp.getText());
								break;
							default:
								processingObject.setInfo(processingTag, xpp.getText());
								break;
						}

						break;

					default:
						Log.e("i", Integer.toString(xpp.getEventType()));
						break;
				}

				xpp.next();
			}
		}
		catch (Exception ex)
		{
			Log.e("parseFishingRules()", ex.toString());
		}

		return retValue;
	}

	public static ArrayList<ObjectData> parseRecipes()
	{
		ArrayList<ObjectData> retValue = null;
		String processingTag = null;
		ObjectData processingObject = null;
		XmlPullParser xpp = activity.getResources().getXml(R.xml.recipes);

		try
		{
			retValue = new ArrayList<>();

			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						processingTag = xpp.getName();

						if (processingTag.equals("recipe"))
							processingObject = new ObjectData(ObjectData.RECIPE);

						break;

					case XmlPullParser.END_TAG:

						if (xpp.getName().equals("recipe"))
							retValue.add(processingObject);

						processingTag = "";

						break;

					case XmlPullParser.TEXT:

						if (null == processingTag)
							break;

						switch (processingTag)
						{
							case "recipes":
							case "recipe":
								break;
							case "id":
								processingObject.setId(xpp.getText());
								break;
							case "name":
								processingObject.setName(xpp.getText());
								break;
							case "description":
								processingObject.setDescription(xpp.getText());
								break;
							default:
								processingObject.setInfo(processingTag, xpp.getText());
								break;
						}

						break;

					default:
						Log.e("i", Integer.toString(xpp.getEventType()));
						break;
				}

				xpp.next();
			}
		}
		catch (Exception ex)
		{
			Log.e("parseRecipes()", ex.toString());
		}

		return retValue;
	}

	public static void convertHostels()
	{
		Log.e("Entering", "convertHostels()");
		FileInputStream istream;
		FileOutputStream ostream;
		PrintWriter writer;
		String processingTag = null;
		String processingAttr = null;
		String coordinates;
		String tmp;
		double latitude, longitude;
		int[] seporator_pos = new int[2];

		try
		{
			istream = activity.openFileInput("~hostels");
			ostream = activity.openFileOutput("~~hostels", Activity.MODE_PRIVATE);
			writer = new PrintWriter(ostream);
			xpp.setInput(new InputStreamReader(istream));

			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT)
			{
				switch (xpp.getEventType())
				{
					case XmlPullParser.START_DOCUMENT:
						writer.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
						writer.println("<hostels>");
						break;
					case XmlPullParser.START_TAG:
						processingTag = xpp.getName();

						switch (processingTag)
						{
							case "Placemark":
								writer.println("<hostel>");
								break;
							case "Data":
								processingAttr = xpp.getAttributeValue(0);
								break;
							default:
								break;
						}

						break;

					case XmlPullParser.END_TAG:

						switch (xpp.getName())
						{
							case "Placemark":
								writer.println("</hostel>");
								break;
						}

						processingTag = "";

						break;

					case XmlPullParser.TEXT:

						if (null == processingTag)
							break;

						switch (processingTag)
						{
							case "name":
								if (xpp.getDepth() == 6)
									writer.println(String.format("<name>%s</name>", xpp.getText()));
								break;
							case "description":
								tmp = Html.escapeHtml(xpp.getText());
								writer.println(String.format("<description>%s</description>", tmp));
								break;
							case "value":
								if (processingAttr.equals("link"))
									writer.println(String.format("<site>%s</site>", xpp.getText()));
								break;
							case "coordinates":
								coordinates = xpp.getText();
								seporator_pos[0] = coordinates.indexOf(",");
								seporator_pos[1] = coordinates.lastIndexOf(",");
								longitude = Double.parseDouble(coordinates.substring(0, seporator_pos[0]));
								latitude = Double.parseDouble(coordinates.substring(seporator_pos[0] + 1, seporator_pos[1]));

								coordinates = String.valueOf(latitude).replace(',', '.');
								writer.println(String.format("<latitude>%s</latitude>", coordinates));
								//Log.e("convert", String.format("<latitude>%s</latitude>", coordinates));

								coordinates = String.valueOf(longitude).replace(',', '.');
								writer.println(String.format("<longitude>%s</longitude>", coordinates));
								//Log.e("convert", String.format("<longitude>%s</longitude>", coordinates));

								break;
						}
						break;

					default:
						Log.e("i", Integer.toString(xpp.getEventType()));
						break;
				}

				xpp.next();
			}

			writer.println("</hostels>");

			writer.close();
			istream.close();

			renameFile("~~hostels", "hostels.xml");
		}
		catch (Exception ex)
		{
			Log.e("convertHostels()", ex.toString());
		}
	}

	public static void convertShops()
	{
		Log.e("Entering", "convertShops()");
		FileInputStream istream;
		FileOutputStream ostream;
		PrintWriter writer;
		String processingTag = null;
		String processingAttr = null;
		String coordinates;
		String tmp;
		double latitude, longitude;
		int[] separator_pos = new int[2];

		try
		{
			istream = activity.openFileInput("~shops");
			ostream = activity.openFileOutput("~~shops", Activity.MODE_PRIVATE);
			writer = new PrintWriter(ostream);
			xpp.setInput(new InputStreamReader(istream));

			while (true)
			{
				if (xpp.getEventType() == XmlPullParser.END_DOCUMENT)
					break;

				switch (xpp.getEventType())
				{
					case XmlPullParser.START_DOCUMENT:
						writer.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
						writer.println("<shops>");
						break;
					case XmlPullParser.START_TAG:
						processingTag = xpp.getName();
						//Log.e("convertShops()", "START_TAG: " + processingTag);

						switch (processingTag)
						{
							case "Placemark":
								writer.println("<shop>");
								break;
							case "Data":
								processingAttr = xpp.getAttributeValue(0);
								break;
							default:
								break;
						}

						break;

					case XmlPullParser.END_TAG:
						//Log.e("END_TAG", xpp.getName());

						switch (xpp.getName())
						{
							case "Placemark":
								writer.println("</shop>");
								break;
						}

						processingTag = "";

						break;

					case XmlPullParser.TEXT:
						//Log.e("TAG", processingTag);

						switch (processingTag)
						{
							case "name":
								if (xpp.getDepth() == 6)
									writer.println(String.format("<name>%s</name>", xpp.getText()));
								break;
							case "description":
								tmp = Html.escapeHtml(xpp.getText());
								writer.println(String.format("<description>%s</description>", tmp));
								break;
							case "value":
								if (processingAttr.equals("link"))
									writer.println(String.format("<site>%s</site>", xpp.getText()));
								break;
							case "coordinates":
								coordinates = xpp.getText();
								separator_pos[0] = coordinates.indexOf(",");
								separator_pos[1] = coordinates.lastIndexOf(",");
								latitude = Double.parseDouble(coordinates.substring(0, separator_pos[0]));
								longitude = Double.parseDouble(coordinates.substring(separator_pos[0] + 1, separator_pos[1]));

								coordinates = String.valueOf(latitude).replace(',', '.');
								writer.println(String.format("<latitude>%s</latitude>", coordinates));
								//Log.e("convert", String.format("<latitude>%s</latitude>", coordinates));

								coordinates = String.valueOf(longitude).replace(',', '.');
								writer.println(String.format("<longitude>%s</longitude>", coordinates));
								//Log.e("convert", String.format("<longitude>%s</longitude>", coordinates));

								break;
						}
						break;

					default:
						Log.e("i", Integer.toString(xpp.getEventType()));
						break;
				}

				xpp.next();
			}

			writer.println("</shops>");

			writer.close();
			istream.close();

			renameFile("~~shops", "shops.xml");
		}
		catch (Exception ex)
		{
			Log.e("convertShops()", ex.toString());
		}
	}

	public static void convertFish()
	{
		Log.e("Entering", "convertFish()");
		Integer i = 0;
		FileInputStream istream = null;
		FileOutputStream ostream = null;
		PrintWriter writer = null;
		String processingTag = null;
		String processingAttr = null;
		String coordinates = null;
		String tmp;
		double latitude, longitude;
		int[] seporator_pos = new int[2];

		try
		{
			istream = activity.openFileInput("~fishinfo");
			ostream = activity.openFileOutput("~~fishinfo", Activity.MODE_PRIVATE);
			writer = new PrintWriter(ostream);
			xpp.setInput(new InputStreamReader(istream));

			while (true)
			{
				if (xpp.getEventType() == XmlPullParser.END_DOCUMENT)
					break;

				switch (xpp.getEventType())
				{
					case XmlPullParser.START_DOCUMENT:
						writer.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
						writer.println("<fishinfo>");
						break;
					case XmlPullParser.START_TAG:
						processingTag = xpp.getName();
						Log.e("convertFish()", "START_TAG: " + processingTag);

						switch (processingTag)
						{
							case "Placemark":
								writer.println("<shop>");
								break;
							case "Data":
								processingAttr = xpp.getAttributeValue(0);
								break;
							default:
								break;
						}

						break;

					case XmlPullParser.END_TAG:
						//Log.e("END_TAG", xpp.getName());

						switch (xpp.getName())
						{
							case "Placemark":
								writer.println("</shop>");
								break;
						}

						processingTag = "";

						break;

					case XmlPullParser.TEXT:
						//Log.e("TAG", processingTag);

						switch (processingTag)
						{
							case "name":
								if (xpp.getDepth() == 6)
									writer.println(String.format("<name>%s</name>", xpp.getText()));
								break;
							case "description":
								tmp = Html.escapeHtml(xpp.getText());
								writer.println(String.format("<description>%s</description>", tmp));
								break;
							case "value":
								if (processingAttr.equals("link"))
									writer.println(String.format("<site>%s</site>", xpp.getText()));
								break;
							case "coordinates":
								coordinates = xpp.getText();
								seporator_pos[0] = coordinates.indexOf(",");
								seporator_pos[1] = coordinates.lastIndexOf(",");
								latitude = Double.parseDouble(coordinates.substring(0, seporator_pos[0]));
								longitude = Double.parseDouble(coordinates.substring(seporator_pos[0] + 1, seporator_pos[1]));

								coordinates = String.valueOf(latitude).replace(',', '.');
								writer.println(String.format("<latitude>%s</latitude>", coordinates));
								//Log.e("convert", String.format("<latitude>%s</latitude>", coordinates));

								coordinates = String.valueOf(longitude).replace(',', '.');
								writer.println(String.format("<longitude>%s</longitude>", coordinates));
								//Log.e("convertFish", String.format("<longitude>%s</longitude>", coordinates));

								break;
						}
						break;

					default:
						Log.e("i", Integer.toString(xpp.getEventType()));
						break;
				}

				xpp.next();
			}

			writer.println("</shops>");

			writer.close();
			istream.close();

			renameFile("~~shops", "shops.xml");
		}
		catch (Exception ex)
		{
			Log.e("convertShops()", ex.toString());
		}
	}

	private static void renameFile(String _oldname, String _newname)
	{
		try
		{
			FileInputStream istream = activity.openFileInput(_oldname);
			FileOutputStream ostream = activity.openFileOutput(_newname, Activity.MODE_PRIVATE);

			byte[] buffer = new byte[1024];

			while (0 < istream.read(buffer))
			{
				ostream.write(buffer);
			}

			istream.close();
			ostream.close();

			activity.deleteFile(_oldname);
		}
		catch (Exception ex)
		{
			Log.e("renameFile()", ex.toString());
		}
	}

	public static String getTextValue(String _request, String _tagName)
	{
		String retValue;
		String startTag;
		String endTag;
		int startIndex;
		int endIndex;

		if (_request == null || _request.equals(""))
			return null;
		if (_tagName == null || _tagName.equals(""))
			return null;

		retValue = _request;
		startTag = String.format("<%s>", _tagName);
		endTag = String.format("</%s>", _tagName);

		if ((startIndex = retValue.indexOf(startTag)) == -1)
			return null;
		if ((endIndex = retValue.indexOf(endTag)) == -1)
			return null;

		startIndex += startTag.length();
		retValue = retValue.substring(startIndex, endIndex);

		return retValue;
	}
}