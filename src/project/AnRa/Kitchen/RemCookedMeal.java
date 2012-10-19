package project.AnRa.Kitchen;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonElement;

public class RemCookedMeal extends AsyncTask<String, Void, JsonElement> {
	private ProgressDialog mProgressDialog = null;
	private final Context mContext;

	public RemCookedMeal(Context c) {
		mContext = c;
	}

	@Override
	protected void onPreExecute() {
		Log.e("Order", "PREEXECUTE");
		mProgressDialog = ProgressDialog.show(mContext, "Please Wait ...",
				"Removing meal...", true);
		super.onPreExecute();
	}

	@Override
	protected JsonElement doInBackground(String... params) {
		final int timeoutConnection = 5000;
		final HttpClient httpclient = new DefaultHttpClient();
		final HttpParams httpParameters = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		final HttpPost httppost = new HttpPost(params[0]);

		Log.e("Order", "BG1");
		HttpResponse result = null;
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id",
					params[1].toString()));
			Log.e("nvp", "param1:" + params[1].toString());
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			result = httpclient.execute(httppost);
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.e("nvp", "param1:" + result.toString());
		return null;
	}
	
	@Override
	protected void onPostExecute(JsonElement result) {
		super.onPostExecute(result);
		mProgressDialog.dismiss();
	}
}