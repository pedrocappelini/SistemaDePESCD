package br.dsw.pescd.service;

import br.dsw.pescd.domain.Administrador;
import br.dsw.pescd.domain.Aluno;
import br.dsw.pescd.domain.Professor;
import br.dsw.pescd.domain.Secretario;
import br.dsw.pescd.domain.Usuario;
import br.dsw.pescd.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }

    public Usuario salvarNovoUsuario(String tipo, String nomeCompleto, String email, String username, String senha) {
        Usuario usuario;

        switch (tipo.toUpperCase()) {
            case "ADMINISTRADOR":
                usuario = new Administrador();
                break;
            case "PROFESSOR":
                usuario = new Professor();
                break;
            case "SECRETARIO":
                usuario = new Secretario();
                break;
            case "ALUNO":
                usuario = new Aluno();
                break;
            default:
                throw new IllegalArgumentException("Tipo de perfil inválido.");
        }

        usuario.setNomeCompleto(nomeCompleto);
        usuario.setEmail(email);
        usuario.setUsername(username);
        usuario.setSenha(passwordEncoder.encode(senha));

        return usuarioRepository.save(usuario);
    }

    public Usuario atualizarUsuario(Long id, String nomeCompleto, String email, String username, String senha) {
        Usuario usuario = buscarPorId(id);

        usuario.setNomeCompleto(nomeCompleto);
        usuario.setEmail(email);
        usuario.setUsername(username);

        if (senha != null && !senha.trim().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(senha));
        }

        return usuarioRepository.save(usuario);
    }

    public void excluirUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
}
