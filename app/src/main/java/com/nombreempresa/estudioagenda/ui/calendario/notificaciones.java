package com.nombreempresa.estudioagenda.ui.calendario;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.nombreempresa.estudioagenda.MainActivity;
import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.modelos.Actividades;
import com.nombreempresa.estudioagenda.ui.calendario.CalendarioFragment;
public class notificaciones extends BroadcastReceiver {
    private static final String CHANNEL_ID = "event_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Actividades actividad = (Actividades) intent.getSerializableExtra("actividad");

            if (actividad == null) {
                Log.e("Notificaciones", "Actividad es null");
                return;
            }

            createNotificationChannel(context);

            // Intent para abrir la app
            Intent openAppIntent = new Intent(context, MainActivity.class);
            openAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    actividad.getIdEvento(),
                    openAppIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.icono_notificaciones)
                    .setContentTitle("Recordatorio: " + actividad.getTipo_Evento())
                    .setContentText(actividad.getDescripcion())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(actividad.getIdEvento(), notification);

        } catch (Exception e) {
            Log.e("Notificaciones", "Error al mostrar notificación", e);
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Eventos",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notificaciones de eventos del calendario");

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}