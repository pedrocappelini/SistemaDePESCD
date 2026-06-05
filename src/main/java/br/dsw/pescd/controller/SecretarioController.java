package br.dsw.pescd.controller;

import br.dsw.pescd.domain.Oferta;
import br.dsw.pescd.service.OfertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/secretario")
public class SecretarioController {

    @Autowired
    private OfertaService ofertaService;


    @GetMapping("/ofertas/nova")
    public String exibirFormulario(Model model) {

        model.addAttribute("oferta", new Oferta());

        model.addAttribute("professores", ofertaService.listarProfessores());

        return "secretario/criar-oferta";
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


    @GetMapping("/ofertas")
    public String listarOfertas(Model model) {
        model.addAttribute("ofertas", ofertaService.listarTodasOfertas());
        return "secretario/ofertas";
    }
}