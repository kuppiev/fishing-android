package org.fruct.kareliafishing;

import android.app.Activity;
import android.text.Html;
import android.util.Log;

import java.io.BufferedReader;
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
	public static String tmp = null;

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

			for (Integer i = 0; i < source_files.length; i++)
			{
				f = new File(activity.getFilesDir().getAbsolutePath() + "/" + source_files[i]);

				if (!f.exists()) {
					Log.e("Info", activity.getFilesDir().getAbsolutePath() + "/" + source_files[i]);
					saveXmlInDeviceMemory(i + 1);
				}
			}

			saveXmlInDeviceMemory();

			init = true;
		}
		catch (Exception ex)
		{
			Log.e("Error", ex.toString());
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
			/*
			case ObjectData.BEHAVIOR_RULE:
				istream = activity.getResources().openRawResource(R.raw.shops);
				break;

			case ObjectData.FISHING_RULE:
				istream = activity.getResources().openRawResource(R.raw.shops);
				break;

			case ObjectData.RECIPE:
				istream = activity.getResources().openRawResource(R.raw.shops);
				break;
			*/
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

	private static void saveXmlInDeviceMemory() throws Exception
	{
		String tmp;
		InputStream istream;
		byte[] buffer = new byte[2048];

		FileOutputStream ostream = activity.openFileOutput("~hostels", Activity.MODE_PRIVATE);
		istream = activity.getResources().openRawResource(R.raw._hostels);
		PrintWriter writer = new PrintWriter(ostream);
		InputStreamReader isr = new InputStreamReader(istream);
		BufferedReader reader = new BufferedReader(isr);

		while (null != (tmp = reader.readLine()))
		{
			tmp = tmp.replace(String.valueOf(Character.toChars(8260)), "/");
			writer.println(tmp);
		}

		writer.close();
		reader.close();

		Log.e("info", "File \"~hostels\"");
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
			Log.e("Error", ex.toString());
		}

		return retValue;
	}

	public static ArrayList<ObjectData> parseHostels2() throws Exception
	{
		ArrayList<ObjectData> retValue = null;
		Integer i = 0;
		InputStream istream = null;
		String processingTag = null;
		ObjectData processingObject = null;


			retValue = new ArrayList<ObjectData>();
			istream = activity.openFileInput("~~hostels");
			Log.e("parser", "~~hostels");
			xpp.setInput(new InputStreamReader(istream));

			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT)
			{
				try
				{

				switch (xpp.getEventType())
				{
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						processingTag = xpp.getName();

						Log.e("START_TAG", processingTag);

						if (processingTag.equals("hostel"))
							processingObject = new ObjectData(ObjectData.HOSTEL);

						break;

					case XmlPullParser.END_TAG:
						Log.e("END_TAG", xpp.getName());

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
				catch (Exception ex)
				{
					Log.e("Error", ex.toString());
				}
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
			Log.e("Error", ex.toString());
		}

		return retValue;
	}

	public static ArrayList<ObjectData> parseFish()
	{
		ArrayList<ObjectData> retValue = null;
		Integer i = 0;
		InputStream istream = null;
		String processingTag = null;
		ObjectData processingObject = null;

		try
		{
			retValue = new ArrayList<ObjectData>();
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
			Log.e("Error", ex.toString());
		}

		return retValue;
	}

	public static ArrayList<ObjectData> parseLakes()
	{
		ArrayList<ObjectData> retValue = null;
		Integer i = 0, points_count = 0, fish_count = 0;
		InputStream istream = null;
		String processingTag = null;
		ObjectData processingObject = null;

		try
		{
			retValue = new ArrayList<ObjectData>();
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
			Log.e("Error", ex.toString());
		}

		return retValue;
	}

	public static void convertHostels()
	{
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
			istream = activity.openFileInput("~hostels");
			ostream = activity.openFileOutput("~~hostels", Activity.MODE_PRIVATE);
			writer = new PrintWriter(ostream);
			xpp.setInput(new InputStreamReader(istream));

			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()) {
					case XmlPullParser.START_DOCUMENT:
						writer.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
						writer.println("<hostels>");
						break;
					case XmlPullParser.START_TAG:
						processingTag = xpp.getName();
						Log.e("START_TAG", processingTag);

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
						Log.e("END_TAG", xpp.getName());

						switch (xpp.getName())
						{
							case "Placemark":
								writer.println("</hostel>");
								break;
						}

						processingTag = "";

						break;

					case XmlPullParser.TEXT:
						Log.e("TAG", processingTag);

						switch (processingTag)
						{
							case "name":
								Log.e("namespace", String.valueOf(xpp.getDepth()));
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
								Log.e("convert", String.format("<latitude>%s</latitude>", coordinates));

								coordinates = String.valueOf(longitude).replace(',', '.');
								writer.println(String.format("<longitude>%s</longitude>", coordinates));
								Log.e("convert", String.format("<longitude>%s</longitude>", coordinates));

								break;
						}
						break;

					case XmlPullParser.END_DOCUMENT:
						writer.println("</hostels>");
						break;

					default:
						Log.e("i", Integer.toString(xpp.getEventType()));
						break;
				}

				xpp.next();
			}

			writer.close();
			istream.close();
		}
		catch (Exception ex)
		{
			Log.e("Error", ex.toString());
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