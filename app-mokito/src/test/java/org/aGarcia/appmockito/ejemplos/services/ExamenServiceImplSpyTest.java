package org.aGarcia.appmockito.ejemplos.services;

import org.aGarcia.appmockito.ejemplos.Datos;
import org.aGarcia.appmockito.ejemplos.models.Examen;
import org.aGarcia.appmockito.ejemplos.repository.ExamenRepository;
import org.aGarcia.appmockito.ejemplos.repository.ExamenRepositoryImpl;
import org.aGarcia.appmockito.ejemplos.repository.PreguntasRepository;
import org.aGarcia.appmockito.ejemplos.repository.PreguntasRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplSpyTest {
    @Spy
    ExamenRepositoryImpl repository;

    @Spy
    PreguntasRepositoryImpl preguntaRepository;

    @InjectMocks
    ExamenServiceImpl service;


    @Test
    void testSpy() {

       List<String> preguntas =Arrays.asList("ingles");

        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
      // doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());
        doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen =service.findExamenPorNombreConPreguntas("Matematica");
        assertEquals(5L,examen.getId());
        assertEquals("Matematica",examen.getNombre());
        assertEquals(1,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("ingles"));
       verify(repository).findAll();
       verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }


}