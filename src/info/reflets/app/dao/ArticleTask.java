package info.reflets.app.dao;


import info.reflets.app.R;
import info.reflets.app.model.Article;
import info.reflets.app.parsing.ArticleParser;
import info.reflets.app.utils.Tools;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.client.ClientProtocolException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/***
 * Asynchrneous task retrieving remote articles
 * @author Alexandre
 *
 */
public class ArticleTask extends AsyncTask<Void, Void, Boolean> {

	public interface OnHeaderTaskListener {
		public void onDownloaded(boolean result, ArrayList<Article> headers);
	}
	
	Context 		mContext;
	
	// A waiting dialog is shown
	boolean 		mShowDialog = false;
	ProgressDialog 	mDialog;
	
	// Article list
	ArrayList<Article> 	mArticles;
	
	OnHeaderTaskListener		mCallback;
	
	public ArticleTask(Context context, boolean showDialog, OnHeaderTaskListener callback){
		mContext 	= context;
		mShowDialog = showDialog;
		mCallback	= callback;
	}
	
	@Override
	protected void onPreExecute() {

		if (mShowDialog){
			mDialog = new ProgressDialog(mContext);
			mDialog.setCancelable(false);
			mDialog.setMessage(mContext.getString(R.string.loading));
			mDialog.show();
		}
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		
		try {
			URL serverAddress = new URL(Tools.RSS_URL);
			URLConnection connection = serverAddress.openConnection();
			connection.connect();
			
			// Parsing
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			
			ArticleParser handler = new ArticleParser();
			parser.parse(connection.getInputStream(), handler);
			
			mArticles = handler.getArticles();
			mArticles = DataCache.getMergedList(mContext, mArticles);
			
		}
		catch (ClientProtocolException e){
			return false;
		}
		catch (IOException e){
			return false;
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if (mShowDialog)
			mDialog.dismiss();
		
		if (mCallback != null)
			mCallback.onDownloaded(result, mArticles);
	}

}
