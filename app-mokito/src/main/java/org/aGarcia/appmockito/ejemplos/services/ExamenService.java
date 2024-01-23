package org.aGarcia.appmockito.ejemplos.services;

import org.aGarcia.appmockito.ejemplos.models.Examen;

import java.util.Optional;

public interface ExamenService {
    Optional<Examen> findExamenByName(String name);
    Examen findExamenPorNombreConPreguntas(String name);
    Examen guardar(Examen examen);

}
