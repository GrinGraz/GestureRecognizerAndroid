package com.example.abdon.gestos;

import android.app.Dialog;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener{

    private GestureLibrary gesLib;
    private final File file = new File(Environment.getExternalStorageDirectory(),"gestures");
    private GestureOverlayView overlayView;
    private RelativeLayout rl;
    private com.github.nkzawa.socketio.client.Socket socket;
    private TextView pregunta;
    private TextView reconocido;
    private OnRespuestaListener listener;
    private HashMap<String, String> preguntasRespuestas;
    private ArrayList<String> preguntas;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        reconocido = (TextView)findViewById(R.id.reconocido);
        pregunta = (TextView)findViewById(R.id.pregunta);
        rl = (RelativeLayout) findViewById(R.id.relative);
        overlayView = (GestureOverlayView)findViewById(R.id.gestures);


        if (overlayView != null) {
            overlayView.addOnGesturePerformedListener(this);
        }

        gesLib = GestureLibraries.fromFile(file);

        preguntas = new ArrayList<>();
        preguntas.add("1 + 0 = ?");
        preguntas.add("1 + 1 = ?");
        preguntas.add("1 + 2 = ?");
        preguntas.add("1 + 3 = ?");
        preguntas.add("1 + 4 = ?");

        preguntasRespuestas = new HashMap<>();
        preguntasRespuestas.put(preguntas.get(0), "1");
        preguntasRespuestas.put(preguntas.get(1), "2");
        preguntasRespuestas.put(preguntas.get(2), "3");
        preguntasRespuestas.put(preguntas.get(3), "4");
        preguntasRespuestas.put(preguntas.get(4), "5");

        if (!gesLib.load()) {
            //finish();
        }

        pregunta.setText(preguntas.get(index));

        evaluarRespuesta(new OnRespuestaListener() {
            @Override
            public boolean onRespuestaCorrecta() {
                index++;
                if (index == 5){
                    reconocido.setText("Respuesta correcta! Terminado!");
                    Toast.makeText(MainActivity.this, "FIN", Toast.LENGTH_SHORT).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2000);
                    return false;
                }
                Log.d("pregArray", preguntas.get(index)+"");
                Log.d("pregRespHash", preguntasRespuestas.get(preguntas.get(index))+"");
                reconocido.setText("Respuesta correcta! Vamos por la que sigue...");

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pregunta.setText(preguntas.get(index));
                        reconocido.setText(" ");
                    }
                }, 1000);
                return true;

            }

            @Override
            public boolean onRespuestaIncorrecta() {
                reconocido.setText("Respuesta incorrecta :(");
                return false;
            }
        });

        //irInicio();
        //socket.on("nueva pregunta", onNuevaPregunta);
        //Log.d("Log","pase");
    }



    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {

        ArrayList<Prediction> predictions = gesLib.recognize(gesture);
        for (Prediction prediction : predictions) {
            if (index <= preguntas.size()-1){
                if (prediction.name.equals(preguntasRespuestas.get(preguntas.get(index)))) {
                    if (listener.onRespuestaCorrecta()) return;
                } else if (!listener.onRespuestaIncorrecta()) return;
            }
        }
    }

    public void irInicio(){
        if (Util.existeConexionInternet()){
            iniciarConectividad();
        } else {
            final Dialog dialog = DialogHelper.crearDialogAlerta(this, R.layout.dialog_alerta, false, "Revise su conexión", "Sin conexion");
            Button aceptar = (Button) dialog.findViewById(R.id.dialog_aceptar);
            aceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    irInicio();
                    dialog.dismiss();
                }
            });
        }
    }

    private void iniciarConectividad() {
        try {
            socket = IO.socket("http://192.168.114.235:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.connect();
        agendarEventos();
    }

    private void agendarEventos() {
        if(socket != null) {
            Log.d("paso", "paso");
            socket.on("nueva pregunta", onNuevaPregunta);
        }
    }

    private Emitter.Listener onNuevaPregunta = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data = (JSONObject) args[0];
                    String pregunta;
                    try {
                        Log.d("CONECTADO", "pregunta");
                        pregunta = data.getString("mensaje");
                    } catch (JSONException e) {
                        return;
                    }

                    // add the message to view
                    agregarPregunta(pregunta);
                }
            });
        }
    };

    private void evaluarRespuesta(OnRespuestaListener listener){
        this.listener = listener;
    }

    public void agregarPregunta(String pregunta){
        this.pregunta.setText(pregunta);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private interface OnRespuestaListener{
        boolean onRespuestaCorrecta();
        boolean onRespuestaIncorrecta();
    }

}