package br.dsw.pescd.controller;

import br.dsw.pescd.domain.Documentacao;
import br.dsw.pescd.domain.Inscricao;
import br.dsw.pescd.domain.Oferta;
import br.dsw.pescd.domain.PlanoTrabalho;
import br.dsw.pescd.domain.RelatorioFinal;
import br.dsw.pescd.repository.DocumentacaoRepository;
import br.dsw.pescd.repository.PlanoTrabalhoRepository;
import br.dsw.pescd.repository.RelatorioFinalRepository;
import br.dsw.pescd.service.ProfessorResponsavelService;
import br.dsw.pescd.service.OfertaService;
import br.dsw.pescd.service.InscricaoService;
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
@RequestMapping("/professor/responsavel")
public class ProfessorResponsavelController {

    @Autowired
    private ProfessorResponsavelService prService;

    @Autowired
    private PlanoTrabalhoRepository planoTrabalhoRepository;

    @Autowired
    private RelatorioFinalRepository relatorioFinalRepository;

    @Autowired
    private DocumentacaoRepository documentacaoRepository;

    @Autowired
    private OfertaService ofertaService;

    @Autowired
    private InscricaoService inscricaoService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();

        List<Inscricao> inscricoes = prService.listarInscricoesDoResponsavel(username);

        Map<Oferta, List<Inscricao>> inscricoesPorOferta = inscricoes.stream()
                .collect(Collectors.groupingBy(Inscricao::getOferta));

        model.addAttribute("inscricoesPorOferta", inscricoesPorOferta);

        return "professor/responsavel/dashboard";
    }

    @GetMapping("/ofertas/{id}/acompanhar")
    public String acompanharOferta(@PathVariable("id") Long ofertaId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Oferta oferta = ofertaService.buscarOfertaPorId(ofertaId);
            List<Inscricao> inscricoes = inscricaoService.listarInscricoesDaOferta(ofertaId);

            model.addAttribute("oferta", oferta);
            model.addAttribute("inscricoes", inscricoes);

            return "professor/responsavel/acompanhar-oferta";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/professor/responsavel/dashboard";
        }
    }

    @PostMapping("/ofertas/{id}/encerrar")
    public String solicitarEncerramentoOferta(@PathVariable("id") Long ofertaId, RedirectAttributes redirectAttributes) {
        try {
            ofertaService.solicitarEncerramento(ofertaId);
            redirectAttributes.addFlashAttribute("sucesso", "Oferta encaminhada para encerramento pela Secretaria!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/professor/responsavel/dashboard";
    }

    @GetMapping("/relatorios/{inscricaoId}/concluir")
    public String exibirFormularioConclusao(
            @PathVariable("inscricaoId") Long inscricaoId,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Inscricao inscricao = prService.buscarInscricaoPorId(inscricaoId);

            PlanoTrabalho plano = planoTrabalhoRepository.findByInscricao(inscricao)
                    .orElseThrow(() -> new IllegalArgumentException("Plano não encontrado."));

            RelatorioFinal relatorio = relatorioFinalRepository.findByInscricao(inscricao)
                    .orElseThrow(() -> new IllegalArgumentException("Relatório não encontrado."));

            model.addAttribute("inscricao", inscricao);
            model.addAttribute("plano", plano);
            model.addAttribute("relatorio", relatorio);

            return "professor/responsavel/concluir-relatorio";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/professor/responsavel/dashboard";
        }
    }

    @PostMapping("/relatorios/{inscricaoId}/concluir")
    public String concluirRelatorio(
            @PathVariable("inscricaoId") Long inscricaoId,
            @RequestParam("parecer") String parecer,
            @RequestParam("frequencia") Integer frequencia,
            @RequestParam("nota") String nota,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            String username = authentication.getName();
            prService.concluirRelatorio(inscricaoId, parecer, frequencia, nota, username);

            redirectAttributes.addFlashAttribute("sucesso",
                    "Relatório concluído com sucesso! Aluno avaliado.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/professor/responsavel/relatorios/" + inscricaoId + "/concluir";
        }

        return "redirect:/professor/responsavel/dashboard";
    }

    @GetMapping("/documentacoes/{inscricaoId}/analisar")
    public String exibirFormularioAnalise(
            @PathVariable("inscricaoId") Long inscricaoId,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Inscricao inscricao = prService.buscarInscricaoPorId(inscricaoId);

            Documentacao documentacao = documentacaoRepository.findByInscricao(inscricao)
                    .orElseThrow(() -> new IllegalArgumentException("Documentação não encontrada."));

            model.addAttribute("inscricao", inscricao);
            model.addAttribute("documentacao", documentacao);

            return "professor/responsavel/analisar-documentacao";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/professor/responsavel/dashboard";
        }
    }

    @PostMapping("/documentacoes/{inscricaoId}/analisar")
    public String analisarDocumentacao(
            @PathVariable("inscricaoId") Long inscricaoId,
            @RequestParam("parecer") String parecer,
            @RequestParam("frequencia") Integer frequencia,
            @RequestParam("nota") String nota,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            String username = authentication.getName();
            prService.analisarDocumentacao(inscricaoId, parecer, frequencia, nota, username);

            redirectAttributes.addFlashAttribute("sucesso",
                    "Documentação analisada com sucesso! Aluno avaliado.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/professor/responsavel/documentacoes/" + inscricaoId + "/analisar";
        }

        return "redirect:/professor/responsavel/dashboard";
    }
}