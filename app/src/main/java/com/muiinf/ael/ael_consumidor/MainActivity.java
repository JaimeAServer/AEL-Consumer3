package com.muiinf.ael.ael_consumidor;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.net.Uri;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    final static String fallasString = "http://mapas.valencia.es/lanzadera/opendata/Monumentos_falleros/JSON";

    // Interfaz
    private ProgressDialog pDialog;
    private ListView lv;

    ArrayList<HashMap<String, String>> listaFallas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main);
        //setContentView(R.layout.activity_maps);

        listaFallas = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);


        new GetJson().execute();

    }

    private class GetJson extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Cargando fallas...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(fallasString);

            Log.i(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray fallas = jsonObj.getJSONArray("features");

                    for (int i = 0; i < fallas.length(); i++) {
                        JSONObject falla = fallas.getJSONObject(i);
                        // Phone node is JSON Object
                        JSONObject datos = falla.getJSONObject("properties");
                        JSONObject geometria = falla.getJSONObject("geometry");
                        String id = datos.getString("id");
                        String nombre = datos.getString("nombre");
                        String fallera = datos.getString("fallera");
                        String seccion = datos.getString("seccion");
                        String coordenadas = geometria.getString("coordinates");

                        // tmp hash map for single contact
                        HashMap<String, String> fallaMap = new HashMap<>();

                        // adding each child node to HashMap key => value
                        fallaMap.put("id", id);
                        fallaMap.put("nombre", nombre);
                        fallaMap.put("fallera", fallera);
                        fallaMap.put("seccion", seccion);
                        fallaMap.put("coordenadas", coordenadas);

                        // adding contact to contact list
                        listaFallas.add(fallaMap);
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "Error de parseo JSON: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Error al descargar los datos.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
                }
            } else{
                Log.e(TAG,"Error inicial JSON");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, listaFallas,
                    R.layout.list_item, new String[]{"nombre", "fallera",
                    "seccion", "coordenadas"}, new int[]{R.id.nombre,
                    R.id.fallera, R.id.seccion});

            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                    //Obtener elemento
                    Object item = lv.getItemAtPosition(position);

                    //Tratamiento para arreglar las coordenadas
                    String posicion = item.toString();
                    posicion = posicion.substring(posicion.indexOf("[")+1, posicion.indexOf("]"));
                    double pX = Double.parseDouble(posicion.substring(0,posicion.indexOf(",")));
                    double pY = Double.parseDouble(posicion.substring(posicion.indexOf(",")+1, posicion.length()));

                    //Inventar algo para que cuadre

                    //Instrucciones para abrir google maps
                    Uri gmmIntentUri = Uri.parse("geo:"+pX+","+pY);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });


        }
    }
}
