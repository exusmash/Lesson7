package ru.mirea.kokorevkv.httpurlconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import ru.mirea.kokorevkv.httpurlconnection.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    static ActivityMainBinding binding;
    public static String[] weather_params = {"temperature_2m_max_member01", "temperature_2m_max_member02"};
    public static int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btn.setOnClickListener(v -> {
            status = 1;
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();

            }
            if (networkInfo != null && networkInfo.isConnected()) {
                DownloadPageTask downloadPageTask = new DownloadPageTask();
                downloadPageTask.execute("https://ipinfo.io/json");


            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Нет подключения к интернету ", Toast.LENGTH_SHORT);
                toast.show();
                Log.d(TAG, "нет подключения к интернету");
            }
        });
        binding.btn2.setOnClickListener(v -> {
            status = 2;
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();

            }
            if (networkInfo != null && networkInfo.isConnected()) {
                DownloadPageTask downloadPageTask = new DownloadPageTask();
                String[] loc = binding.textLoc.getText().toString().split(",");
                String url =  "https://api.open-meteo.com/v1/forecast?latitude=" + loc[0] + "&longitude=" + loc[1] + "&hourly=temperature_2m";
                downloadPageTask.execute(url);


            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Нет подключения к интернету ", Toast.LENGTH_SHORT);
                toast.show();
                Log.d(TAG, "нет подключения к интернету");
            }
        });
    }
}