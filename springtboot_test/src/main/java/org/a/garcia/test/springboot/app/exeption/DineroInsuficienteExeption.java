package org.a.garcia.test.springboot.app.exeption;

public class DineroInsuficienteExeption extends RuntimeException{
    public DineroInsuficienteExeption(String message) {
        super(message);
    }
}
