package joseluis.ayala.joseluis01ayala2025.clases;

import java.io.Serializable;
import java.util.Objects;

public class Articulo implements Serializable {
    private String codigo;
    private String nombre;
    private double precio;
    private String foto;

    // Constructor vacío para Firebase
    public Articulo() {}

    public Articulo(String codigo, String nombre, double precio, String foto) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.foto = foto;
    }

    // Getters y setters

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Articulo articulo = (Articulo) o;
        return Objects.equals(codigo, articulo.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public String getFoto() {
        return foto;
    }
}
