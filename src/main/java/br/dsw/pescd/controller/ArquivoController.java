package br.dsw.pescd.controller;

import br.dsw.pescd.domain.Documentacao;
import br.dsw.pescd.domain.PlanoTrabalho;
import br.dsw.pescd.domain.RelatorioFinal;
import br.dsw.pescd.repository.DocumentacaoRepository;
import br.dsw.pescd.repository.PlanoTrabalhoRepository;
import br.dsw.pescd.repository.RelatorioFinalRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/arquivos")
@Tag(name = "arquivos")
public class ArquivoController {

    private final PlanoTrabalhoRepository planoTrabalhoRepository;
    private final DocumentacaoRepository documentacaoRepository;
    private final RelatorioFinalRepository relatorioFinalRepository;

    public ArquivoController(
            PlanoTrabalhoRepository planoTrabalhoRepository,
            DocumentacaoRepository documentacaoRepository,
            RelatorioFinalRepository relatorioFinalRepository
    ) {
        this.planoTrabalhoRepository = planoTrabalhoRepository;
        this.documentacaoRepository = documentacaoRepository;
        this.relatorioFinalRepository = relatorioFinalRepository;
    }

    @GetMapping("/planos/{id}/pdf")
    @Operation(summary = "baixar pdf do plano")
    public ResponseEntity<byte[]> baixarPlano(@PathVariable Long id) {
        PlanoTrabalho plano = planoTrabalhoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plano nao encontrado."));
        return pdf(plano.getArquivoPdf(), plano.getNomeArquivoOriginal());
    }

    @GetMapping("/documentacoes/{id}/pdf")
    @Operation(summary = "baixar pdf da documentacao")
    public ResponseEntity<byte[]> baixarDocumentacao(@PathVariable Long id) {
        Documentacao documentacao = documentacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documentacao nao encontrada."));
        return pdf(documentacao.getArquivoPdf(), documentacao.getNomeArquivoOriginal());
    }

    @GetMapping("/relatorios/{id}/pdf")
    @Operation(summary = "baixar pdf do relatorio")
    public ResponseEntity<byte[]> baixarRelatorio(@PathVariable Long id) {
        RelatorioFinal relatorio = relatorioFinalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relatorio nao encontrado."));
        return pdf(relatorio.getArquivoPdf(), relatorio.getNomeArquivoOriginal());
    }

    private ResponseEntity<byte[]> pdf(byte[] conteudo, String nomeArquivo) {
        if (conteudo == null || conteudo.length == 0) {
            throw new IllegalArgumentException("Arquivo PDF nao encontrado.");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(nomeArquivo == null || nomeArquivo.isBlank() ? "arquivo.pdf" : nomeArquivo)
                        .build()
                        .toString())
                .body(conteudo);
    }
}
