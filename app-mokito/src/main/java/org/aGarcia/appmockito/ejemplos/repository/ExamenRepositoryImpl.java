package org.aGarcia.appmockito.ejemplos.repository;

import org.aGarcia.appmockito.ejemplos.Datos;
import org.aGarcia.appmockito.ejemplos.models.Examen;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamenRepositoryImpl implements ExamenRepository{
    @Override
    public Examen guardar(Examen examen) {
        System.out.println("ExamenRepositoryImpl.guardar");
        return Datos.EXAMEN;
    }

    @Override
    public List<Examen> findAll() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.getStackTrace();
            throw new RuntimeException(e);
        }
        System.out.println("ExamenRepositoryImpl.findAll");
        return Datos.EXAMENES;
    }
}
