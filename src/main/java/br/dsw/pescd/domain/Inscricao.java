package br.dsw.pescd.domain;

import br.dsw.pescd.enums.StatusAlunoOferta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Inscricao")
public class Inscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "oferta_id", nullable = false)
    private Oferta oferta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAlunoOferta status = StatusAlunoOferta.NAO_ENVIADO;

    @ManyToOne
    @JoinColumn(name = "professor_supervisor_id")
    private Professor professorSupervisor;
}