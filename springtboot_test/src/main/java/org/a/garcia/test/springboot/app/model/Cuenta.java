package org.a.garcia.test.springboot.app.model;



import org.a.garcia.test.springboot.app.exeption.DineroInsuficienteExeption;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
@Entity
@Table(name ="cuentas" )
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;
    private String nombre;
    private BigDecimal saldo;

    public Cuenta() {
    }

    public Cuenta(Long id, String nombre, BigDecimal saldo) {
        this.id = id;
        this.nombre = nombre;
        this.saldo = saldo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
    public void debito(BigDecimal monto){
    BigDecimal newsaldo=this.saldo.subtract(monto);
    if(newsaldo.compareTo(BigDecimal.ZERO)<0){
        throw new DineroInsuficienteExeption("El el monto supera el saldo actual");

    }
    this.saldo=newsaldo;
    }
    public void credito(BigDecimal monto){
    this.saldo=this.saldo.add(monto);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cuenta cuenta = (Cuenta) o;
        return Objects.equals(id, cuenta.id) && Objects.equals(nombre, cuenta.nombre) && Objects.equals(saldo, cuenta.saldo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, saldo);
    }
}
