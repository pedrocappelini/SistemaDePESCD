package br.dsw.pescd.controller;

import br.dsw.pescd.dto.ApiDtos.OfertaResponse;
import br.dsw.pescd.dto.ApiMapper;
import br.dsw.pescd.service.InscricaoService;
import br.dsw.pescd.service.OfertaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class VisitanteController {

    private final OfertaService ofertaService;
    private final InscricaoService inscricaoService;

    public VisitanteController(OfertaService ofertaService, InscricaoService inscricaoService) {
        this.ofertaService = ofertaService;
        this.inscricaoService = inscricaoService;
    }

    @GetMapping("/api/ofertas-publicas")
    public List<OfertaResponse> listarOfertasPublicas() {
        return ofertaService.listarTodasOfertas().stream()
                .map(oferta -> ApiMapper.oferta(oferta, inscricaoService.listarInscricoesDaOferta(oferta.getId()).size()))
                .toList();
    }
}
