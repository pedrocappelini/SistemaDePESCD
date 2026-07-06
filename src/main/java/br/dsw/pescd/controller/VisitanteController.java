package br.dsw.pescd.controller;

import br.dsw.pescd.service.OfertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VisitanteController {

    @Autowired
    private OfertaService ofertaService;

    @GetMapping("/ofertas-publicas")
    public String listarOfertasPublicas(Model model) {
        model.addAttribute("ofertas", ofertaService.listarTodasOfertas());
        return "ofertas-publicas";
    }
}