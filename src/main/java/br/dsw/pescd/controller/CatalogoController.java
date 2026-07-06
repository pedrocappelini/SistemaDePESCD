package br.dsw.pescd.controller;

import br.dsw.pescd.dto.ApiDtos.ProfessorResponse;
import br.dsw.pescd.dto.ApiMapper;
import br.dsw.pescd.service.ProfessorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CatalogoController {

    private final ProfessorService professorService;

    public CatalogoController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @GetMapping("/api/professores")
    public List<ProfessorResponse> listarProfessores() {
        return professorService.listarProfessores().stream()
                .map(ApiMapper::professor)
                .toList();
    }
}
