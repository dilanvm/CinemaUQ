package edu.uniquindio.cinemauq.domain;

public class Cliente extends Usuario {
    private TarjetaVirtual tarjetaVirtual;
    private CuentaPuntos cuentaPuntos;

    public Cliente(Long id, String nombre, String correo, String contrasena) {
        super(id, nombre, correo, contrasena);
        this.tarjetaVirtual = new TarjetaVirtual();
        this.cuentaPuntos = new CuentaPuntos();
    }

    public Cliente(Long id, String nombre, String correo, String contrasena, TarjetaVirtual tarjetaVirtual, CuentaPuntos cuentaPuntos) {
        super(id, nombre, correo, contrasena);
        this.tarjetaVirtual = tarjetaVirtual != null ? tarjetaVirtual : new TarjetaVirtual();
        this.cuentaPuntos = cuentaPuntos != null ? cuentaPuntos : new CuentaPuntos();
    }

    public TarjetaVirtual getTarjetaVirtual() {
        return tarjetaVirtual;
    }

    public void setTarjetaVirtual(TarjetaVirtual tarjetaVirtual) {
        this.tarjetaVirtual = tarjetaVirtual;
    }

    public CuentaPuntos getCuentaPuntos() {
        return cuentaPuntos;
    }

    public void setCuentaPuntos(CuentaPuntos cuentaPuntos) {
        this.cuentaPuntos = cuentaPuntos;
    }
}
