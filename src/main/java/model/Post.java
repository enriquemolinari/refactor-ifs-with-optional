package model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Post {

    @Id
    @GeneratedValue
    private Long id;
    private String texto;
    private String titulo;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Author author;

    protected Post() {
    }

    public Post(String titulo, String texto, Author author) {
        this.titulo = titulo;
        this.texto = texto;
        this.author = author;
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
}

