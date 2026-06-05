package br.dsw.pescd.controller;

import br.dsw.pescd.domain.Inscricao;
import br.dsw.pescd.service.OfertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        model.addAttribute("nomeAluno", authentication.getName());

        return "aluno/ofertas";
    }
}