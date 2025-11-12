package model;

import java.math.BigDecimal;

public class Precio {
    private int idPrecio;
    private int idEstacion;
    private String tipoCombustible;
    private BigDecimal precio;

    public Precio() {
    }

    public Precio(int idEstacion, String tipoCombustible, BigDecimal precio) {
        this.idEstacion = idEstacion;
        this.tipoCombustible = tipoCombustible;
        this.precio = precio;
    }

    public int getIdPrecio() {
        return idPrecio;
    }

    public void setIdPrecio(int idPrecio) {
        this.idPrecio = idPrecio;
    }

    public int getIdEstacion() {
        return idEstacion;
    }

    public void setIdEstacion(int idEstacion) {
        this.idEstacion = idEstacion;
    }

    public String getTipoCombustible() {
        return tipoCombustible;
    }

    public void setTipoCombustible(String tipoCombustible) {
        this.tipoCombustible = tipoCombustible;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}