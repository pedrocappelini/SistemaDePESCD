package br.dsw.pescd.controller;

import br.dsw.pescd.dto.HistoricoItemDTO;
import br.dsw.pescd.service.HistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/aluno")
public class HistoricoController {

    @Autowired
    private HistoricoService historicoService;

    @GetMapping("/historico")
    public String historico(Authentication authentication,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();

            List<HistoricoItemDTO> historico = historicoService.buscarHistoricoDoAluno(username);
            int concluidos = historicoService.contarSemestresConcluidos(historico);
            int minimos = historicoService.getSemestresMinimos();

            model.addAttribute("historico", historico);
            model.addAttribute("concluidos", concluidos);
            model.addAttribute("minimos", minimos);
            model.addAttribute("cumpriuMinimo", concluidos >= minimos);

            return "aluno/historico";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/aluno/ofertas";
        }
    }
}