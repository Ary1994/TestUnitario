package org.a.garcia.test.springboot.app.service;


import org.a.garcia.test.springboot.app.model.Banco;
import org.a.garcia.test.springboot.app.model.Cuenta;
import org.a.garcia.test.springboot.app.repository.BancoRepository;
import org.a.garcia.test.springboot.app.repository.CuentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CuentaServiceImpl implements CuentaService{
    private CuentaRepository cuentaRepository;
    private BancoRepository bancoRepository;

    public CuentaServiceImpl(CuentaRepository cuentaRepository, BancoRepository bancoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.bancoRepository = bancoRepository;
    }

    @Override
    public List<Cuenta> findAll() {
        return cuentaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Cuenta findById(Long id) {
        return cuentaRepository.findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public Cuenta save(Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        cuentaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public int revisarTotalTranferencias(Long bancoId) {
        Banco banco=bancoRepository.findById(bancoId).orElseThrow();

        return banco.getTotalTranferencia();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal revisarSaldo(Long cuentaId) {
        Cuenta cuenta= cuentaRepository.findById(cuentaId).orElseThrow();
        return cuenta.getSaldo();
    }

    @Override
    @Transactional
    public void tranferir(Long cuentaIdOrigen, Long cuentaIdDestino, BigDecimal monto,
                          Long bancoId) {
        Cuenta cuentaO=cuentaRepository.findById(cuentaIdOrigen).orElseThrow();
        cuentaO.debito(monto);
        cuentaRepository.save(cuentaO);
        Cuenta cuentaD=cuentaRepository.findById(cuentaIdDestino).orElseThrow();
        cuentaD.credito(monto);
        cuentaRepository.save(cuentaD);

        Banco banco =bancoRepository.findById(bancoId).orElseThrow();
        int totalTranferencias=banco.getTotalTranferencia();
        banco.setTotalTranferencia(++totalTranferencias);
        bancoRepository.save(banco);

    }
}
