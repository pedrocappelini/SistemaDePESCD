package br.dsw.pescd.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "PlanoTrabalho")
public class PlanoTrabalho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String codigoDisciplina;

    @Column(nullable = false, length = 150)
    private String nomeDisciplina;

    @Column(nullable = false, length = 150)
    private String cursoDisciplina;

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
    @JoinColumn(name = "inscricao_id", nullable = false, unique = true)
    private Inscricao inscricao;

    @Column(columnDefinition = "TEXT")
    private String parecerSupervisor;

    private LocalDateTime dataHoraAvaliacao;
}
