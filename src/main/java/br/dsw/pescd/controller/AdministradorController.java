package br.dsw.pescd.controller;

import br.dsw.pescd.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/usuarios")
public class AdministradorController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "admin/usuarios";
    }

    @GetMapping("/novo")
    public String exibirFormulario() {
        return "admin/form-usuario";
    }

    @PostMapping("/novo")
    public String criar(
            @RequestParam("tipo") String tipo,
            @RequestParam("nomeCompleto") String nomeCompleto,
            @RequestParam("email") String email,
            @RequestParam("username") String username,
            @RequestParam("senha") String senha,
            RedirectAttributes redirectAttributes) {

        try {
            usuarioService.salvarNovoUsuario(tipo, nomeCompleto, email, username, senha);
            redirectAttributes.addFlashAttribute("sucesso", "Usuário criado com sucesso!");
            return "redirect:/admin/usuarios";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao criar usuário: " + e.getMessage());
            return "redirect:/admin/usuarios/novo";
        }
    }

    @GetMapping("/{id}/editar")
    public String exibirFormularioEdicao(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("usuario", usuarioService.buscarPorId(id));
            return "admin/form-editar-usuario";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    @PostMapping("/{id}/editar")
    public String atualizar(
            @PathVariable("id") Long id,
            @RequestParam("nomeCompleto") String nomeCompleto,
            @RequestParam("email") String email,
            @RequestParam("username") String username,
            @RequestParam(value = "senha", required = false) String senha,
            RedirectAttributes redirectAttributes) {

        try {
            usuarioService.atualizarUsuario(id, nomeCompleto, email, username, senha);
            redirectAttributes.addFlashAttribute("sucesso", "Usuário atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar usuário: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.excluirUsuario(id);
            redirectAttributes.addFlashAttribute("sucesso", "Usuário removido com sucesso!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("erro", "Não é possível excluir este usuário, pois ele possui vínculos ativos (ofertas, inscrições, relatórios).");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro inesperado ao remover usuário.");
        }
        return "redirect:/admin/usuarios";
    }
}