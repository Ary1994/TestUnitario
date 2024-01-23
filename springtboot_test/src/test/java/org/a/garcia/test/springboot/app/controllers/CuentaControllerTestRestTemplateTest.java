package org.a.garcia.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jfr.ContentType;
import org.a.garcia.test.springboot.app.model.Cuenta;
import org.a.garcia.test.springboot.app.model.TransaccionDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.tags.HtmlEscapeTag;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
@Tag("integracion_rt")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CuentaControllerTestRestTemplateTest {
    @Autowired
    private TestRestTemplate client;

    private ObjectMapper objectMapper;
    @LocalServerPort
    private int puerto;

    @BeforeEach
    void setUp() {

        objectMapper=new ObjectMapper();
    }

    @Test
    @Order(1)
    void testTrnferir() throws JsonProcessingException {

        //given

        TransaccionDto dto=new TransaccionDto();
        dto.setMonto(new BigDecimal("100"));
        dto.setCuentOrigenId(1L);
        dto.setBancoId(1L);
        dto.setCuentaDestinoId(2L);

        ResponseEntity<String> response = client
                .postForEntity(crearUri("/api/cuentas/transferir"), dto, String.class);
        System.out.println(puerto);
        String json= response.getBody();
        System.out.println(json);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON,response.getHeaders().getContentType());
        assertNotNull(json);
        assertTrue(json.contains("Transferencia realizada con éxito!"));
        assertTrue(json.contains("\"cuentOrigenId\":1,\"cuentaDestinoId\":2,\"monto\":100,\"bancoId\":1"));
        JsonNode jsonNode = objectMapper.readTree(json);
        assertEquals("Transferencia realizada con éxito!",jsonNode.path("mensaje").asText());
        assertEquals(LocalDate.now().toString(),jsonNode.path("date").asText());
        assertEquals("100",jsonNode.path("transaccion").path("monto").asText());
        assertEquals(1L,jsonNode.path("transaccion").path("cuentOrigenId").asLong());
        Map<String, Object> response2 = new HashMap<>();
        response2.put("date", LocalDate.now().toString());
        response2.put("status", "OK");
        response2.put("mensaje", "Transferencia realizada con éxito!");
        response2.put("transaccion", dto);
        assertEquals(objectMapper.writeValueAsString(response2),json);
    }

    @Test
    @Order(2)
    void testDetalle() {

        ResponseEntity<Cuenta> respuesta = client.getForEntity(crearUri("/api/cuentas/1"), Cuenta.class);
        Cuenta cuenta= respuesta.getBody();
        assertEquals(HttpStatus.OK,respuesta.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON,respuesta.getHeaders().getContentType());
        assertNotNull(cuenta);
        assertEquals(1L,cuenta.getId());
        assertEquals("Andres",cuenta.getNombre());
        assertEquals("900.00",cuenta.getSaldo().toPlainString());
       // assertEquals(new Cuenta(1L,"Andres",new BigDecimal("900")),cuenta);
    }

    @Test
    @Order(3)
    void testListar() throws JsonProcessingException{

        ResponseEntity<Cuenta[]> respuesta = client.getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
        assertNotNull(respuesta);
        List<Cuenta> cuentas= Arrays.asList(Objects.requireNonNull(respuesta.getBody()));
        assertEquals(HttpStatus.OK,respuesta.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON,respuesta.getHeaders().getContentType());

        assertEquals(2,cuentas.size());
        assertEquals("Andres",cuentas.get(0).getNombre());
        assertEquals(1L,cuentas.get(0).getId());
        assertEquals("900.00",cuentas.get(0).getSaldo().toPlainString());
        assertEquals("Jhon",cuentas.get(1).getNombre());
        assertEquals(2L,cuentas.get(1).getId());
        assertEquals("2100.00",cuentas.get(1).getSaldo().toPlainString());
        JsonNode json =objectMapper.readTree(objectMapper.writeValueAsString(cuentas));
        assertEquals(1L,json.get(0).path("id").asLong());
        assertEquals("Andres",json.get(0).path("nombre").asText());
        assertEquals("900.0",json.get(0).path("saldo").asText());
        assertEquals(2L,json.get(1).path("id").asLong());
        assertEquals("Jhon",json.get(1).path("nombre").asText());
        assertEquals("2100.0",json.get(1).path("saldo").asText());

    }

    @Test
    @Order(4)
    void testGuardar() {
        Cuenta cuenta=new Cuenta(null,"Pepe",new BigDecimal("3000"));
        ResponseEntity<Cuenta> respuesta = client.postForEntity(crearUri("/api/cuentas"), cuenta, Cuenta.class);
        assertEquals(HttpStatus.CREATED,respuesta.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON,respuesta.getHeaders().getContentType());
        Cuenta cuentaResponse=respuesta.getBody();
        assertNotNull(cuentaResponse);
        assertEquals("Pepe",cuentaResponse.getNombre());
        assertEquals(3L,cuentaResponse.getId());
        assertEquals("3000",cuentaResponse.getSaldo().toPlainString());
    }

    @Test
    @Order(5)
    void testEliminar() {
        ResponseEntity<Cuenta[]> respuesta = client.getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
        assertNotNull(respuesta);
        List<Cuenta> cuentas= Arrays.asList(Objects.requireNonNull(respuesta.getBody()));

        assertEquals(MediaType.APPLICATION_JSON,respuesta.getHeaders().getContentType());

        assertEquals(3,cuentas.size());
        //client.delete(crearUri("/api/cuentas/3"));
        ResponseEntity<Void> exchange = client.exchange(crearUri("/api/cuentas/3"), HttpMethod.DELETE, null, void.class);

        assertEquals(HttpStatus.NO_CONTENT,exchange.getStatusCode());
        assertFalse(exchange.hasBody());

        respuesta = client.getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
        assertNotNull(respuesta);
        cuentas= Arrays.asList(Objects.requireNonNull(respuesta.getBody()));

        assertEquals(MediaType.APPLICATION_JSON,respuesta.getHeaders().getContentType());

        assertEquals(2,cuentas.size());
        ResponseEntity<Cuenta> respuestaDetalle = client.getForEntity(crearUri("/api/cuentas/3"), Cuenta.class);

        assertEquals(HttpStatus.NOT_FOUND,respuestaDetalle.getStatusCode());
        assertFalse(respuestaDetalle.hasBody());
    }

    private String crearUri(String uri){

        return "http://localhost:"+puerto+uri;
    }
}