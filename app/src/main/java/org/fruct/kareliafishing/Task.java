package org.fruct.kareliafishing;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by doomgiver on 07.04.15.
 */

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
		builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.fishing64));

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(30673, builder.build());
	}
}
