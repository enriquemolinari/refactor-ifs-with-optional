package model;

import jakarta.persistence.*;
import service.LatestsPost;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Post {

    @Id
    @GeneratedValue
    private Long id;
    private String texto;
    private String titulo;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Author author;
    private LocalDateTime fechaPublicacion;

    protected Post() {
    }

    public Post(String titulo, String texto, Author author, LocalDateTime fechaPublicacion) {
        this.titulo = titulo;
        this.texto = texto;
        this.author = author;
        this.fechaPublicacion = fechaPublicacion;
    }

    private Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    private String getTexto() {
        return texto;
    }

    private void setTexto(String texto) {
        this.texto = texto;
    }

    private String getTitulo() {
        return titulo;
    }

    private void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    private Author getAuthor() {
        return author;
    }

    private void setAuthor(Author author) {
        this.author = author;
    }

    private LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    private void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String title() {
        return titulo;
    }

    public LatestsPost toLatest() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return new LatestsPost(this.titulo, this.fechaPublicacion.format(formatter));
    }
}

