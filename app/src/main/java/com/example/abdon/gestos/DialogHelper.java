package com.example.abdon.gestos;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Creado por GRINGRAZ el 10-06-2016.
 */
public class DialogHelper {

    public static Dialog crearDialogAlerta(final Context context, int layout, boolean cerrarClickFuera, String mensaje, String titulo) {

        Typeface thin = Typeface.createFromAsset(context.getAssets(),"fonts/HelveticaNeue-Thin.otf");

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.setCancelable(cerrarClickFuera);
        dialog.setCanceledOnTouchOutside(cerrarClickFuera);

        TextView textoDialog = (TextView) dialog.findViewById(R.id.mensaje);
        TextView tituloDialog = (TextView) dialog.findViewById(R.id.titulo_tema);
        Button btnAceptar = (Button) dialog.findViewById(R.id.dialog_aceptar);

        textoDialog.setTypeface(thin);
        tituloDialog.setTypeface(thin);
        btnAceptar.setTypeface(thin);

        textoDialog.setText(mensaje);
        tituloDialog.setText(titulo);

        dialog.show();
        return dialog;
    }
}
