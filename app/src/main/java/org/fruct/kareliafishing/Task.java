package org.fruct.kareliafishing;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.io.File;
import java.net.UnknownHostException;

/**
 * Created by doomgiver on 07.04.15.
 */
public abstract class Task
{
	public static final int MULTIPLE = 1;
	public static final int NO_DUPLICATE = 2;
	public static final int STOP_ON_ERROR = 4;
	public static final int RETRY_ON_ERROR = 8;
	protected int option = 1;
	protected int interval = 10000;
	protected int timeout = 0;
	protected long timeOfLastExecute = 0;
	public abstract int doTask();
}

class GeTSLoginTask extends Task
{
	public GeTSLoginTask()
	{
		this.option = 0;
		this.option |= NO_DUPLICATE;
		this.option |= RETRY_ON_ERROR;
	}

	public int doTask()
	{
		String response;
		String message;
		String redirect_url;
		String id;
		Integer code;

		try
		{
			if (!ApplicationData.getSetting("net_id").equals(""))
				return 0;

			response = NetworkData.sendRequestForResponse(Request.empty());

			if (response == null)
				return -1;

			code = Integer.parseInt(Parser.getTextValue(response, "code"));
			message = Parser.getTextValue(response, "message");

			if (!message.equals("redirect"))
				return code;

			id = Parser.getTextValue(response, "id");
			ApplicationData.addSetting("net_id", id);

			redirect_url = Parser.getTextValue(response, "redirect_url");
			ApplicationData.addSetting("net_redirect_url", redirect_url);

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(redirect_url));
			NetworkData.activity.startActivity(intent);
		}
		catch (NullPointerException ex)
		{
			return -1;
		}
		catch (Exception ex)
		{
			return -2;
		}

		return 0;
	}
}

class WaitForGoogleAuthTask extends Task
{
	public WaitForGoogleAuthTask()
	{
		this.option = Task.MULTIPLE;
		this.option |= NO_DUPLICATE;
	}

	public int doTask()
	{
		String response;
		String message;
		String id = ApplicationData.getSetting("net_id");
		String auth_token = ApplicationData.getSetting("net_token");
		Integer code;

		if (auth_token != null && !auth_token.equals(""))
			return 0;

		if (id == null || id.equals(""))
			return -1;

		response = NetworkData.sendRequestForResponse(Request.login(id));

		if (response == null)
			return -1;

		code = Integer.parseInt(Parser.getTextValue(response, "code"));
		message = Parser.getTextValue(response, "message");

		//NetworkData.msg = response;

		if (!message.equals("success"))
			return code;

		auth_token = Parser.getTextValue(response, "auth_token");

		if (auth_token == null)
			return -1;

		ApplicationData.addSetting("auth_token", auth_token);

		return 0;
	}
}

class UpdateTask extends Task
{
	public UpdateTask()
	{
		this.option = Task.NO_DUPLICATE;
	}

	public int doTask()
	{
		try
		{
			//NetworkData.sendRequestForResponse(Request.getCategories(""), "~fishinfo");
			//NetworkData.sendRequestForResponse(Request.loadPoints(12), "~lakes");
			NetworkData.sendRequestForResponse(Request.loadPoints(3), "~hostels");
			NetworkData.sendRequestForResponse(Request.loadPoints(1), "~shops");

			Parser.convertHostels();
			Parser.convertShops();

			ApplicationData.setHostelsData(Parser.parseHostels());
			ApplicationData.setShopsData(Parser.parseShops());

			return 0;
		}
		catch (Exception ex)
		{
			Log.e("UpdateTask:doTask()", ex.toString());
			return -1;
		}

		//return 0;
	}
}

class Update extends AsyncTask<Void, Integer, Integer>
{
	@Override
	protected Integer doInBackground( Void... params )
	{
		try
		{
			//NetworkData.sendRequestForResponse(Request.getCategories(""), "~fishinfo");
			//NetworkData.sendRequestForResponse(Request.loadPoints(12), "~lakes");
			NetworkData.sendRequestForResponse(Request.loadPoints(3), "~hostels");
			NetworkData.sendRequestForResponse(Request.loadPoints(1), "~shops");

			Parser.convertHostels();
			Parser.convertShops();

			ApplicationData.setHostelsData(Parser.parseHostels());
			ApplicationData.setShopsData(Parser.parseShops());

			return 0;
		}
		catch (Exception ex)
		{
			Log.e("UpdateTask:doTask()", ex.toString());
			return -1;
		}
	}

	@Override
	protected void onPostExecute(Integer result)
	{
		Log.e("Entering", "onPostExecute()");

		Context context = NetworkData.activity.getApplicationContext();

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

		//Intent notificationIntent = new Intent(context, MainActivity.class);
		//PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		//builder.setContentIntent(pendingIntent);
		builder.setContentTitle(context.getString(R.string.update));

		if (0 == result)
			builder.setContentText(context.getString(R.string.updateSuccess));
		else
			builder.setContentText(context.getString(R.string.updateFailure));

		builder.setTicker(context.getString(R.string.update));
		builder.setWhen(System.currentTimeMillis());
		builder.setAutoCancel(true);
		builder.setSmallIcon(R.drawable.ic_drawer);
		builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(30673, builder.build());
	}
}
