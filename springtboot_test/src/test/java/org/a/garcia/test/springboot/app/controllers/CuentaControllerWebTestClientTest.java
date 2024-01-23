package org.a.garcia.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.a.garcia.test.springboot.app.model.Cuenta;
import org.a.garcia.test.springboot.app.model.TransaccionDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
@Tag("integracion_wc")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CuentaControllerWebTestClientTest {
    @Autowired
    private WebTestClient client;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
       mapper=new ObjectMapper();
    }

    @Test
    @Order(1)
    void testTranferir() throws JsonProcessingException {
        //given
        TransaccionDto dto=new TransaccionDto();
        dto.setCuentOrigenId(2L);
        dto.setCuentaDestinoId(2L);
        dto.setBancoId(1L);
        dto.setMonto(new BigDecimal("100"));
        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con éxito!");
        response.put("transaccion", dto);
        //when

        client.post().uri("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                //then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(r->{
                    try {
                        JsonNode json=mapper.readTree(r.getResponseBody()) ;
                        assertEquals("Transferencia realizada con éxito!",json.path("mensaje").asText());
                        assertEquals(2L,json.path("transaccion").path("cuentOrigenId").asLong());
                        assertEquals("100",json.path("transaccion").path("monto").asText());
                        assertEquals(LocalDate.now().toString(),json.path("date").asText());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                })
                .jsonPath("$.mensaje").isNotEmpty()
                .jsonPath("$.mensaje").value(is("Transferencia realizada con éxito!"))
                .jsonPath("$.mensaje").value(valor->assertEquals("Transferencia realizada con éxito!",valor))
                .jsonPath("$.mensaje").isEqualTo("Transferencia realizada con éxito!")
                .jsonPath("$.transaccion.cuentOrigenId").isEqualTo(dto.getCuentOrigenId())
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .json(mapper.writeValueAsString(response));

    }

    @Test
    @Order(2)
    void testDetalle() throws JsonProcessingException {
        Cuenta cuenta=new Cuenta(1L,"Andres",new BigDecimal(1000));

        client.get().uri("/api/cuentas/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.nombre").isEqualTo("Andres")
                .jsonPath("$.saldo").isEqualTo(1000)
                .jsonPath(mapper.writeValueAsString(cuenta));


    }
    @Test
    @Order(3)
    void testDetalle2() {
        client.get().uri("/api/cuentas/2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(r->{
                    Cuenta cuenta= r.getResponseBody();
                    assertNotNull(cuenta);
                    assertEquals(2L,cuenta.getId());
                    assertEquals("Jhon",cuenta.getNombre());
                    assertEquals("2000.00",cuenta.getSaldo().toPlainString());
                });


    }
    @Test
    @Order(4)
    void testListar() {
        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].nombre").isEqualTo("Andres")
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].saldo").isEqualTo(1000)
                .jsonPath("$[1].nombre").isEqualTo("Jhon")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].saldo").isEqualTo(2000)
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(2));



    }
    @Test
    @Order(5)
    void testListar2() {
        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .consumeWith(r->{
                    List<Cuenta> cuentas=r.getResponseBody();
                    assertNotNull(cuentas);
                    assertEquals(2,cuentas.size());
                    assertEquals("Andres",cuentas.get(0).getNombre());
                    assertEquals(1L,cuentas.get(0).getId());
                    assertEquals("1000.0",cuentas.get(0).getSaldo().toPlainString());
                    assertEquals("Jhon",cuentas.get(1).getNombre());
                    assertEquals(2L,cuentas.get(1).getId());
                    assertEquals("2000.0",cuentas.get(1).getSaldo().toPlainString());
                }).hasSize(2);
    }

    @Test
    @Order(6)
    void testGuardar() {
        //give
        Cuenta cuenta=new Cuenta(null,"Pepe",new BigDecimal("3000"));
        client.post().uri("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.nombre").isEqualTo("Pepe")
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.saldo").isEqualTo(3000);




    }
    @Test
    @Order(7)
    void testGuardar2() {
        //give
        Cuenta cuenta=new Cuenta(null,"Pepa",new BigDecimal("50"));
        client.post().uri("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(r->{
                    Cuenta cuenta1=r.getResponseBody();
                    assertNotNull(cuenta1);
                    assertEquals(4L,cuenta1.getId());
                    assertEquals("Pepa",cuenta1.getNombre());
                    assertEquals("50",cuenta1.getSaldo().toPlainString());
                });





    }


    @Test
    @Order(8)
    void testDelete() {
        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class).hasSize(4);
        client.delete().uri("/api/cuentas/3").exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class).hasSize(3);

        client.get().uri("/api/cuentas/3").exchange()
                //.expectStatus().is5xxServerError();
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }
}