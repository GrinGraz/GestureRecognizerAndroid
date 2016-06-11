package com.example.abdon.gestos;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Creado por GRINGRAZ el 10-06-2016.
 */
public class Util {
    public static boolean existeConexionInternet() {
        ConnectivityManager cm = (ConnectivityManager) Aplicacion.getAppInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
