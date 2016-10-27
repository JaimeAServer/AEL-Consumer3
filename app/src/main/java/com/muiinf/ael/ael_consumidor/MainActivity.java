package com.muiinf.ael.ael_consumidor;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    final static String fallasString = "http://mapas.valencia.es/lanzadera/opendata/Monumentos_falleros/JSON";
    private static final String TAG = "JSONConsumer";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GetJson().execute();

    }

    private class GetJson extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(fallasString);

            //Log.i(TAG, "Response from url: " + jsonStr);
            int maxLogSize = 1000;
            for(int i = 0; i <= jsonStr.length() / maxLogSize; i++) {
                int start = i * maxLogSize;
                int end = (i+1) * maxLogSize;
                end = end > jsonStr.length() ? jsonStr.length() : end;
                Log.v(TAG, jsonStr.substring(start, end));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
        }

    }
}
