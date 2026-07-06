package br.dsw.pescd.controller;

import br.dsw.pescd.domain.Inscricao;
import br.dsw.pescd.domain.PlanoTrabalho;
import br.dsw.pescd.dto.ApiDtos.AlunoOfertaResponse;
import br.dsw.pescd.dto.ApiDtos.PlanoResponse;
import br.dsw.pescd.dto.ApiMapper;
import br.dsw.pescd.repository.PlanoTrabalhoRepository;
import br.dsw.pescd.service.InscricaoService;
import br.dsw.pescd.service.SubmissaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/aluno")
public class AlunoController {

    private final InscricaoService inscricaoService;
    private final SubmissaoService submissaoService;
    private final PlanoTrabalhoRepository planoTrabalhoRepository;

    public AlunoController(
            InscricaoService inscricaoService,
            SubmissaoService submissaoService,
            PlanoTrabalhoRepository planoTrabalhoRepository
    ) {
        this.inscricaoService = inscricaoService;
        this.submissaoService = submissaoService;
        this.planoTrabalhoRepository = planoTrabalhoRepository;
    }

    @GetMapping("/ofertas")
    public List<AlunoOfertaResponse> listarOfertas(Authentication authentication) {
        return inscricaoService.buscarInscricoesDoAluno(authentication.getName()).stream()
                .map(inscricao -> ApiMapper.alunoOferta(
                        inscricao,
                        inscricaoService.listarInscricoesDaOferta(inscricao.getOferta().getId()).size()
                ))
                .toList();
    }

    @PostMapping(value = "/ofertas/{ofertaId}/plano", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlanoResponse> enviarPlano(
            @PathVariable Long ofertaId,
            @RequestParam String codigoDisciplina,
            @RequestParam String nomeDisciplina,
            @RequestParam String cursoDisciplina,
            @RequestParam Long professorSupId,
            @RequestParam MultipartFile arquivo,
            Authentication authentication
    ) throws Exception {
        submissaoService.enviarPlano(
                authentication.getName(),
                ofertaId,
                codigoDisciplina,
                nomeDisciplina,
                cursoDisciplina,
                professorSupId,
                arquivo
        );

        Inscricao inscricao = inscricaoService.buscarInscricao(authentication.getName(), ofertaId);
        PlanoTrabalho plano = planoTrabalhoRepository.findByInscricao(inscricao)
                .orElseThrow(() -> new IllegalArgumentException("Plano nao encontrado."));

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiMapper.plano(plano));
    }
}
