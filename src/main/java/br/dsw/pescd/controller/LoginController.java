package br.dsw.pescd.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login"; 
    }

    @GetMapping("/login/redirecionar")
    public String redirecionar(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            switch (authority.getAuthority()) {
                case "ROLE_ALUNO":         return "redirect:/aluno/ofertas";
                case "ROLE_SECRETARIO":    return "redirect:/secretario/ofertas";
                case "ROLE_PROFESSOR":     return "redirect:/professor/ofertas";
                case "ROLE_ADMINISTRADOR": return "redirect:/admin/usuarios";
            }
        }
        return "redirect:/login?error";
    }
}