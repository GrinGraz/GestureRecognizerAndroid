package com.example.abdon.gestos;

import android.app.Application;

/**
 * Creado por GRINGRAZ el 10-06-2016.
 */
public class Aplicacion extends Application {
    private static Aplicacion instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static synchronized Aplicacion getAppInstance(){
        return instance;
    }
}
