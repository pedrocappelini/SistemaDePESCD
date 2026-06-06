package br.dsw.pescd.controller;

import br.dsw.pescd.domain.Inscricao;
import br.dsw.pescd.service.OfertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/aluno")
public class AlunoController {

    @Autowired
    private OfertaService ofertaService;

    @GetMapping("/ofertas")
    public String listarOfertas(Authentication authentication, Model model) {

        String username = authentication.getName();

        List<Inscricao> inscricoes = ofertaService.buscarInscricoesDoAluno(username);

        model.addAttribute("inscricoes", inscricoes);

        return "aluno/ofertas";
    }

    @GetMapping("/plano/enviar")
    public String exibirFormularioPlano(
            @RequestParam("ofertaId") Long ofertaId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            String username = authentication.getName();

            Inscricao inscricao = ofertaService.buscarInscricao(username, ofertaId);

            model.addAttribute("inscricao", inscricao);
            model.addAttribute("professores", ofertaService.listarProfessores());

            return "aluno/enviar-plano";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/aluno/ofertas";
        }
    }

    @PostMapping("/plano/enviar")
    public String enviarPlano(
            @RequestParam("ofertaId") Long ofertaId,
            @RequestParam("codigoDisciplina") String codigoDisciplina,
            @RequestParam("nomeDisciplina") String nomeDisciplina,
            @RequestParam("cursoDisciplina") String cursoDisciplina,
            @RequestParam("professorSupId") Long professorSupId,
            @RequestParam("arquivo") org.springframework.web.multipart.MultipartFile arquivo,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            String username = authentication.getName();

            ofertaService.enviarPlano(
                    username,
                    ofertaId,
                    codigoDisciplina,
                    nomeDisciplina,
                    cursoDisciplina,
                    professorSupId,
                    arquivo
            );

            redirectAttributes.addFlashAttribute("sucesso", "Plano de trabalho enviado com sucesso!");
            return "redirect:/aluno/ofertas";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/aluno/plano/enviar?ofertaId=" + ofertaId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar o arquivo. Tente novamente.");
            return "redirect:/aluno/plano/enviar?ofertaId=" + ofertaId;
        }
    }
}