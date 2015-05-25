package org.fruct.kareliafishing;

import android.content.Intent;
import android.net.Uri;
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
		//NetworkData.sendRequestForResponse(Request.getCategories(""), "~fishinfo");
		//NetworkData.sendRequestForResponse(Request.loadPoints(12), "~lakes");
		NetworkData.sendRequestForResponse(Request.loadPoints(3), "~hostels");
		NetworkData.sendRequestForResponse(Request.loadPoints(1), "~shops");

		Parser.convertHostels();
		Parser.convertShops();

		try
		{
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
