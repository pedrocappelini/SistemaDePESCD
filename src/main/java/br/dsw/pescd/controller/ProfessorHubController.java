package br.dsw.pescd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/professor")
public class ProfessorHubController {

    @GetMapping
    public String hub() {
        return "professor/hub";
    }
}