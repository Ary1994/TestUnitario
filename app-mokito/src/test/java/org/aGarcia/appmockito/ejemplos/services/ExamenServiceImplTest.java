package org.aGarcia.appmockito.ejemplos.services;

import org.aGarcia.appmockito.ejemplos.Datos;
import org.aGarcia.appmockito.ejemplos.models.Examen;
import org.aGarcia.appmockito.ejemplos.repository.ExamenRepository;
import org.aGarcia.appmockito.ejemplos.repository.ExamenRepositoryImpl;
import org.aGarcia.appmockito.ejemplos.repository.PreguntasRepository;
import org.aGarcia.appmockito.ejemplos.repository.PreguntasRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
//@ExtendWith(MockitoExtension.class)  SI NO ESTA ESTA ANOTATION TIENE QUE ESTAR EN EL CONTRUCTOR
class ExamenServiceImplTest {
    @Mock
    ExamenRepositoryImpl repository;
    @Mock
    PreguntasRepositoryImpl preguntasRepository;
    @InjectMocks
    ExamenServiceImpl service;
    @Captor
    ArgumentCaptor<Long> captor;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); //recordar q si esta aca no va la anotation antes de la clase

        //repository= mock(ExamenRepository.class);
        //preguntasRepository=mock(PreguntasRepository.class);
        //service= new ExamenServiceImpl(repository,preguntasRepository);

    }

    @Test
    void findExamenByName() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        Optional<Examen>  examen=service.findExamenByName("Matematica");



        assertTrue(examen.isPresent());
        assertEquals(5L,examen.get().getId());
        assertEquals("Matematica",examen.get().getNombre());
    }

    @Test
    void findExamenByNameListaVacia() {
        List<Examen> datos= Collections.emptyList();
        when(repository.findAll()).thenReturn(datos);
        Optional<Examen>  examen=service.findExamenByName("Matematica");
        assertFalse(examen.isPresent());

    }

    @Test
    void findExamenByNameConPreguntas() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen=service.findExamenPorNombreConPreguntas("Matematica");
        assertNotNull(examen);
        assertEquals(5,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("derivadas"));

    }

    @Test
    void findExamenByNameConPreguntasVerify() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen=service.findExamenPorNombreConPreguntas("Matematica");
        assertNotNull(examen);
        assertEquals(5,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("derivadas"));
        verify(repository).findAll();
        verify(preguntasRepository).findPreguntasPorExamenId(anyLong());

    }
    @Test
    void noExisteExamenByNameConPreguntasVerify() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen=service.findExamenPorNombreConPreguntas("Matematica");
        assertNull(examen);
        verify(repository).findAll();
        //verify(preguntasRepository).findPreguntasPorExamenId(5L);

    }

    @Test
    void testGuardarExamen() {
        //given dada
        Examen newExamen=Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);
        when(repository.guardar(any(Examen.class))).then(new Answer<Examen>(){
            Long secuencia =8L;
            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen=invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        });
        //when cuando
        Examen examen=service.guardar(newExamen);
        //then entonces
        assertNotNull(examen.getId());
        assertEquals(8L,examen.getId());
        assertEquals("Fisica",examen.getNombre());
        verify(repository).guardar(any(Examen.class));
        verify(preguntasRepository).guardarVarias(anyList());

    }

    @Test
    void testManejoExepciones() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES_ID_NULL);
        when(preguntasRepository.findPreguntasPorExamenId(isNull())).thenThrow(IllegalArgumentException.class);

        Exception exeption=assertThrows(IllegalArgumentException.class,()->{
            service.findExamenPorNombreConPreguntas("Matematica");
        });

        assertEquals(IllegalArgumentException.class,exeption.getClass());

        verify(repository).findAll();
        verify(preguntasRepository).findPreguntasPorExamenId(isNull());


    }

    @Test
    void testArgumentMatchers() {

        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        service.findExamenPorNombreConPreguntas("Matematica");

        verify(repository).findAll();
        //verify(preguntasRepository).findPreguntasPorExamenId(argThat(arg->arg !=null&&arg.equals(5L)));
        verify(preguntasRepository).findPreguntasPorExamenId(eq(5L));

    }
    @Test
    void testArgumentMatchers2() {

        when(repository.findAll()).thenReturn(Datos.EXAMENES);//FALLA SI PONEMOS EXAMENES_ID_NEGATIVO
        when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        service.findExamenPorNombreConPreguntas("Matematica");

        verify(repository).findAll();
        verify(preguntasRepository).findPreguntasPorExamenId(argThat(new MiArgsMatchers()));
        verify(preguntasRepository).findPreguntasPorExamenId(eq(5L));

    }



    @Test
    void testArgumentCaptor() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematica");
        //ArgumentCaptor<Long>captor=ArgumentCaptor.forClass(Long.class);
        verify(preguntasRepository).findPreguntasPorExamenId(this.captor.capture());
        assertEquals(5L,captor.getValue());


    }


    @Test
    void testDothrow() {

        Examen examen=Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);
        //when(preguntasRepository.guardarVarias(anyList())).thenThrow(IllegalArgumentException.class); se hace al reves por que esun metodo void
        doThrow(IllegalArgumentException.class).when(preguntasRepository).guardarVarias(anyList());

        assertThrows(IllegalArgumentException.class,()->{

            service.guardar(examen);
        });
    }

    @Test
    void testDoAnswer() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        doAnswer(invocation->{
            Long id =invocation.getArgument(0);
            return id==5L?Datos.PREGUNTAS:Collections.emptyList();
        }).when(preguntasRepository).findPreguntasPorExamenId(anyLong());
        Examen examen=service.findExamenPorNombreConPreguntas("Matematica");
        assertEquals(5L,examen.getId());
        assertTrue(examen.getPreguntas().contains("geometria"));
        assertEquals("Matematica",examen.getNombre());
        assertEquals(5,examen.getPreguntas().size());
        verify(preguntasRepository).findPreguntasPorExamenId(anyLong());


    }


    @Test
    void testDoAnswerGuardarExamen() {
        //given dada
        Examen newExamen=Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);



        doAnswer(new Answer<Examen>(){
            Long secuencia =8L;
            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen=invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        }).when(repository).guardar(any(Examen.class));
        //when cuando
        Examen examen=service.guardar(newExamen);
        //then entonces
        assertNotNull(examen.getId());
        assertEquals(8L,examen.getId());
        assertEquals("Fisica",examen.getNombre());
        verify(repository).guardar(any(Examen.class));
        verify(preguntasRepository).guardarVarias(anyList());

    }

    @Test
    void testDoCallRealMethod() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
       //when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        doCallRealMethod().when(preguntasRepository).findPreguntasPorExamenId(anyLong());
        Examen examena = service.findExamenPorNombreConPreguntas("Matematica");
       // assertEquals(5L,examen.getId());
        assertEquals("Matematica",examena.getNombre());
    }

    @Test
    void testSpy() {

        ExamenRepository examenRepository =spy(ExamenRepositoryImpl.class);
        PreguntasRepository preguntasRepository=spy(PreguntasRepositoryImpl.class);
        ExamenService examenService= new ExamenServiceImpl(examenRepository,preguntasRepository);
       List<String>pregunta=Arrays.asList("ingles");

        //when(preguntasRepository.findPreguntasPorExamenId(anyLong())).thenReturn(pregunta);
        doReturn(pregunta).when(preguntasRepository).findPreguntasPorExamenId(anyLong());

        Examen examen =examenService.findExamenPorNombreConPreguntas("Matematica");
        assertEquals(5L,examen.getId());
        assertEquals("Matematica",examen.getNombre());
        assertEquals(1,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("ingles"));
        verify(examenRepository).findAll();
        verify(preguntasRepository).findPreguntasPorExamenId(anyLong());
    }

    @Test
    void testOrdenDeInvocaciones() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        service.findExamenPorNombreConPreguntas("Matematica");
        service.findExamenPorNombreConPreguntas("Historia");
        InOrder inOrder = inOrder(preguntasRepository);
        inOrder.verify(preguntasRepository).findPreguntasPorExamenId(5L);
        inOrder.verify(preguntasRepository).findPreguntasPorExamenId(7L);



    }
    @Test
    void testOrdenDeInvocaciones2() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        service.findExamenPorNombreConPreguntas("Matematica");
        service.findExamenPorNombreConPreguntas("Historia");
        InOrder inOrder = inOrder(repository,preguntasRepository);
        inOrder.verify(repository).findAll();
        inOrder.verify(preguntasRepository).findPreguntasPorExamenId(5L);
        inOrder.verify(repository).findAll();
        inOrder.verify(preguntasRepository).findPreguntasPorExamenId(7L);



    }

    @Test
    void testNumeroDeInvocacciones() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        service.findExamenPorNombreConPreguntas("Matematica");
        verify(preguntasRepository).findPreguntasPorExamenId(5L);
        verify(preguntasRepository,times(1)).findPreguntasPorExamenId(5L);
        verify(preguntasRepository,atLeast(1)).findPreguntasPorExamenId(5L);
        verify(preguntasRepository,atLeastOnce()).findPreguntasPorExamenId(5L);
        verify(preguntasRepository,atMost(10)).findPreguntasPorExamenId(5L);
        verify(preguntasRepository,atMostOnce()).findPreguntasPorExamenId(5L);


    }

    @Test
    void testNumeroDeInvocaciones3() {

        when(repository.findAll()).thenReturn(Collections.emptyList());

        service.findExamenPorNombreConPreguntas("Matematica");

        verify(preguntasRepository,never()).findPreguntasPorExamenId(5L);
        verifyNoInteractions(preguntasRepository);
        verify(repository).findAll();
        verify(repository,times(1)).findAll();
        verify(repository,atLeast(1)).findAll();
        verify(repository,atLeastOnce()).findAll();
        verify(repository,atMost(1)).findAll();
        verify(repository,atMostOnce()).findAll();

    }

    public static class MiArgsMatchers implements ArgumentMatcher<Long>{
        private Long argument;
        @Override
        public boolean matches(Long aLong) {
            this.argument=aLong;
            return aLong!=null && aLong>0;
        }

        @Override
        public String toString() {
            return "es para un mensaje personalizado de error "
                    +"que imprime mokito en caso de que falle el test "
                    +this.argument +" debe ser un numero entero positivo";
        }
    }
}