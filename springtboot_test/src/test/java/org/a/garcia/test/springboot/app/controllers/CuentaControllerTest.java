package org.a.garcia.test.springboot.app.controllers;

import static org.a.garcia.test.springboot.app.Datos.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.a.garcia.test.springboot.app.model.Cuenta;
import org.a.garcia.test.springboot.app.model.TransaccionDto;
import org.a.garcia.test.springboot.app.service.CuentaService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private CuentaService cuentaService;
    ObjectMapper objectMapper;


    @BeforeEach
    void setUp(){
        objectMapper=new ObjectMapper();

    }
    @Test
    void testDetalle() throws Exception {
        when(cuentaService.findById(1L)).thenReturn(crearCuenta_001().orElseThrow());
        mvc.perform(get("/api/cuentas/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre").value("Andres"))
                .andExpect(jsonPath("$.saldo").value("1000"));

        verify(cuentaService).findById(1L);
    }

    @Test
    void testTranferir() throws Exception {
            //given
        TransaccionDto dto=new TransaccionDto();
        dto.setCuentOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setMonto(new BigDecimal("100"));
        dto.setBancoId(1L);
        System.out.println(objectMapper.writeValueAsString(dto));
        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con éxito!");
        response.put("transaccion", dto);

        System.out.println(objectMapper.writeValueAsString(response));
        //when o contexto
        mvc.perform(post("/api/cuentas/transferir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.mensaje").value("Transferencia realizada con éxito!"))
                .andExpect(jsonPath("$.transaccion.cuentOrigenId").value(dto.getCuentOrigenId()))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));


    }

    @Test
    void testListar() throws Exception {
        List<Cuenta> cuentas= Arrays.asList(crearCuenta_001().orElseThrow()
                ,crearCuenta_002().orElseThrow());
        when(cuentaService.findAll()).thenReturn(cuentas);

        mvc.perform(get("/api/cuentas").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombre").value("Andres"))
                .andExpect(jsonPath("$[1].nombre").value("Jhon"))
                .andExpect(jsonPath("$[0].saldo").value("1000"))
                .andExpect(jsonPath("$[1].saldo").value("2000"))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(cuentas)));
    }

    @Test
    void testGuardar() throws Exception {
        Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));

        when(cuentaService.save(any())).then(invocation ->{
            Cuenta c =invocation.getArgument(0);
            c.setId(3L);
            return c;
        });

        mvc.perform(post("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuenta)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id",is(3)))
                .andExpect(jsonPath("$.nombre",is("Pepe")))
                .andExpect(jsonPath("$.saldo",is(3000)));

        verify(cuentaService).save(any());



    }
}