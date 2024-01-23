package org.a.garcia.test.springboot.app.model;

import java.math.BigDecimal;

public class TransaccionDto {
    private Long cuentOrigenId;
    private Long cuentaDestinoId;
    private BigDecimal monto;
    private Long bancoId;

    public Long getBancoId() {
        return bancoId;
    }

    public void setBancoId(Long bancoId) {
        this.bancoId = bancoId;
    }

    public Long getCuentOrigenId() {
        return cuentOrigenId;
    }

    public void setCuentOrigenId(Long cuentOrigenId) {
        this.cuentOrigenId = cuentOrigenId;
    }

    public Long getCuentaDestinoId() {
        return cuentaDestinoId;
    }

    public void setCuentaDestinoId(Long cuentaDestinoId) {
        this.cuentaDestinoId = cuentaDestinoId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}
