package info.reflets.app.utils;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.WindowManager;


public class Tools {

	public static final String RSS_URL = "http://feeds.feedburner.com/refletsinfo?format=xml";
	
	private static final String RSS_DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";
	
	public static int DEVICE_WIDTH;

	public static void init(Context context){
		WindowManager mWinMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		DEVICE_WIDTH = mWinMgr.getDefaultDisplay().getWidth();
	}
	
	
	/***
	 * Parse date from the rss format
	 * @param dateStr
	 * @return
	 */
	public static Calendar parseDate(String dateStr){
		SimpleDateFormat dFormat = new SimpleDateFormat(RSS_DATE_FORMAT, Locale.US);					
		Calendar calendar = Calendar.getInstance();
		
		try {
			// Parsing date
			Date date = dFormat.parse(dateStr);
			calendar.setTime(date);			
		} catch (Exception e) {
			Log.d("Tools", e.getMessage());
		}
		
		return calendar;
	}
	
	public static String urlEncode(String url){

		try {
			if (url.contains("/")) {
				String newUrl;
				int leftBound = url.lastIndexOf("/");
				newUrl = url.subSequence(0, leftBound+1).toString();
				newUrl += URLEncoder.encode(url.subSequence(leftBound+1, url.length()).toString());
				return newUrl;
			}
		}
		catch (Exception e) {}
		return url;
	}
	/***
	 * True if network is available, otherwise false
	 * @param context
	 * @return
	 */
	public static boolean isConnected(Context context) {

	    NetworkInfo info = (NetworkInfo) ((ConnectivityManager) context
	            .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

	    return (info != null && info.isConnected());
	}
	
	public static Bitmap openContactPhotoInputStream(Context context, Uri contactUri) {
		
		return BitmapFactory.decodeStream(ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), contactUri));
		
	}
}
