package br.dsw.pescd.domain;

import br.dsw.pescd.enums.StatusOferta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Oferta")
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Se não for preenchido, o sistema vai montar com base no semestre depois
    @Column(length = 150)
    private String nome;

    @Column(nullable = false, length = 20)
    private String semestre;

    @Column(nullable = false)
    private LocalDate dataInicio;

    @Column(nullable = false)
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOferta status;

    @ManyToOne
    @JoinColumn(name = "professor_responsavel_id", nullable = false)
    private Professor professorResponsavel;

    @Column(nullable = false)
    private LocalDateTime dataHoraCriacao;

    @ManyToOne
    @JoinColumn(name = "criado_por_id", nullable = false)
    private Secretario criadoPor;
}