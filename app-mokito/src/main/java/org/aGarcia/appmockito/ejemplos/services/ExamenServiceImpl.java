package org.aGarcia.appmockito.ejemplos.services;

import org.aGarcia.appmockito.ejemplos.models.Examen;
import org.aGarcia.appmockito.ejemplos.repository.ExamenRepository;
import org.aGarcia.appmockito.ejemplos.repository.PreguntasRepository;

import java.util.List;
import java.util.Optional;

public class ExamenServiceImpl implements ExamenService{
    private ExamenRepository examenRepository;
    private PreguntasRepository preguntasRepository;

    public ExamenServiceImpl(ExamenRepository examenRepository, PreguntasRepository preguntasRepository) {
        this.examenRepository = examenRepository;
        this.preguntasRepository = preguntasRepository;
    }

    @Override
    public Optional<Examen> findExamenByName(String name) {
        return  examenRepository.findAll()
                .stream()
                .filter(e-> e.getNombre().contains(name)).findFirst();

    }

    @Override
    public Examen findExamenPorNombreConPreguntas(String name) {
        Optional<Examen>examenOptional=findExamenByName(name);
        Examen examen=null;

        if(examenOptional.isPresent()){
            examen=examenOptional.get();
            List<String> preguntas=preguntasRepository.findPreguntasPorExamenId(examen.getId());
            examen.setPreguntas(preguntas);


        }
        return examen;
    }

    @Override
    public Examen guardar(Examen examen) {
        if(!examen.getPreguntas().isEmpty()){
            preguntasRepository.guardarVarias(examen.getPreguntas());


        }
        return examenRepository.guardar(examen);
    }
}
