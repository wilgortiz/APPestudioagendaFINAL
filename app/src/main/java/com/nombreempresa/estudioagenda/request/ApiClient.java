package com.nombreempresa.estudioagenda.request;



import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nombreempresa.estudioagenda.modelos.Actividades;
import com.nombreempresa.estudioagenda.modelos.Apuntes;
import com.nombreempresa.estudioagenda.modelos.Calificaciones;
import com.nombreempresa.estudioagenda.modelos.CambioClave;
import com.nombreempresa.estudioagenda.modelos.Contactos;
import com.nombreempresa.estudioagenda.modelos.Estudiante;
import com.nombreempresa.estudioagenda.modelos.FaltaActualizacionDTO;
import com.nombreempresa.estudioagenda.modelos.Faltas;
import com.nombreempresa.estudioagenda.modelos.Horarios;
import com.nombreempresa.estudioagenda.modelos.LoginResponse;
import com.nombreempresa.estudioagenda.modelos.Materia;
import com.nombreempresa.estudioagenda.modelos.MateriaDto;
import com.nombreempresa.estudioagenda.modelos.Profesor;
import com.nombreempresa.estudioagenda.modelos.RegistroEstudiante;
import com.nombreempresa.estudioagenda.modelos.RegistroResponse;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;



import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


public class ApiClient {

    //public static final String URLBASE = "http://192.168.1.103:5000/"; //notebook wifi
    //public static final String URLBASE = "http://192.168.0.6:5000/";



    //pc escritorio con wifi
    //public static final String URLBASE = "http://192.168.1.107:5000/";
    //public static final String URLBASE = "http://192.168.1.104:5000/";


    //conexion a la api, usamos la ipv4 usando el comando config para visualizarlo
    //protocolo://dominio:puerto/ruta
   // public static final String URLBASE = "http://192.168.0.7:5000/";//con ethernet, puede ser wifi segun lo que usemos
    //recorda que debemos usar la misma ip de  wifi tanto en la pc como en el telefono, osea la misma red de wifi
    //public static final String URLBASE = "http://192.168.1.104:5000/";//con wifi madison
    //public static final String URLBASE = "http://192.168.0.4:5000/"; //con wifi arris
    public static final String URLBASE= "http://192.168.100.23:5000/";//compu nueva, madison b
    private static MisEndpoints mep;
    public static MisEndpoints getEndPoints() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        // Creamos un interceptor para ver el JSON en los logs
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Loguea cuerpo completo

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLBASE)
                .client(client) // ← le pasamos el client con interceptor
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mep = retrofit.create(MisEndpoints.class);
        return mep;
    }


