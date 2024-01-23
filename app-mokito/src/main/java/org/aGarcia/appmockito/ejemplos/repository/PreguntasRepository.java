package org.aGarcia.appmockito.ejemplos.repository;

import sun.rmi.runtime.Log;

import java.util.List;

public interface PreguntasRepository {

    List<String> findPreguntasPorExamenId(Long id);

    void guardarVarias(List<String> preguntas);
}
