package br.dsw.pescd.domain;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Aluno extends Usuario {

    @Column(unique = true, length = 20)
    private String RA;
}