package com.nombreempresa.estudioagenda.ui.materias;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nombreempresa.estudioagenda.modelos.Materia;
import com.nombreempresa.estudioagenda.modelos.MateriaDto;
import com.nombreempresa.estudioagenda.modelos.Profesor;
import com.nombreempresa.estudioagenda.request.ApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MateriasViewModel extends AndroidViewModel {

    private Context context;
    private MutableLiveData<List<Materia>> materiasList;
    private MutableLiveData<List<Materia>> materiasFiltradas;

    public MateriasViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        materiasList = new MutableLiveData<>();
        materiasFiltradas = new MutableLiveData<>();
    }

    public LiveData<List<Materia>> getMateriasList() {
        return materiasList;
    }

    public LiveData<List<Materia>> getMateriasFiltradas() {
        return materiasFiltradas;
    }

    public void obtenerMaterias() {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<List<Materia>> call = api.obtenerMaterias(token);

        Log.d("materias", "Solicitando materias con token: " + token);

        call.enqueue(new Callback<List<Materia>>() {
            @Override
            public void onResponse(Call<List<Materia>> call, Response<List<Materia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    materiasList.setValue(response.body());
                    Log.e("materias", "Respuesta JSON: " + response.body());
                    Log.d("materias", "Respuesta JSON: " + response.body().toString());

                } else {
                    Log.e("API_ERROR", "Código: " + response.code() + " | Mensaje: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Materia>> call, Throwable t) {
                Log.e("API_FAILURE", "Error: ", t);
            }
        });
    }

    public void filtrarMateriasPorPeriodo(String periodo) {
        if (materiasList.getValue() != null) {
            List<Materia> materiasFiltradasList;
            if (periodo.equals("Todos")) {
                materiasFiltradasList = materiasList.getValue();
            } else {
                materiasFiltradasList = materiasList.getValue().stream()
                        .filter(materia -> String.valueOf(materia.getPeriodo()).equals(periodo))
                        .collect(Collectors.toList());
            }
            materiasFiltradas.setValue(materiasFiltradasList);
        }
    }

    public void agregarMateria(Materia materia) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();

        // Crear DTO para enviar al servidor
        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre(materia.getNombre());
        materiaDto.setPeriodo(materia.getPeriodo());

        if (materia.getProfesores() != null) {
            List<Integer> profesoresIds = new ArrayList<>();
            for (Profesor profesor : materia.getProfesores()) {
                profesoresIds.add(profesor.getIdProfesor());
            }
            materiaDto.setProfesoresIds(profesoresIds);
        }

        Call<Materia> call = api.AgregarMaterias(token, materiaDto);
        call.enqueue(new Callback<Materia>() {
            @Override
            public void onResponse(Call<Materia> call, Response<Materia> response) {
                if (response.isSuccessful() && response.body() != null) {
                    obtenerMaterias();
                } else {
                    Log.e("API_ERROR", "Código: " + response.code() + " | Mensaje: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Materia> call, Throwable t) {
                Log.e("API_FAILURE", "Error: ", t);
            }
        });
    }

    public void eliminarMateria(int id) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<Void> call = api.eliminarMateria(token, id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    obtenerMaterias(); // Refrescar la lista
                } else {
                    Log.e("API_ERROR", "Error al eliminar materia: " + response.code());
                    Toast.makeText(context, "Error al eliminar materia", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_FAILURE", "Error: ", t);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void actualizarMateria(Materia materia, Runnable onSuccess) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();

        // Crear DTO para enviar al servidor
        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setIdMateria(materia.getIdMateria());
        materiaDto.setNombre(materia.getNombre());
        materiaDto.setPeriodo(materia.getPeriodo());

        if (materia.getProfesores() != null) {
            List<Integer> profesoresIds = new ArrayList<>();
            for (Profesor profesor : materia.getProfesores()) {
                profesoresIds.add(profesor.getIdProfesor());
            }
            materiaDto.setProfesoresIds(profesoresIds);
        }

        Call<Materia> call = api.actualizarMateria(token, materiaDto);
        call.enqueue(new Callback<Materia>() {
            @Override
            public void onResponse(Call<Materia> call, Response<Materia> response) {
                if (response.isSuccessful() && response.body() != null) {
                    obtenerMaterias(); // Refrescar la lista
                    if (onSuccess != null) {
                        onSuccess.run(); // Ejecutar callback de éxito
                    }
                } else {
                    Log.e("API_ERROR", "Código: " + response.code() + " | Mensaje: " + response.message());
                    Toast.makeText(context, "Error al actualizar materia", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Materia> call, Throwable t) {
                Log.e("API_FAILURE", "Error: ", t);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
//    public void agregarMateria(Materia materia) {
//        String token = ApiClient.leerToken(context);
//        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
//        Call<Materia> call = api.AgregarMaterias(token, materia);
//        call.enqueue(new Callback<Materia>() {
//            @Override
//            public void onResponse(Call<Materia> call, Response<Materia> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    obtenerMaterias();
//                } else {
//                    Log.e("API_ERROR", "Código: " + response.code() + " | Mensaje: " + response.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Materia> call, Throwable t) {
//                Log.e("API_FAILURE", "Error: ", t);
//            }
//        });
//    }



//
//    public void actualizarMateria(Materia materia, final Runnable onComplete) {
//        String token = ApiClient.leerToken(context);
//        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
//        Call<Materia> call = api.actualizarMateria(token, materia);
//
//        call.enqueue(new Callback<Materia>() {
//            @Override
//            public void onResponse(Call<Materia> call, Response<Materia> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    obtenerMaterias(); // Refrescar la lista
//                    if (onComplete != null) {
//                        new android.os.Handler(android.os.Looper.getMainLooper()).post(onComplete);
//                    }
//                } else {
//                    Log.e("API_ERROR", "Error al actualizar materia: " + response.code());
//                    Toast.makeText(context, "Error al actualizar materia", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Materia> call, Throwable t) {
//                Log.e("API_FAILURE", "Error: ", t);
//                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }




//
//
//public class MateriasViewModel extends AndroidViewModel {
//
//    private Context context;
//    private MutableLiveData<List<Materia>> materiasList;
//    private MutableLiveData<List<Materia>> materiasFiltradas;
//
//    public MateriasViewModel(@NonNull Application application) {
//        super(application);
//        context = application.getApplicationContext();
//        materiasList = new MutableLiveData<>();
//        materiasFiltradas = new MutableLiveData<>();
//    }
//
//    public LiveData<List<Materia>> getMateriasList() {
//        return materiasList;
//    }
//
//    public LiveData<List<Materia>> getMateriasFiltradas() {
//        return materiasFiltradas;
//    }
//
//    public void obtenerMaterias() {
//        String token = ApiClient.leerToken(context);
//        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
//        Call<List<Materia>> call = api.obtenerMaterias(token);
//
//        Log.d("materias", "Solicitando materias con token: " + token);
//
//        call.enqueue(new Callback<List<Materia>>() {
//            @Override
//            public void onResponse(Call<List<Materia>> call, Response<List<Materia>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    materiasList.setValue(response.body());
//                    Log.d("materias", "Materias obtenidas: " + response.body().size());
//                } else {
//                    Log.e("API_ERROR", "Código: " + response.code() + " | Mensaje: " + response.message());
//                    Toast.makeText(context, "Error al obtener materias", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Materia>> call, Throwable t) {
//                Log.e("API_FAILURE", "Error: ", t);
//                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    public void filtrarMateriasPorPeriodo(String periodo) {
//        if (materiasList.getValue() != null) {
//            List<Materia> materiasFiltradasList;
//            if (periodo.equals("Todos")) {
//                materiasFiltradasList = materiasList.getValue();
//            } else {
//                materiasFiltradasList = materiasList.getValue().stream()
//                        .filter(materia -> String.valueOf(materia.getPeriodo()).equals(periodo))
//                        .collect(Collectors.toList());
//            }
//            materiasFiltradas.setValue(materiasFiltradasList);
//        }
//    }
//
//    public void agregarMateria(Materia materia) {
//        String token = ApiClient.leerToken(context);
//        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
//
//        // Convertir Materia a MateriaDto
//        MateriaDto materiaDto = new MateriaDto();
//        materiaDto.setNombre(materia.getNombre());
//        materiaDto.setPeriodo(materia.getPeriodo());
//
//        // Agregar IDs de profesores si existen
//        if (materia.getProfesores() != null && !materia.getProfesores().isEmpty()) {
//            List<Integer> profesoresIds = materia.getProfesores().stream()
//                    .map(Profesor::getIdProfesor)
//                    .collect(Collectors.toList());
//            materiaDto.setProfesoresIds(profesoresIds);
//        }
//
//        Call<Materia> call = api.AgregarMaterias(token, materiaDto);
//        call.enqueue(new Callback<Materia>() {
//            @Override
//            public void onResponse(Call<Materia> call, Response<Materia> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    obtenerMaterias(); // Refrescar la lista
//                    Toast.makeText(context, "Materia creada exitosamente", Toast.LENGTH_SHORT).show();
//                } else {
//                    Log.e("API_ERROR", "Código: " + response.code() + " | Mensaje: " + response.message());
//                    Toast.makeText(context, "Error al crear materia", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Materia> call, Throwable t) {
//                Log.e("API_FAILURE", "Error: ", t);
//                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    public void actualizarMateria(Materia materia, final Runnable onComplete) {
//        String token = ApiClient.leerToken(context);
//        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
//
//        // Convertir Materia a MateriaDto
//        MateriaDto materiaDto = new MateriaDto();
//        materiaDto.setIdMateria(materia.getIdMateria());
//        materiaDto.setNombre(materia.getNombre());
//        materiaDto.setPeriodo(materia.getPeriodo());
//
//        // Agregar IDs de profesores si existen
//        if (materia.getProfesores() != null && !materia.getProfesores().isEmpty()) {
//            List<Integer> profesoresIds = materia.getProfesores().stream()
//                    .map(Profesor::getIdProfesor)
//                    .collect(Collectors.toList());
//            materiaDto.setProfesoresIds(profesoresIds);
//        }
//
//        Call<Materia> call = api.actualizarMateria(token, materiaDto);
//        call.enqueue(new Callback<Materia>() {
//            @Override
//            public void onResponse(Call<Materia> call, Response<Materia> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    obtenerMaterias(); // Refrescar la lista
//                    if (onComplete != null) {
//                        new android.os.Handler(android.os.Looper.getMainLooper()).post(onComplete);
//                    }
//                    Toast.makeText(context, "Materia actualizada", Toast.LENGTH_SHORT).show();
//                } else {
//                    Log.e("API_ERROR", "Error al actualizar materia: " + response.code());
//                    Toast.makeText(context, "Error al actualizar materia", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Materia> call, Throwable t) {
//                Log.e("API_FAILURE", "Error: ", t);
//                Toast.makeText(context, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    public void eliminarMateria(int id) {
//        String token = ApiClient.leerToken(context);
//        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
//        Call<Void> call = api.eliminarMateria(token, id);
//
//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    obtenerMaterias(); // Refrescar la lista
//                    Toast.makeText(context, "Materia eliminada", Toast.LENGTH_SHORT).show();
//                } else {
//                    Log.e("API_ERROR", "Error al eliminar materia: " + response.code());
//                    Toast.makeText(context, "Error al eliminar materia", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Log.e("API_FAILURE", "Error: ", t);
//                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

}