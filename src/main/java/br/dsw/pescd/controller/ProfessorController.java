package br.dsw.pescd.controller;

import br.dsw.pescd.domain.Inscricao;
import br.dsw.pescd.domain.Oferta;
import br.dsw.pescd.domain.PlanoTrabalho;
import br.dsw.pescd.repository.PlanoTrabalhoRepository;
import br.dsw.pescd.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private PlanoTrabalhoRepository planoTrabalhoRepository;

    @GetMapping("/ofertas")
    public String listarMinhasSupervisoes(Authentication authentication, Model model) {
        String username = authentication.getName();

        List<Inscricao> minhasInscricoes = professorService.buscarInscricoesDoSupervisor(username);

        Map<Oferta, List<Inscricao>> inscricoesPorOferta = minhasInscricoes.stream()
                .collect(Collectors.groupingBy(Inscricao::getOferta));

        model.addAttribute("inscricoesPorOferta", inscricoesPorOferta);

        return "professor/ofertas";
    }

    @GetMapping("/planos/{inscricaoId}/avaliar")
    public String exibirFormularioAvaliacao(
            @PathVariable("inscricaoId") Long inscricaoId,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Inscricao inscricao = professorService.buscarInscricaoPorId(inscricaoId);
            PlanoTrabalho plano = planoTrabalhoRepository.findByInscricao(inscricao)
                    .orElseThrow(() -> new IllegalArgumentException("Plano não encontrado."));

            model.addAttribute("inscricao", inscricao);
            model.addAttribute("plano", plano);

            return "professor/avaliar-plano";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/professor/ofertas";
        }
    }

    @PostMapping("/planos/{inscricaoId}/aprovar")
    public String aprovarPlano(
            @PathVariable("inscricaoId") Long inscricaoId,
            @RequestParam("parecer") String parecer,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            String username = authentication.getName();
            professorService.aprovarPlanoDeTrabalho(inscricaoId, parecer, username);

            redirectAttributes.addFlashAttribute("sucesso", "Plano de trabalho aprovado com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/professor/ofertas";
    }
}