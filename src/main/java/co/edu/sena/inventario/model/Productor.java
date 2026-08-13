package co.edu.sena.inventario.model;

public class Productor {

    private Long id;
    private String nombre;
    private String municipio;

    public Productor(Long id, String nombre, String municipio){
        this.id = id;
        this.nombre = nombre;
        this.municipio = municipio;
    }

    public Long getId() {
        return id;
    }

    public String getNombre(){
        return nombre;
    }

    public String getMunicipio() {
        return municipio;
    }
}