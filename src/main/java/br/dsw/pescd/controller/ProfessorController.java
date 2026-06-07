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
import br.dsw.pescd.domain.RelatorioFinal;
import br.dsw.pescd.repository.RelatorioFinalRepository;

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

    @Autowired
    private RelatorioFinalRepository relatorioFinalRepository;

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

    @GetMapping("/relatorios/{inscricaoId}/avaliar")
    public String exibirFormularioRelatorio(
            @PathVariable("inscricaoId") Long inscricaoId,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Inscricao inscricao = professorService.buscarInscricaoPorId(inscricaoId);

            // RN-2: Dados do plano (leitura)
            PlanoTrabalho plano = planoTrabalhoRepository.findByInscricao(inscricao).orElse(null);

            // RN-2: Dados do relatório (leitura)
            RelatorioFinal relatorio = relatorioFinalRepository.findByInscricao(inscricao)
                    .orElseThrow(() -> new IllegalArgumentException("Relatório não encontrado."));

            model.addAttribute("inscricao", inscricao);
            model.addAttribute("plano", plano);
            model.addAttribute("relatorio", relatorio);

            return "professor/avaliar-relatorio";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/professor/ofertas";
        }
    }

    @PostMapping("/relatorios/{inscricaoId}/aprovar")
    public String aprovarRelatorio(
            @PathVariable("inscricaoId") Long inscricaoId,
            @RequestParam("parecer") String parecer,
            @RequestParam("frequencia") Integer frequencia,
            @RequestParam("sugestaoNota") String sugestaoNota,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            String username = authentication.getName();
            professorService.aprovarRelatorioFinal(inscricaoId, parecer, frequencia, sugestaoNota, username);

            redirectAttributes.addFlashAttribute("sucesso", "Relatório final aprovado com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/professor/relatorios/" + inscricaoId + "/avaliar";
        }

        return "redirect:/professor/ofertas";
    }
}