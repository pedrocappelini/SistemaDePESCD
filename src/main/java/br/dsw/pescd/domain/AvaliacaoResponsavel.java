package br.dsw.pescd.domain;

import br.dsw.pescd.enums.TipoConclusao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Avaliacao_Responsavel")
public class AvaliacaoResponsavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "inscricao_id", nullable = false, unique = true)
    private Inscricao inscricao;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String parecer;

    @Column(nullable = false)
    private Integer frequencia;     // de 0 a 100

    @Column(nullable = false, length = 1)
    private String nota;            // "A", "B", "C", "D" ou "E"

    @ManyToOne
    @JoinColumn(name = "professor_responsavel_id", nullable = false)
    private Professor professorResponsavel;

    @Column(nullable = false)
    private LocalDateTime dataHoraAvaliacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConclusao tipo;     // RELATORIO ou DOCUMENTACAO
}