//    public static MisEndpoints getEndPoints() {
//        //Gson gson = new GsonBuilder().setLenient().create();
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss") // Sin indicador de zona horaria
//                .create();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(URLBASE)
//                //conversion de json a objetos java
//                .addConverterFactory(GsonConverterFactory.create(gson))
//
//                .build();
//        mep = retrofit.create(MisEndpoints.class);
//        return mep;
//    }

    public interface MisEndpoints {
        // Login de estudiante
        @FormUrlEncoded
        @POST("Login/Login")
        Call<LoginResponse> login(@Field("email") String email, @Field("password") String password);


        // Endpoint para obtener los detalles del estudiante
        @GET("Login/ObtenerEstudiante")
        Call<Estudiante> obtenerEstudiante(@Header("Authorization") String token);
        //Registro de estudiante
        // @POST("Login/Registro")
       // Call<String> registro(@Body RegistroEstudiante estudiante);

         @POST("Login/Registro")
        Call<RegistroResponse> registro(@Body RegistroEstudiante request);

        @POST("Login/CambiarClave")
        Call<Void> cambiarClave(@Header("Authorization") String token, @Body CambioClave cambioClave);

        @POST("Login/OlvidoClave")
        Call<Void> olvidoClave(@Body String email);

        //APUNTES

            //@GET("Apuntes")
            //Call<List<Apuntes>> obtenerTodosLosApuntes(@Header("Authorization") String token);

             @GET("Apuntes")
            Call<List<Apuntes>> obtenerTodosLosApuntes();

            @GET("Apuntes")
            Call<List<Apuntes>> obtenerApuntes(@Header("Authorization") String token);
             @GET("Apuntes/Obtener/{id}")
             Call<Apuntes> obtenerApuntePorId(@Header("Authorization") String token, @Path("id") int id);

             @POST("Apuntes/Crear")
             Call<Apuntes> crearApunte(@Header("Authorization") String token, @Body Apuntes apunte);

             @PUT("Apuntes/actualizarApunte")
             Call<Apuntes> actualizarApunte(@Header("Authorization")String token, @Body Apuntes apunte);

             @DELETE("Apuntes/Eliminar/{id}")
             Call<Void> eliminarApunte(@Header("Authorization") String token, @Path("id") int id);


             //endpoints para las actividades del estudiante
             @POST("Actividades/Crear")
             Call<Actividades> crearActividad(@Header("Authorization")String token, @Body Actividades actividades);

            @GET("Actividades/obtenerActividades")
            Call<List<Actividades>> obtenerActividades(@Header("Authorization")String token);

            @PUT("Actividades/actualizarActividad")
            Call<Actividades> actualizarActividad(@Header("Authorization")String token, @Body Actividades actividad);

            @DELETE("Actividades/Eliminar/{id}")
            Call<Void> eliminarActividad(@Header("Authorization")String token, @Path("id") int id);



        //MATERIA
//        @POST("Materia/Agregar")
//        Call<Materia> AgregarMaterias(@Header("Authorization") String token, @Body Materia materia);
//        @GET("Materia/obtenerMaterias")
//        Call<List<Materia>> obtenerMaterias (@Header("Authorization")String token);
//
//        @PUT("Materia/actualizarMateria")
//        Call<Materia> actualizarMateria(@Header("Authorization")String token, @Body Materia materia);
//
//        @DELETE("Materia/Eliminar/{id}")
//        Call<Void> eliminarMateria(@Header("Authorization")String token, @Path("id") int id);


        //probando para agregarle profes a la materia
        //MATERIA
        @POST("Materia/Agregar")
        Call<Materia> AgregarMaterias(@Header("Authorization") String token, @Body MateriaDto materia);

        @GET("Materia/obtenerMaterias")
        Call<List<Materia>> obtenerMaterias(@Header("Authorization")String token);

        @PUT("Materia/actualizarMateria")
        Call<Materia> actualizarMateria(@Header("Authorization")String token, @Body MateriaDto materia);

        @DELETE("Materia/Eliminar/{id}")
        Call<Void> eliminarMateria(@Header("Authorization")String token, @Path("id") int id);




        //PROFES
        @POST("Profesores/Agregar")
        Call<Profesor> AgregarProfes(@Header("Authorization")String token, @Body Profesor profesor);

        @GET("Profesores/obtenerProfes")
        Call<List<Profesor>> obtenerProfes (@Header("Authorization")String token);

        @PUT("Profesores/actualizarProfe")
        Call<Profesor> actualizarProfe(@Header("Authorization")String token, @Body Profesor profe);

        @DELETE("Profesores/Eliminar/{id}")
        Call<Void> eliminarProfe(@Header("Authorization")String token, @Path("id") int id);

        //CONTACTOS
        @POST("Contactos/Agregar")
        Call<Contactos> AgregarContactos(@Header("Authorization")String token, @Body Contactos contactos);

        @GET("Contactos/obtenerContactos")
        Call<List<Contactos>> obtenerContactos (@Header("Authorization")String token);

        @PUT("Contactos/actualizarContactos")
        Call<Contactos> actualizarContactos(@Header("Authorization") String token, @Body Contactos contacto);

        @DELETE("Contactos/Eliminar/{id}")
        Call<Void> eliminarContacto(@Header("Authorization")String token, @Path("id") int id);

        //FALTAS
        @GET("Faltas/obtenerFaltas")
        Call<List<Faltas>> obtenerFaltas (@Header("Authorization")String token);

        @PUT("Faltas/actualizarCantidadFalta")
        Call<Void> actualizarCantidadFalta(@Header("Authorization")String token, @Body FaltaActualizacionDTO dto);

        @POST("Faltas/AgregarFaltaMateria")
        Call<Faltas> AgregarFaltaMateria(@Header("Authorization") String token, @Body Faltas falta);

        @DELETE("Faltas/Eliminar/{id}")
        Call<Void> eliminarFaltas(@Header("Authorization")String token, @Path("id") int id);

        //NOTAS
        @POST("Calificaciones/AgregarNotas")
        Call<Calificaciones> AgregarNotas (@Header("Authorization") String token, @Body Calificaciones notas);

        @GET("Calificaciones/obtenerNotas")
        Call<List<Calificaciones>> obtenerNotas(@Header("Authorization") String token);


        @PUT("Calificaciones/actualizarNotas")
        Call<Calificaciones> actualizarNotas(@Header("Authorization") String token, @Body Calificaciones nota);

        @DELETE("Calificaciones/Eliminar/{id}")
        Call<Void> eliminarNotas(@Header("Authorization")String token, @Path("id") int id);

        //Horarios
        @POST("Horarios/AgregarHorariosMateria")
        Call<Horarios> AgregarHorariosMateria(@Header("Authorization") String token, @Body Horarios nuevoHorario);

        @GET("Horarios/obtenerHorarios")
        Call<List<Horarios>> obtenerHorarios(@Header("Authorization") String token);


        @PUT("Horarios/actualizarHorario")
        Call<Horarios> actualizarHorario(@Header("Authorization") String token, @Body Horarios horario);

        @DELETE("Horarios/Eliminar/{id}")
        Call<Void> eliminarHorario(@Header("Authorization")String token, @Path("id") int id);


    }





    //token
    public static void guardarToken(String token, Context contexto) {
        SharedPreferences sp = contexto.getSharedPreferences("token.xml", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token", token);
        editor.apply();
        Log.d("tokeng", "✅ Token guardado: " + token);
        //editor.commit();
    }


    public static String leerToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences("token.xml", 0);
        return sp.getString("token", "");
    }


    public static void borrarToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences("token.xml", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("token");
        editor.apply();
        Log.d("tokeng", "✅ Token borrado correctamente" );
    }

    //opcion 2 con boolean
//    public static boolean borrarToken(Context context) {
//        SharedPreferences sp = context.getSharedPreferences("token.xml", 0);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.remove("token");
//        return editor.commit();
//    }


}