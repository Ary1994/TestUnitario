package org.a.garcia.test.springboot.app;

import org.a.garcia.test.springboot.app.model.Cuenta;
import org.a.garcia.test.springboot.app.repository.CuentaRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@Tag("integracion_jpa")
@DataJpaTest
public class integracionJpaTest {
    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void testFindById() {
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
        assertTrue(cuenta.isPresent());
        assertEquals("Andres",cuenta.orElseThrow().getNombre());
    }
    @Test
    void testFindByNombre() {
        Optional<Cuenta> cuenta = cuentaRepository.buscarPorNombre("Andres");
        assertTrue(cuenta.isPresent());
        assertEquals("Andres",cuenta.orElseThrow().getNombre());
        assertEquals("1000.00",cuenta.orElseThrow().getSaldo().toPlainString());
    }
    @Test
    void testFindByNombreThrowExeption() {
        Optional<Cuenta> cuenta = cuentaRepository.buscarPorNombre("Rod");
        assertThrows(NoSuchElementException.class,cuenta::orElseThrow);
        assertFalse(cuenta.isPresent());

    }

    @Test
    void testFindAll() {
        List<Cuenta> cuentas = cuentaRepository.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2,cuentas.size());
    }

    @Test
    void testSave() {
        //given
        Cuenta cuentapepe=new Cuenta(null,"Pepe",new BigDecimal(3000));

        //when cuando
        Cuenta cuenta = cuentaRepository.save(cuentapepe);
        //Cuenta cuenta = cuentaRepository.buscarPorNombre("Pepe").orElseThrow();
       // Cuenta cuenta = cuentaRepository.findById(save.getId()).orElseThrow();
        //then
        assertEquals("Pepe",cuenta.getNombre());
        assertEquals("3000",cuenta.getSaldo().toPlainString());


    }
    @Test
    void testUpdate() {
        //given
        Cuenta cuentapepe=new Cuenta(null,"Pepe",new BigDecimal(3000));

        //when cuando
        Cuenta cuenta = cuentaRepository.save(cuentapepe);
        //Cuenta cuenta = cuentaRepository.buscarPorNombre("Pepe").orElseThrow();
        // Cuenta cuenta = cuentaRepository.findById(save.getId()).orElseThrow();
        //then
        assertEquals("Pepe",cuenta.getNombre());
        assertEquals("3000",cuenta.getSaldo().toPlainString());

        cuenta.setSaldo(new BigDecimal(3800));
        Cuenta cuentaUpdate = cuentaRepository.save(cuenta);

        assertEquals("Pepe",cuentaUpdate.getNombre());
        assertEquals("3800",cuentaUpdate.getSaldo().toPlainString());

    }

    @Test
    void testDelete() {
        Cuenta cuenta= cuentaRepository.findById(2L).orElseThrow();
        assertEquals("Jhon",cuenta.getNombre());

        cuentaRepository.delete(cuenta);
        assertThrows(NoSuchElementException.class,()->{
            cuentaRepository.findById(2L).orElseThrow();
        });


    }
}
