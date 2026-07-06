package br.dsw.pescd.controller;

import br.dsw.pescd.domain.Oferta;
import br.dsw.pescd.dto.ApiDtos.OfertaRequest;
import br.dsw.pescd.dto.ApiDtos.OfertaResponse;
import br.dsw.pescd.dto.ApiMapper;
import br.dsw.pescd.service.OfertaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/secretario")
public class SecretarioController {

    private final OfertaService ofertaService;

    public SecretarioController(OfertaService ofertaService) {
        this.ofertaService = ofertaService;
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
}
