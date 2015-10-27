package com.example.android.direccionesprueba;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class RutaIntentService extends IntentService {

    private Parcelable miReciver;
    private LatLng origen;
    private LatLng puntoA;
    private LatLng puntoB;
    private LatLng puntoC;
    private String url;
    private JSONObject miJson;
    private ResultReceiver mReceiver;
    private ArrayList<String> guia = new ArrayList<>();

    public RutaIntentService() {
        super("RutaIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            mReceiver = intent.getParcelableExtra("receiver");
            origen = intent.getParcelableExtra("origen");
            puntoA = intent.getParcelableExtra("puntoA");
            puntoB = intent.getParcelableExtra("puntoB");
            puntoC = intent.getParcelableExtra("puntoC");

            final String URL_BASE = "https://maps.googleapis.com/maps/api/directions/json?";
            final String ORIGIN = "origin";
            final String DESTINATION = "destination";
            final String WAYPOINTS = "waypoints";
            final String KEY = "key";
            final String LANGUAGE = "language";

            String waypoints = "optimize:true|" + String.valueOf(puntoA.latitude) + "," + String.valueOf(puntoA.longitude) + "|" + String.valueOf(puntoB.latitude) + "," + String.valueOf(puntoB.longitude);
            String origin = String.valueOf(origen.latitude) + "," + String.valueOf(origen.longitude);
            String destination = String.valueOf(puntoC.latitude) + "," + String.valueOf(puntoC.longitude);
            String language = "es";

            Uri.Builder builder = new Uri.Builder();

            builder.scheme("https")
                    .authority("maps.googleapis.com")
                    .appendPath("maps")
                    .appendPath("api")
                    .appendPath("directions")
                    .appendPath("json")
                    .appendQueryParameter(ORIGIN, origin)
                    .appendQueryParameter(DESTINATION, destination)
                    .appendQueryParameter(WAYPOINTS, waypoints)
                    .appendQueryParameter(LANGUAGE,language)
                    .appendQueryParameter(KEY, "AIzaSyCLMiBZXLDO2WCQoyi66Q2uxgRKNrRalmg");

            url = builder.build().toString();

            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


            StringBuilder response = new StringBuilder();

            if (networkInfo != null && networkInfo.isConnected()) {

                try {
                    URL Url = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
                    conn.connect();

                    BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8192);
                    String strLine = null;

                    while ((strLine = input.readLine()) != null) {
                        response.append(strLine);
                    }
                    input.close();

                    String jsonOutput = response.toString();

                    miJson = new JSONObject(jsonOutput);


                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            JSONArray rutasArray;
            JSONArray legsArray;
            JSONObject ruta;
            JSONObject leg;
            JSONArray stepsArray;
            List<String> steps = new ArrayList<>();
            List<List<LatLng>> polylines = new ArrayList<>();
            List<LatLng> listaPoints = new ArrayList<>();
            List<LatLng> points = new ArrayList<>();

            if (miJson != null) {

                try {
                    rutasArray = miJson.getJSONArray("routes");
                    ruta = rutasArray.getJSONObject(0);
                    legsArray = ruta.getJSONArray("legs");

                    for (int i = 0; i < legsArray.length(); i++) {

                        leg = legsArray.getJSONObject(i);
                        stepsArray = leg.getJSONArray("steps");

                        for (int j = 0; j < stepsArray.length(); j++) {
                            JSONObject step = stepsArray.getJSONObject(j);
                            guia.add(step.getString("html_instructions"));
                            listaPoints = PolyUtil.decode(step.getJSONObject("polyline").getString("points"));
                            points.addAll(listaPoints);
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            Bundle bundle = new Bundle();
            bundle.putStringArrayList("guia", guia);
            bundle.putParcelableArrayList("points", (ArrayList<? extends Parcelable>) points);
            mReceiver.send(0, bundle);


        }


    }
}

