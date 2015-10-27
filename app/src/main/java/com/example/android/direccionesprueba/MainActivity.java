package com.example.android.direccionesprueba;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private LatLng origen;
    private LatLng puntoA;
    private LatLng puntoB;
    private LatLng puntoC;

    private ArrayList<String> listaDirecciones;
    private RutaResultReceiver rutaResultReceiver;
    private ArrayList<String> guia;
    private ArrayList<LatLng> points;
    private GoogleMap miMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        origen = new LatLng(4.703791, -74.032728);
        puntoA = new LatLng(4.678602, -74.042104);
        puntoB = new LatLng(4.678534, -74.044978);
        puntoC = new LatLng(4.682151, -74.048174);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        rutaResultReceiver = new RutaResultReceiver(new Handler());

        Intent intent = new Intent(this, RutaIntentService.class);
        intent.putExtra("receiver", rutaResultReceiver);
        intent.putExtra("origen", origen);
        intent.putExtra("puntoA", puntoA);
        intent.putExtra("puntoB", puntoB);
        intent.putExtra("puntoC", puntoC);
        startService(intent);



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        miMap = googleMap;
        miMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(puntoC.latitude,puntoC.longitude), 13));
        miMap.addMarker(new MarkerOptions().position(new LatLng(puntoA.latitude,puntoA.longitude)).title("A"));
        miMap.addMarker(new MarkerOptions().position(new LatLng(puntoB.latitude,puntoB.longitude)).title("B"));
        miMap.addMarker(new MarkerOptions().position(new LatLng(puntoC.latitude, puntoC.longitude)).title("C"));
    }


    private class RutaResultReceiver extends ResultReceiver {
        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public RutaResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            guia = resultData.getStringArrayList("guia");
            points = resultData.getParcelableArrayList("points");

            Polyline ruta = miMap.addPolyline(new PolylineOptions().addAll(points));

            TextView paso1 = (TextView) findViewById(R.id.paso1);
            paso1.setText(Html.fromHtml(guia.get(0)));
            TextView paso2 = (TextView) findViewById(R.id.paso2);
            paso2.setText(Html.fromHtml(guia.get(1)));
            TextView paso3 = (TextView) findViewById(R.id.paso3);
            paso3.setText(Html.fromHtml(guia.get(2)));
            TextView paso4 = (TextView) findViewById(R.id.paso4);
            paso4.setText(Html.fromHtml(guia.get(3)));
            TextView paso5 = (TextView) findViewById(R.id.paso5);
            paso5.setText(Html.fromHtml(guia.get(4)));
            TextView paso6 = (TextView) findViewById(R.id.paso6);
            paso6.setText(Html.fromHtml(guia.get(5)));
            TextView paso7 = (TextView) findViewById(R.id.paso7);
            paso7.setText(Html.fromHtml(guia.get(6)));
            TextView paso8 = (TextView) findViewById(R.id.paso8);
            paso8.setText(Html.fromHtml(guia.get(7)));
        }


    }
}
