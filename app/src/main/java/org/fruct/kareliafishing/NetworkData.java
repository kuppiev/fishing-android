package org.fruct.kareliafishing;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by doomgiver on 24.03.15.
 */
class NetworkData
{
	public static volatile String msg = null;
	private static ArrayList<Task> tasks = null;
	private static boolean init = false;
	protected static String id = null;
	protected static String token = null;
	protected static Activity activity = null;

	private NetworkData(){}

	public static void initialize(Activity _activity) throws Exception
	{
		if (_activity == null)
			throw new Exception("Activity is null");

		activity = _activity;

		loadNetworkSettings();

		init = true;
	}

	public static boolean isInitialized()
	{
		return init;
	}

	protected static void sendRequestForResponse(Request _request, String responseFile) throws Exception
	{
		URL url;
		HttpURLConnection connection;
		PrintWriter out;

		try
		{
			url = new URL(_request.protocol, _request.hostname, _request.port, _request.file);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod(_request.method);
			connection.setDoOutput(true);
			connection.connect();

			out = new PrintWriter(connection.getOutputStream());
			out.println(_request.request);
			out.close();

			listen(connection, responseFile);
		}
		catch (Exception ex)
		{
			Log.e("sendRequest()", ex.toString());
			throw ex;
		}
	}

	protected static String sendRequestForResponse(Request _request)
	{
		URL url;
		HttpURLConnection connection;
		PrintWriter out;

		try
		{
			url = new URL(_request.protocol, _request.hostname, _request.port, _request.file);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod(_request.method);
			connection.setDoOutput(true);
			connection.connect();

			out = new PrintWriter(connection.getOutputStream());
			out.println(_request.request);
			out.close();

			return listen(connection);
		}
		catch (Exception ex)
		{
			Log.e("sendRequest()", ex.toString());
		}

		return null;
	}

	private static void listen(HttpURLConnection _connection, String responseFile) throws Exception
	{
		FileOutputStream ostream = activity.openFileOutput(responseFile, Activity.MODE_PRIVATE);
		PrintWriter writer = new PrintWriter(ostream);
		BufferedReader in = new BufferedReader(new InputStreamReader(_connection.getInputStream()));
		String inputLine;

		File f = new File("/storage/sdcard0/" + responseFile + ".txt");
		PrintWriter writer2 = new PrintWriter(new FileOutputStream(f));

		while (true)
		{
			if ((inputLine = in.readLine()) != null)
			{
				inputLine = inputLine.replace("%3A", ":");
				inputLine = inputLine.replace("%2F", "/");
				inputLine = inputLine.replace("&amp;", "&");

				writer.println(inputLine);
				writer2.println(inputLine);

				Log.e("listen", inputLine);

				if (inputLine.endsWith("</response>"))
					break;
			}
		}

		in.close();
		writer.close();
		writer2.close();
	}

	private static String listen(HttpURLConnection _connection) throws Exception
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(_connection.getInputStream()));
		String inputLine;
		String retValue = "";

		while (true)
		{
			if ((inputLine = in.readLine()) != null)
			{
				retValue += inputLine;

				if (inputLine.endsWith("</response>"))
					break;
			}
		}

		retValue = retValue.replace("%3A", ":");
		retValue = retValue.replace("%2F", "/");
		retValue = retValue.replace("&amp;", "&");

		in.close();

		return retValue;
	}

	private static void loadNetworkSettings()
	{
		NetworkData.id = ApplicationData.getSetting("net_id");
		NetworkData.token = ApplicationData.getSetting("net_token");
	}
}

class Request
{
	protected String file;
	protected String method;
	protected String request;
	protected String protocol;
	protected String hostname;
	protected int port;

	private Request() {}

	public static Request empty()
	{
		Request request = new Request();
		request.method = "POST";
		request.request = "<request><params></params></request>";
		request.protocol = "http";
		request.hostname = NetworkData.activity.getString(R.string.hostname);
		request.file = "/gets/service/userLogin.php";
		request.port = 80;

		return request;
	}

	public static Request login(String id)
	{
		Request request = new Request();
		request.method = "POST";
		request.request = String.format("<request><params><id>%s</id></params></request>", id);
		request.protocol = "http";
		request.hostname = NetworkData.activity.getString(R.string.hostname);
		request.file = "/gets/service/userLogin.php";
		request.port = 80;

		return request;
	}

	public static Request getCategories(String token)
	{
		Request request = new Request();
		request.method = "POST";
		if (token == null)
			request.request = "<request><params><auth_token></auth_token></params></request>";
		else
			request.request = String.format("<request><params><auth_token>%s</auth_token></params></request>", token);
		request.protocol = "http";
		request.hostname = NetworkData.activity.getString(R.string.hostname);
		request.file = "/gets/service/getCategories.php";
		request.port = 80;

		return request;
	}

	public static Request loadPoints(long category_id)
	{
		Request request = new Request();
		request.method = "POST";
		request.request = String.format("<request><params><category_id>%d</category_id></params></request>", category_id);
		request.protocol = "http";
		request.hostname = NetworkData.activity.getString(R.string.hostname);
		request.file = "/gets/service/loadPoints.php";
		request.port = 80;

		return request;
	}
}
