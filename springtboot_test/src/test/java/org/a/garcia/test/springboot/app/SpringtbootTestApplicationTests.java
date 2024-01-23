package org.a.garcia.test.springboot.app;

import org.a.garcia.test.springboot.app.exeption.DineroInsuficienteExeption;
import org.a.garcia.test.springboot.app.model.Banco;
import org.a.garcia.test.springboot.app.model.Cuenta;
import org.a.garcia.test.springboot.app.repository.BancoRepository;
import org.a.garcia.test.springboot.app.repository.CuentaRepository;
import org.a.garcia.test.springboot.app.service.CuentaService;
import org.a.garcia.test.springboot.app.service.CuentaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.a.garcia.test.springboot.app.Datos.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SpringtbootTestApplicationTests {
	@MockBean
	CuentaRepository cuentaRepository;
	@MockBean
	BancoRepository bancoRepository;
	@Autowired
	CuentaService service;

	@BeforeEach
	void setUp() {
		//cuentaRepository= mock(CuentaRepository.class);
		//bancoRepository=mock(BancoRepository.class);
		//service =new CuentaServiceImpl(cuentaRepository,bancoRepository);
		//Datos.CUENTA_001.setSaldo(new BigDecimal(1000));
		//Datos.CUENTA_002.setSaldo(new BigDecimal(2000));
		//Datos.BANCO.setTotalTranferencia(0);

	}

	@Test
	void contextLoads() {
		when (cuentaRepository.findById(1L)).thenReturn(crearCuenta_001());
		when (cuentaRepository.findById(2L)).thenReturn(crearCuenta_002());
		when(bancoRepository.findById(1L)).thenReturn(crearBanco());
		BigDecimal saldoOrigen= service.revisarSaldo(1L);
		BigDecimal saldoDestino= service.revisarSaldo(2L);

		assertEquals("1000",saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());
		service.tranferir(1L,2L,new BigDecimal(500),1L);
		 saldoOrigen= service.revisarSaldo(1L);
		 saldoDestino= service.revisarSaldo(2L);
		assertEquals("500",saldoOrigen.toPlainString());
		assertEquals("2500", saldoDestino.toPlainString());


		int totalTranf=service.revisarTotalTranferencias(1L);
		assertEquals(1,totalTranf);
		verify(cuentaRepository,times(3)).findById(1L);
		verify(cuentaRepository,times(3)).findById(2L);
		verify(cuentaRepository,times(2)).save(any(Cuenta.class));
		verify(bancoRepository,times(2)).findById(1L);
		verify(bancoRepository).save(any(Banco.class));
		verify(cuentaRepository,times(6)).findById(anyLong());
		verify(cuentaRepository,never()).findAll();




	}
	@Test
	void contextLoads2() {
		when (cuentaRepository.findById(1L)).thenReturn(crearCuenta_001());
		when (cuentaRepository.findById(2L)).thenReturn(crearCuenta_002());
		when(bancoRepository.findById(1L)).thenReturn(crearBanco());
		BigDecimal saldoOrigen= service.revisarSaldo(1L);
		BigDecimal saldoDestino= service.revisarSaldo(2L);

		assertEquals("1000",saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());
		assertThrows(DineroInsuficienteExeption.class,()->{
			service.tranferir(1L,2L,new BigDecimal(1200),1L);
		});

		saldoOrigen= service.revisarSaldo(1L);
		saldoDestino= service.revisarSaldo(2L);
		assertEquals("1000",saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());


		int totalTranf=service.revisarTotalTranferencias(1L);
		assertEquals(0,totalTranf);
		verify(cuentaRepository,times(3)).findById(1L);
		verify(cuentaRepository,times(2)).findById(2L);
		verify(cuentaRepository,never()).save(any(Cuenta.class));
		verify(bancoRepository,times(1)).findById(1L);
		verify(bancoRepository,never()).save(any(Banco.class));

		verify(cuentaRepository,times(5)).findById(anyLong());
		verify(cuentaRepository,never()).findAll();


	}

	@Test
	void contextLoads3() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta_001());
		Cuenta cuenta1=service.findById(1L);
		Cuenta cuenta2=service.findById(1L);

		assertSame(cuenta1,cuenta2);
		assertTrue(cuenta1==cuenta2);
		assertEquals("Andres",cuenta1.getNombre());
		assertEquals("Andres",cuenta2.getNombre());
		verify(cuentaRepository,times(2)).findById(1L);
	}

	@Test
	void testFindAll() {
		List<Cuenta> cuentas= Arrays.asList(crearCuenta_001().orElseThrow(),crearCuenta_002().orElseThrow());

		when(cuentaRepository.findAll()).thenReturn(cuentas);
		List<Cuenta>respuesta=service.findAll();

		assertFalse(cuentas.isEmpty());
		assertEquals(2,respuesta.size());
		assertTrue(respuesta.contains(crearCuenta_001().orElseThrow()));
		verify(cuentaRepository).findAll();



	}

	@Test
	void testSave() {

		Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));

		when(cuentaRepository.save(any())).then(invocation->{
			Cuenta c=invocation.getArgument(0);
			c.setId(3L);
			return c;
		});



		 Cuenta respuesta=service.save(cuenta);

		 assertEquals("Pepe",respuesta.getNombre());
		 assertEquals(3,respuesta.getId());
		 assertEquals("3000",respuesta.getSaldo().toPlainString());

		 verify(cuentaRepository).save(any());


	}
}
