package br.dsw.pescd.controller;

import br.dsw.pescd.domain.Oferta;
import br.dsw.pescd.domain.Inscricao;
import br.dsw.pescd.service.OfertaService;
import br.dsw.pescd.service.InscricaoService;
import br.dsw.pescd.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Controller
@RequestMapping("/secretario")
public class SecretarioController {

    @Autowired
    private OfertaService ofertaService;

    @Autowired
    private InscricaoService inscricaoService;

    @Autowired
    private ProfessorService professorService;

    @GetMapping("/ofertas/nova")
    public String exibirFormulario(Model model) {

        model.addAttribute("oferta", new Oferta());

        model.addAttribute("professores", professorService.listarProfessores());

        return "secretario/criar-oferta";
    }

    @GetMapping("/ofertas/{id}/alunos")
    public String gerenciarAlunos(@PathVariable("id") Long ofertaId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Oferta oferta = ofertaService.buscarOfertaPorId(ofertaId);

            List<Inscricao> inscricoes = inscricaoService.listarInscricoesDaOferta(ofertaId);

            model.addAttribute("oferta", oferta);
            model.addAttribute("inscricoes", inscricoes);

            return "secretario/gerenciar-alunos";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/secretario/ofertas";
        }
    }

    @PostMapping("/ofertas/nova")
    public String criarOferta(
            @ModelAttribute Oferta oferta,
            @RequestParam("professorId") Long professorId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            String username = authentication.getName();
            ofertaService.criarOferta(oferta, professorId, username);

            redirectAttributes.addFlashAttribute("sucesso", "Oferta criada com sucesso!");
            return "redirect:/secretario/ofertas";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/secretario/ofertas/nova";
        }
    }

    @PostMapping("/ofertas/{id}/alunos/csv")
    public String adicionarAlunosCsv(
            @PathVariable("id") Long ofertaId,
            @RequestParam("arquivoCsv") MultipartFile arquivoCsv,
            RedirectAttributes redirectAttributes) {

        try {
            inscricaoService.adicionarAlunosPorCsv(ofertaId, arquivoCsv);
            redirectAttributes.addFlashAttribute("sucesso", "Arquivo CSV processado. Alunos adicionados à oferta!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro inesperado ao ler o arquivo CSV. Verifique a formatação.");
        }

        return "redirect:/secretario/ofertas/" + ofertaId + "/alunos";
    }

    @PostMapping("/ofertas/{id}/alunos/existente")
    public String adicionarAlunoExistente(
            @PathVariable("id") Long ofertaId,
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {

        try {
            inscricaoService.adicionarAlunoExistente(ofertaId, email);
            redirectAttributes.addFlashAttribute("sucesso", "Aluno existente adicionado com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/secretario/ofertas/" + ofertaId + "/alunos";
    }

    @PostMapping("/ofertas/{id}/alunos/novo")
    public String cadastrarNovoAluno(
            @PathVariable("id") Long ofertaId,
            @RequestParam("ra") String ra,
            @RequestParam("nomeCompleto") String nomeCompleto,
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {

        try {
            inscricaoService.cadastrarNovoAlunoEMatricular(ofertaId, ra, nomeCompleto, email);
            redirectAttributes.addFlashAttribute("sucesso", "Novo aluno cadastrado e matriculado com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/secretario/ofertas/" + ofertaId + "/alunos";
    }

    @GetMapping("/ofertas")
    public String listarOfertas(Model model) {
        model.addAttribute("ofertas", ofertaService.listarTodasOfertas());
        return "secretario/ofertas";
    }
}