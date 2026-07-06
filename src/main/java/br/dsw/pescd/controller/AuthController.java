package br.dsw.pescd.controller;

import br.dsw.pescd.domain.Usuario;
import br.dsw.pescd.dto.ApiDtos.UsuarioResumoResponse;
import br.dsw.pescd.dto.ApiMapper;
import br.dsw.pescd.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "autenticacao")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/api/me")
    @Operation(summary = "consultar usuario autenticado")
    public UsuarioResumoResponse me(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByUsername(authentication.getName());
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario nao encontrado.");
        }

        return ApiMapper.usuarioResumo(usuario);
    }
}
