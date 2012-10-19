package project.AnRa.Kitchen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class KitchenClientActivity extends Activity {
	private final String URL_0 = "http://soba.cs.man.ac.uk/~sup9/AnRa/php/removeMealOrder.php";
	private final String URL = "http://soba.cs.man.ac.uk/~sup9/AnRa/php/getOrders.php";
	private final String COMPLETE_MEAL_URL = "http://soba.cs.man.ac.uk/~sup9/AnRa/php/removeCookedMeal.php";
	// public List<Meal> mealList = Collections
	// .synchronizedList(new ArrayList<Meal>());
	private ArrayList<Meal> mealList = new ArrayList<Meal>();
	private MealAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	// Checks if there is a connection with the Internet/mobile network
	// Check is done in a separate thread form the UI thread
	private class Check extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting()) {
				return true;
			}
			return false;
		}

		@Override
		protected void onCancelled() {

			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			final ListView lv = (ListView) findViewById(R.id.list);
			LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View v = li.inflate(R.layout.header, null);
			try {
				if (lv.getAdapter() == null) {
					lv.addHeaderView(v);
				}

				if (result) {
					mAdapter = new MealAdapter(KitchenClientActivity.this,
							R.layout.row, mealList);
					lv.setAdapter(mAdapter);
					new GetOrders().execute();

					lv.setOnItemClickListener(new ListView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> a, View v,
								final int position, long l) {

							try {
								DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										switch (which) {
										case DialogInterface.BUTTON_POSITIVE:
											// Removes selected item from list
											// since
											// it's cooked
											// and updates database
											Log.e("Order",
													mealList.get(position-1)
															.getMealName()
															+ " removed");
											String meal_ordered_id = mealList
													.get(position-1)
													.getMealOrderedID();
											Log.e("Order_ID", meal_ordered_id);
											new RemCookedMeal(
													KitchenClientActivity.this)
													.execute(COMPLETE_MEAL_URL,
															meal_ordered_id);

											mealList.remove(position-1);
											mAdapter.notifyDataSetChanged();
											new GetOrders().execute();
											break;

										case DialogInterface.BUTTON_NEGATIVE:
											break;

										}
									}
								};

								AlertDialog.Builder builder = new AlertDialog.Builder(
										KitchenClientActivity.this);
								builder.setMessage(
										"Is "
												+ mealList.get(position-1)
														.getMealName()
												+ " cooked?")
										.setPositiveButton("Yes",
												dialogClickListener)
										.setNegativeButton("No",
												dialogClickListener).show();

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

				} else
					Toast.makeText(KitchenClientActivity.this,
							"Please connect to the internet", Toast.LENGTH_LONG)
							.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (check == null) {
			check = new Check();
			check.execute();
		}
	}

	Check check = null;

	@Override
	protected void onPause() {
		// interrupt check
		if (check != null) {
			check.cancel(true);
			check = null;
		}
		super.onPause();
	}

	private class GetOrders extends AsyncTask<String, Void, HttpGet> {
		private ProgressDialog mProgressDialog = null;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			mProgressDialog = ProgressDialog.show(KitchenClientActivity.this,
					"Please Wait ...", "Retrieving data ...", true);
			super.onPreExecute();
		}

		@Override
		protected HttpGet doInBackground(String... params) {
			final int timeoutConnection = 5000;
			final HttpClient httpclient = new DefaultHttpClient();
			final HttpParams httpParameters = new BasicHttpParams();

			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			// URL_O removes all orders that are created with no meals in them
			final HttpGet httpget0 = new HttpGet(URL_0);
			final HttpGet httpget1 = new HttpGet(URL);

			try {
				HttpResponse response = httpclient.execute(httpget0);
				HttpEntity entity = response.getEntity();
				entity.consumeContent();
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return httpget1;
		}

		@Override
		protected void onPostExecute(HttpGet httpget1) {
			final int timeoutConnection = 5000;
			final HttpClient httpclient = new DefaultHttpClient();
			final HttpParams httpParameters = new BasicHttpParams();
			JsonElement j = null;

			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			HttpResponse result = null;

			try {
				result = httpclient.execute(httpget1);
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (result != null) {
				BufferedReader br = null;
				String json;
				try {
					br = new BufferedReader(new InputStreamReader(result
							.getEntity().getContent()));
					json = "";
					String s;
					while ((s = br.readLine()) != null) {
						json += s;
					}
					j = new JsonParser().parse(json);
				} catch (final IOException e) {
					e.printStackTrace();
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (final IOException e) {
							e.printStackTrace();
						} // catch
					} // if
				} // finally
			}

			if (j != null) {
				mealList.clear();
				try {
					final JsonArray ja = j.getAsJsonArray();

					for (final JsonElement je : ja) {
						final JsonObject jo = je.getAsJsonObject();
						final String meal_ordered_id = jo.getAsJsonPrimitive(
								"meal_ordered_id").getAsString();
						final String meal_id = jo.getAsJsonPrimitive("meal_id")
								.getAsString();
						final String meal_name = jo.getAsJsonPrimitive(
								"meal_name").getAsString();
						final String meal_extra = jo.getAsJsonPrimitive(
								"meal_extra").getAsString();
						final String order_id = jo.getAsJsonPrimitive(
								"order_id").getAsString();

						Meal meal = new Meal(meal_ordered_id, meal_id,
								meal_name, meal_extra, order_id);
						mealList.add(meal);
					}
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
				Collections.sort(mealList, new Comparator<Meal>() {
					@Override
					public int compare(Meal meal1, Meal meal2) {
						// TODO Auto-generated method stub
						if (Integer.parseInt(meal1.getOrderID()) > Integer
								.parseInt(meal2.getOrderID()))
							return 1;
						else if (Integer.parseInt(meal1.getOrderID()) < Integer
								.parseInt(meal2.getOrderID()))
							return -1;
						else
							return 0;

					}
				});

				mAdapter.notifyDataSetChanged();
				mProgressDialog.dismiss();
				super.onPostExecute(httpget1);
				// runThread.run();

			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			new Check().execute();
			break;
		}
		return true;
	}

	/*
	 * GetOrders go = null; Thread runThread = new Thread() {
	 * 
	 * @Override public void run() { Log.e("New Thread", "Trying sleep"); while
	 * (!isInterrupted()) { try { Thread.sleep(120000); Log.e("Sleep",
	 * "1 Second"); if (mealList.isEmpty()) { if (go != null) { go.cancel(true);
	 * go = null; } go = new GetOrders(); go.execute(); } } catch
	 * (InterruptedException e) { e.printStackTrace(); } } } };
	 */
}