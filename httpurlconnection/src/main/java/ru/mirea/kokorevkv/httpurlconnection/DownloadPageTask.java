package ru.mirea.kokorevkv.httpurlconnection;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadPageTask extends AsyncTask<String, Void, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity.binding.textView.setText("Загружаем...");

    }


    @Override
    protected String doInBackground(String... strings) {
        try {
            return downloadInfo(strings[0]);
        }catch (IOException e){
            e.printStackTrace();
            return "error";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject result = new JSONObject(s);

            MainActivity.binding.textView.setText(s);
            if(MainActivity.status == 1){
                MainActivity.binding.textView.setText(result.getString("ip"));
                MainActivity.binding.textCity.setText(result.getString("city"));
                MainActivity.binding.textRegion.setText(result.getString("region"));
                MainActivity.binding.textCountry.setText(result.getString("country"));
                MainActivity.binding.textLoc.setText(result.getString("loc"));

            }else {
                MainActivity.binding.textView.setText("Weather:");
                MainActivity.binding.textCity.setText("");
                MainActivity.binding.textRegion.setText("");
                MainActivity.binding.textCountry.setText("");
                MainActivity.binding.textLoc.setText("");
                JSONArray hourlyData = result.getJSONArray("temperature_2m");
                JSONObject firstValue = hourlyData.getJSONObject(0);
                double temp = firstValue.getDouble("value");
                MainActivity.binding.textCity.setText(temp + "");
            }

        } catch (JSONException e) {
            Log.d(getClass().getSimpleName(), "error");
            e.printStackTrace();
        }

    }

    private String downloadInfo(String address) throws IOException {
        InputStream inputStream = null;
        String data = "";
        try {
            URL url = new URL(address);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(100000);
            urlConnection.setConnectTimeout(100000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.setUseCaches(false);
            urlConnection.setDoInput(true);
            int responseCode = urlConnection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                inputStream = urlConnection.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int read = 0;
                while ((read = inputStream.read()) != -1){
                    byteArrayOutputStream.write(read);
                    byteArrayOutputStream.close();
                    data = byteArrayOutputStream.toString();
                }
            } else {
                data = urlConnection.getResponseMessage() + ". Error Code: " + responseCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null){
                inputStream.close();
            }
        }
        return data;
    }
}