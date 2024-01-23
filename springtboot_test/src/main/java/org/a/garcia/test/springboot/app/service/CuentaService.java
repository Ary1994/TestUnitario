package org.a.garcia.test.springboot.app.service;

import org.a.garcia.test.springboot.app.model.Cuenta;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaService {
    List<Cuenta> findAll();
    Cuenta findById(Long id);
    Cuenta save(Cuenta cuenta);
    void deleteById(Long id);
    int revisarTotalTranferencias(Long bancoId);
    BigDecimal revisarSaldo(Long cuentaId);
    void tranferir(Long cuentaIdOrigen,Long cuentaIdDestino,BigDecimal monto,Long bancoId );


}
