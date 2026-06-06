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

    @GetMapping("/documentacao/enviar")
    public String exibirFormularioDocumentacao(
            @RequestParam("ofertaId") Long ofertaId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            String username = authentication.getName();
            // PC-1 é garantida pelo Spring Security e Authentication.
            // PC-2 é validada pelo buscarInscricao (gera erro se não achar).
            Inscricao inscricao = ofertaService.buscarInscricao(username, ofertaId);

            model.addAttribute("inscricao", inscricao);

            return "aluno/enviar-documentacao"; // Você precisará criar essa view HTML

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/aluno/ofertas";
        }
    }

    @PostMapping("/documentacao/enviar")
    public String enviarDocumentacao(
            @RequestParam("ofertaId") Long ofertaId,
            @RequestParam("instituicao") String instituicao,
            @RequestParam("nomeDisciplina") String nomeDisciplina,
            @RequestParam("cursoDisciplina") String cursoDisciplina,
            @RequestParam("cargaHoraria") Integer cargaHoraria,
            @RequestParam("arquivo") org.springframework.web.multipart.MultipartFile arquivo,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            String username = authentication.getName();

            ofertaService.enviarDocumentacao(
                    username,
                    ofertaId,
                    instituicao,
                    nomeDisciplina,
                    cursoDisciplina,
                    cargaHoraria,
                    arquivo
            );

            redirectAttributes.addFlashAttribute("sucesso", "Documentação enviada com sucesso! Você solicitou os créditos.");
            return "redirect:/aluno/ofertas";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/aluno/documentacao/enviar?ofertaId=" + ofertaId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar o arquivo. Tente novamente.");
            return "redirect:/aluno/documentacao/enviar?ofertaId=" + ofertaId;
        }
    }

    @Autowired
    private br.dsw.pescd.repository.PlanoTrabalhoRepository planoTrabalhoRepository;

    @GetMapping("/relatorio/enviar")
    public String exibirFormularioRelatorio(
            @RequestParam("ofertaId") Long ofertaId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            String username = authentication.getName();
            Inscricao inscricao = ofertaService.buscarInscricao(username, ofertaId);

            // Validação antecipada da PC-4 para evitar carregar a tela atoa
            if (inscricao.getStatus() != br.dsw.pescd.enums.StatusAlunoOferta.PLANO_APROVADO) {
                throw new IllegalArgumentException("Seu plano de trabalho precisa estar aprovado para enviar o relatório.");
            }

            // RN-1: Buscar o plano de trabalho para exibir dados de leitura
            br.dsw.pescd.domain.PlanoTrabalho plano = planoTrabalhoRepository.findByInscricao(inscricao)
                    .orElse(null);

            model.addAttribute("inscricao", inscricao);
            model.addAttribute("plano", plano);

            return "aluno/enviar-relatorio";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/aluno/ofertas";
        }
    }

    @PostMapping("/relatorio/enviar")
    public String enviarRelatorio(
            @RequestParam("ofertaId") Long ofertaId,
            @RequestParam("frequencia") Integer frequencia,
            @RequestParam("arquivo") org.springframework.web.multipart.MultipartFile arquivo,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            String username = authentication.getName();

            ofertaService.enviarRelatorioFinal(
                    username,
                    ofertaId,
                    frequencia,
                    arquivo
            );

            redirectAttributes.addFlashAttribute("sucesso", "Relatório final enviado com sucesso para aprovação!");
            return "redirect:/aluno/ofertas";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/aluno/relatorio/enviar?ofertaId=" + ofertaId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar o arquivo. Tente novamente.");
            return "redirect:/aluno/relatorio/enviar?ofertaId=" + ofertaId;
        }
    }
}