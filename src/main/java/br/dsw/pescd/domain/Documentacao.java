package br.dsw.pescd.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Documentacao")
public class Documentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String instituicao;

    @Column(nullable = false)
    private String nomeDisciplina;

    @Column(nullable = false)
    private String cursoDisciplina;

    @Column(nullable = false)
    private Integer cargaHoraria;

    @Column(nullable = false)
    private String nomeArquivo;

    @Column(nullable = false)
    private String nomeArquivoOriginal;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false)
    private Long tamanhoBytes;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] arquivoPdf;

    @OneToOne
    @JoinColumn(name = "inscricao_id", nullable = false)
    private Inscricao inscricao;
}
