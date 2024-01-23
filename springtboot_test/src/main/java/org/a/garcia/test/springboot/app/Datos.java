package org.a.garcia.test.springboot.app;

import org.a.garcia.test.springboot.app.model.Banco;
import org.a.garcia.test.springboot.app.model.Cuenta;

import java.math.BigDecimal;
import java.util.Optional;

public class Datos {
    //public static final Cuenta CUENTA_001=new Cuenta(1L,"Andres",new BigDecimal("1000"));
   // public static final Cuenta CUENTA_002=new Cuenta(2L,"Jhon",new BigDecimal("2000"));
    //public static final Banco BANCO=new Banco(1L,"El banco Financiero",0);


    public static Optional<Cuenta> crearCuenta_001() {

        return  Optional.of( new Cuenta(1L,"Andres",new BigDecimal("1000"))) ;
    }
    public static Optional<Cuenta> crearCuenta_002() {

        return Optional.of(new Cuenta(2L,"Jhon",new BigDecimal("2000")));
    }

    public static Optional<Banco> crearBanco(){

        return Optional.of(new Banco(1L,"El banco Financiero",0));
    }
}
