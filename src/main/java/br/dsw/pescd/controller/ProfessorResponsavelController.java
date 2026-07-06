package br.dsw.pescd.controller;

import br.dsw.pescd.domain.AvaliacaoResponsavel;
import br.dsw.pescd.domain.Documentacao;
import br.dsw.pescd.domain.Inscricao;
import br.dsw.pescd.domain.PlanoTrabalho;
import br.dsw.pescd.domain.RelatorioFinal;
import br.dsw.pescd.dto.ApiDtos.AvaliacaoRequest;
import br.dsw.pescd.dto.ApiDtos.InscricaoDetalheResponse;
import br.dsw.pescd.dto.ApiMapper;
import br.dsw.pescd.repository.AvaliacaoResponsavelRepository;
import br.dsw.pescd.repository.DocumentacaoRepository;
import br.dsw.pescd.repository.PlanoTrabalhoRepository;
import br.dsw.pescd.repository.RelatorioFinalRepository;
import br.dsw.pescd.service.ProfessorResponsavelService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/professor/responsavel")
public class ProfessorResponsavelController {

    private final ProfessorResponsavelService prService;
    private final PlanoTrabalhoRepository planoTrabalhoRepository;
    private final RelatorioFinalRepository relatorioFinalRepository;
    private final DocumentacaoRepository documentacaoRepository;
    private final AvaliacaoResponsavelRepository avaliacaoResponsavelRepository;

    public ProfessorResponsavelController(
            ProfessorResponsavelService prService,
            PlanoTrabalhoRepository planoTrabalhoRepository,
            RelatorioFinalRepository relatorioFinalRepository,
            DocumentacaoRepository documentacaoRepository,
            AvaliacaoResponsavelRepository avaliacaoResponsavelRepository
    ) {
        this.prService = prService;
        this.planoTrabalhoRepository = planoTrabalhoRepository;
        this.relatorioFinalRepository = relatorioFinalRepository;
        this.documentacaoRepository = documentacaoRepository;
        this.avaliacaoResponsavelRepository = avaliacaoResponsavelRepository;
    }

    @GetMapping("/relatorios/{inscricaoId}")
    public InscricaoDetalheResponse detalharRelatorio(
            @PathVariable Long inscricaoId,
            Authentication authentication
    ) {
        Inscricao inscricao = prService.buscarInscricaoDoResponsavel(inscricaoId, authentication.getName());
        PlanoTrabalho plano = planoTrabalhoRepository.findByInscricao(inscricao)
                .orElseThrow(() -> new IllegalArgumentException("Plano nao encontrado."));
        RelatorioFinal relatorio = relatorioFinalRepository.findByInscricao(inscricao)
                .orElseThrow(() -> new IllegalArgumentException("Relatorio nao encontrado."));
        AvaliacaoResponsavel avaliacao = avaliacaoResponsavelRepository.findByInscricao(inscricao).orElse(null);

        return ApiMapper.inscricaoDetalhe(inscricao, plano, null, relatorio, avaliacao);
    }

    @PostMapping("/relatorios/{inscricaoId}/concluir")
    public InscricaoDetalheResponse concluirRelatorio(
            @PathVariable Long inscricaoId,
            @RequestBody AvaliacaoRequest request,
            Authentication authentication
    ) {
        validarAvaliacao(request);
        prService.concluirRelatorio(
                inscricaoId,
                request.parecer(),
                request.frequencia(),
                request.nota(),
                authentication.getName()
        );

        return detalharRelatorio(inscricaoId, authentication);
    }

    @GetMapping("/documentacoes/{inscricaoId}")
    public InscricaoDetalheResponse detalharDocumentacao(
            @PathVariable Long inscricaoId,
            Authentication authentication
    ) {
        Inscricao inscricao = prService.buscarInscricaoDoResponsavel(inscricaoId, authentication.getName());
        Documentacao documentacao = documentacaoRepository.findByInscricao(inscricao)
                .orElseThrow(() -> new IllegalArgumentException("Documentacao nao encontrada."));
        AvaliacaoResponsavel avaliacao = avaliacaoResponsavelRepository.findByInscricao(inscricao).orElse(null);

        return ApiMapper.inscricaoDetalhe(inscricao, null, documentacao, null, avaliacao);
    }

    @PostMapping("/documentacoes/{inscricaoId}/analisar")
    public InscricaoDetalheResponse analisarDocumentacao(
            @PathVariable Long inscricaoId,
            @RequestBody AvaliacaoRequest request,
            Authentication authentication
    ) {
        validarAvaliacao(request);
        prService.analisarDocumentacao(
                inscricaoId,
                request.parecer(),
                request.frequencia(),
                request.nota(),
                authentication.getName()
        );

        return detalharDocumentacao(inscricaoId, authentication);
    }

    private void validarAvaliacao(AvaliacaoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Corpo da requisicao e obrigatorio.");
        }
    }
}
