package org.a.garcia.test.springboot.app.repository;

import org.a.garcia.test.springboot.app.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository <Cuenta,Long>{
    @Query("select c from Cuenta c where c.nombre=?1")
    Optional<Cuenta> buscarPorNombre(String nombre);
//List<Cuenta> findAll();
//Cuenta findById(Long id);
//void update(Cuenta cuenta);

}
