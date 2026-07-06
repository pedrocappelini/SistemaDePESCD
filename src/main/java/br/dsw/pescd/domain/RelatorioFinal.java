package br.dsw.pescd.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Relatorio_Final")
public class RelatorioFinal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer frequencia;

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

    @Column(columnDefinition = "TEXT")
    private String parecerSupervisor;

    @Column(length = 1)
    private String sugestaoNota;

    private LocalDateTime dataHoraAvaliacao;
}
