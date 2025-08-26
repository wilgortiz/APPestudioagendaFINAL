//package com.nombreempresa.estudioagenda;
//
//import android.os.Bundle;
//import android.view.View;
//import android.view.Menu;
//
//import com.google.android.material.snackbar.Snackbar;
//import com.google.android.material.navigation.NavigationView;
//
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//import androidx.navigation.ui.AppBarConfiguration;
//import androidx.navigation.ui.NavigationUI;
//import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.nombreempresa.estudioagenda.databinding.ActivityMainBinding;
//
//public class MainActivity extends AppCompatActivity {
//
//    private AppBarConfiguration mAppBarConfiguration;
//    private ActivityMainBinding binding;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        //setSupportActionBar(binding.appBarMain.toolbar);
//        /*binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });*/
//        DrawerLayout drawer = binding.drawerLayout;
//        NavigationView navigationView = binding.navView;
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_profes, R.id.nav_contactos)
//                .setOpenableLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
//}


package com.nombreempresa.estudioagenda;



import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import com.google.android.material.navigation.NavigationView;
import com.nombreempresa.estudioagenda.ui.salir.SalirFragment;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.nombreempresa.estudioagenda.databinding.ActivityMainBinding;
import com.nombreempresa.estudioagenda.modelos.Estudiante;
import com.nombreempresa.estudioagenda.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);


        // Solicitar permiso para notificaciones (Android 13+)
        solicitarPermisoNotificaciones();
        // Verificar permisos al iniciar
        verificarPermisosAlarmas();

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profes, R.id.nav_contactos)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        String token = ApiClient.leerToken(this);
        Log.d("MAIN_ACTIVITY", "🔍 Token al iniciar MainActivity: " + (token.isEmpty() ? "VACÍO" : token.substring(0, Math.min(token.length(), 30)) + "..."));
        
        if (!token.isEmpty()) {
            Log.d("MAIN_ACTIVITY", "✅ Token válido encontrado, obteniendo datos del estudiante");
            obtenerEstudiante(token);
        } else {
            Log.d("MAIN_ACTIVITY", "⚠️ No hay token guardado, el usuario debe loguearse");
        }
    }

    private void obtenerEstudiante(String token) {
        ApiClient.getEndPoints().obtenerEstudiante( token).enqueue(new Callback<Estudiante>() {
            @Override
            public void onResponse(Call<Estudiante> call, Response<Estudiante> response) {
                if (response.isSuccessful()) {
                    Estudiante estudiante = response.body();
                    actualizarUI(estudiante);
                } else {
                    // Manejamos el error
                    Log.e("API_ERROR", "Error en la respuesta: " + response.code());
                    // mostramos un mensaje de error
                    Toast.makeText(MainActivity.this, "Error al obtener datos del estudiante", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Estudiante> call, Throwable t) {
                Log.e("API_FAILURE", "Fallo en la llamada", t);
                Toast.makeText(MainActivity.this, "No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void actualizarUI(Estudiante estudiante) {
        NavigationView navigationView = binding.navView;
        if (navigationView.getHeaderCount() > 0) {
            View headerView = navigationView.getHeaderView(0);
            TextView nombreTextView = headerView.findViewById(R.id.textView);
            TextView correoTextView = headerView.findViewById(R.id.textView2);

            nombreTextView.setText(estudiante.getNombre() + " " + estudiante.getApellido()); // Agrega un texto antes del nombre
            correoTextView.setText( estudiante.getEmail()); // Agrega un texto antes del correo
        } else {
            Log.d("Error", "No hay headers en el NavigationView");
        }
    }
    private void solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "event_channel",
                    "Eventos",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificaciones de eventos del calendario");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Android 13+ → pedir permiso en tiempo de ejecución
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        101
                );
            }
        }
    }


//    private void solicitarPermisoNotificaciones() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    "event_channel",
//                    "Eventos",
//                    NotificationManager.IMPORTANCE_HIGH  // Cambiado a HIGH para mayor prioridad
//            );
//            channel.setDescription("Notificaciones de eventos del calendario");
//
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel);
//        }
//    }

    private void verificarPermisosAlarmas() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                // Mostrar explicación al usuario antes de redirigir a configuración
                new AlertDialog.Builder(this)
                        .setTitle("Permiso necesario")
                        .setMessage("La aplicación necesita permiso para programar recordatorios exactos")
                        .setPositiveButton("Aceptar", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el menu. esto agrega elementos a la barra de acción si está presente.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    //para salir de la app tambien, usamos los 3 puntos
    //de la barra superior a la derecha
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            SalirFragment.mostrarDialogo(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


}