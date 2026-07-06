package br.dsw.pescd.controller;

import br.dsw.pescd.domain.Oferta;
import br.dsw.pescd.dto.ApiDtos.ApiMessageResponse;
import br.dsw.pescd.dto.ApiDtos.OfertaRequest;
import br.dsw.pescd.dto.ApiDtos.OfertaDetalheResponse;
import br.dsw.pescd.dto.ApiDtos.OfertaResponse;
import br.dsw.pescd.dto.ApiMapper;
import br.dsw.pescd.service.AcompanhamentoService;
import br.dsw.pescd.service.InscricaoService;
import br.dsw.pescd.service.OfertaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/secretario")
public class SecretarioController {

    private final OfertaService ofertaService;
    private final InscricaoService inscricaoService;
    private final AcompanhamentoService acompanhamentoService;

    public SecretarioController(
            OfertaService ofertaService,
            InscricaoService inscricaoService,
            AcompanhamentoService acompanhamentoService
    ) {
        this.ofertaService = ofertaService;
        this.inscricaoService = inscricaoService;
        this.acompanhamentoService = acompanhamentoService;
    }

    @GetMapping("/ofertas")
    public List<OfertaResponse> listarOfertas() {
        return ofertaService.listarTodasOfertas().stream()
                .map(oferta -> ApiMapper.oferta(oferta, inscricaoService.listarInscricoesDaOferta(oferta.getId()).size()))
                .toList();
    }

    @GetMapping("/ofertas/{ofertaId}")
    public OfertaDetalheResponse acompanharOferta(@PathVariable Long ofertaId) {
        return acompanhamentoService.detalharOferta(ofertaId);
    }

    @PostMapping("/ofertas")
    public ResponseEntity<OfertaResponse> criarOferta(
            @RequestBody OfertaRequest request,
            Authentication authentication
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Corpo da requisicao e obrigatorio.");
        }

        Oferta oferta = new Oferta();
        oferta.setNome(request.nome());
        oferta.setSemestre(request.semestre());
        oferta.setDataInicio(request.dataInicio());
        oferta.setDataFim(request.dataFim());

        ofertaService.criarOferta(oferta, request.professorId(), authentication.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiMapper.oferta(oferta, 0));
    }

    @PostMapping("/ofertas/{ofertaId}/encerrar")
    public ApiMessageResponse encerrarOferta(
            @PathVariable Long ofertaId,
            Authentication authentication
    ) {
        ofertaService.encerrarOferta(ofertaId, authentication.getName());
        return new ApiMessageResponse("Oferta encerrada com sucesso.");
    }
}
