package br.dsw.pescd.controller;

import br.dsw.pescd.domain.AvaliacaoResponsavel;
import br.dsw.pescd.domain.Inscricao;
import br.dsw.pescd.domain.Oferta;
import br.dsw.pescd.dto.ApiDtos.AprovacaoPlanoRequest;
import br.dsw.pescd.dto.ApiDtos.AprovacaoRelatorioSupervisorRequest;
import br.dsw.pescd.dto.ApiDtos.InscricaoDetalheResponse;
import br.dsw.pescd.dto.ApiDtos.OfertaDetalheResponse;
import br.dsw.pescd.dto.ApiMapper;
import br.dsw.pescd.service.AcompanhamentoService;
import br.dsw.pescd.service.ProfessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/api/professor")
@Tag(name = "professor supervisor")
public class ProfessorController {

    private final ProfessorService professorService;
    private final AcompanhamentoService acompanhamentoService;

    public ProfessorController(
            ProfessorService professorService,
            AcompanhamentoService acompanhamentoService
    ) {
        this.professorService = professorService;
        this.acompanhamentoService = acompanhamentoService;
    }

    @GetMapping("/ofertas")
    @Operation(summary = "listar supervisoes")
    public List<OfertaDetalheResponse> listarMinhasSupervisoes(Authentication authentication) {
        LinkedHashMap<Long, List<Inscricao>> inscricoesPorOferta = new LinkedHashMap<>();
        professorService.buscarInscricoesDoSupervisor(authentication.getName()).stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        inscricao -> inscricao.getOferta().getId(),
                        LinkedHashMap::new,
                        java.util.stream.Collectors.toList()
                ))
                .forEach(inscricoesPorOferta::put);

        return inscricoesPorOferta.values().stream()
                .map(inscricoes -> {
                    Oferta oferta = inscricoes.get(0).getOferta();
                    List<InscricaoDetalheResponse> detalhes = inscricoes.stream()
                            .map(acompanhamentoService::detalharInscricao)
                            .toList();
                    return ApiMapper.ofertaDetalhe(
                            oferta,
                            inscricoes.size(),
                            detalhes,
                            ApiMapper.estatisticas(List.<AvaliacaoResponsavel>of())
                    );
                })
                .toList();
    }

    @GetMapping("/planos/{inscricaoId}")
    @Operation(summary = "consultar plano de trabalho")
    public InscricaoDetalheResponse detalharPlano(
            @PathVariable Long inscricaoId,
            Authentication authentication
    ) {
        Inscricao inscricao = professorService.buscarInscricaoDoSupervisor(inscricaoId, authentication.getName());
        return acompanhamentoService.detalharInscricao(inscricao);
    }

    @PostMapping("/planos/{inscricaoId}/aprovar")
    @Operation(summary = "aprovar plano de trabalho")
    public InscricaoDetalheResponse aprovarPlano(
            @PathVariable Long inscricaoId,
            @RequestBody AprovacaoPlanoRequest request,
            Authentication authentication
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Corpo da requisicao e obrigatorio.");
        }

        professorService.aprovarPlanoDeTrabalho(inscricaoId, request.parecer(), authentication.getName());
        Inscricao inscricao = professorService.buscarInscricaoDoSupervisor(inscricaoId, authentication.getName());
        return acompanhamentoService.detalharInscricao(inscricao);
    }

    @GetMapping("/relatorios/{inscricaoId}")
    @Operation(summary = "consultar relatorio final")
    public InscricaoDetalheResponse detalharRelatorio(
            @PathVariable Long inscricaoId,
            Authentication authentication
    ) {
        Inscricao inscricao = professorService.buscarInscricaoDoSupervisor(inscricaoId, authentication.getName());
        return acompanhamentoService.detalharInscricao(inscricao);
    }

    @PostMapping("/relatorios/{inscricaoId}/aprovar")
    @Operation(summary = "aprovar relatorio final")
    public InscricaoDetalheResponse aprovarRelatorio(
            @PathVariable Long inscricaoId,
            @RequestBody AprovacaoRelatorioSupervisorRequest request,
            Authentication authentication
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Corpo da requisicao e obrigatorio.");
        }

        professorService.aprovarRelatorioFinal(
                inscricaoId,
                request.parecer(),
                request.frequencia(),
                request.sugestaoNota(),
                authentication.getName()
        );
        Inscricao inscricao = professorService.buscarInscricaoDoSupervisor(inscricaoId, authentication.getName());
        return acompanhamentoService.detalharInscricao(inscricao);
    }
}
