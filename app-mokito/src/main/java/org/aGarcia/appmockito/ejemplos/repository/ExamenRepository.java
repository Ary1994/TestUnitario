package org.aGarcia.appmockito.ejemplos.repository;

import org.aGarcia.appmockito.ejemplos.models.Examen;
import org.aGarcia.appmockito.ejemplos.services.ExamenService;

import java.util.List;

public interface ExamenRepository {
    Examen guardar(Examen examen);
    List<Examen> findAll();
}
