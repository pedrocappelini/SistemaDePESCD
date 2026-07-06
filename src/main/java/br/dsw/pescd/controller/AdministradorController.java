package br.dsw.pescd.controller;

import br.dsw.pescd.domain.Usuario;
import br.dsw.pescd.dto.ApiDtos.ApiMessageResponse;
import br.dsw.pescd.dto.ApiDtos.UsuarioRequest;
import br.dsw.pescd.dto.ApiDtos.UsuarioResumoResponse;
import br.dsw.pescd.dto.ApiDtos.UsuarioUpdateRequest;
import br.dsw.pescd.dto.ApiMapper;
import br.dsw.pescd.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/usuarios")
@Tag(name = "administrador")
public class AdministradorController {

    private final UsuarioService usuarioService;

    public AdministradorController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "listar usuarios")
    public List<UsuarioResumoResponse> listar() {
        return usuarioService.listarTodos().stream()
                .map(ApiMapper::usuarioResumo)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "consultar usuario")
    public UsuarioResumoResponse buscar(@PathVariable Long id) {
        return ApiMapper.usuarioResumo(usuarioService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "criar usuario")
    public ResponseEntity<UsuarioResumoResponse> criar(@RequestBody UsuarioRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Corpo da requisicao e obrigatorio.");
        }

        Usuario usuario = usuarioService.salvarNovoUsuario(
                request.tipo(),
                request.nomeCompleto(),
                request.email(),
                request.username(),
                request.senha()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiMapper.usuarioResumo(usuario));
    }

    @PutMapping("/{id}")
    @Operation(summary = "atualizar usuario")
    public UsuarioResumoResponse atualizar(
            @PathVariable Long id,
            @RequestBody UsuarioUpdateRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Corpo da requisicao e obrigatorio.");
        }

        Usuario usuario = usuarioService.atualizarUsuario(
                id,
                request.nomeCompleto(),
                request.email(),
                request.username(),
                request.senha()
        );

        return ApiMapper.usuarioResumo(usuario);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "excluir usuario")
    public ApiMessageResponse excluir(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario.getUsername().equals(authentication.getName())) {
            throw new IllegalArgumentException("Não é possível remover seu próprio usuário.");
        }

        usuarioService.excluirUsuario(id);
        return new ApiMessageResponse("Usuario removido com sucesso.");
    }
}
