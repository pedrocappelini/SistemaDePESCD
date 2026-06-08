package br.dsw.pescd.service;

import br.dsw.pescd.domain.Aluno;
import br.dsw.pescd.domain.AvaliacaoResponsavel;
import br.dsw.pescd.domain.Inscricao;
import br.dsw.pescd.domain.Usuario;
import br.dsw.pescd.dto.HistoricoItemDTO;
import br.dsw.pescd.enums.StatusAlunoOferta;
import br.dsw.pescd.repository.AvaliacaoResponsavelRepository;
import br.dsw.pescd.repository.InscricaoRepository;
import br.dsw.pescd.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class HistoricoService {

    private static final int SEMESTRES_MINIMOS = 2;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private AvaliacaoResponsavelRepository avaliacaoResponsavelRepository;

    public List<HistoricoItemDTO> buscarHistoricoDoAluno(String username) {
        Aluno aluno = buscarAluno(username);

        List<Inscricao> inscricoes = inscricaoRepository.findByAluno(aluno);

        return inscricoes.stream()
                .sorted(Comparator.comparing(
                        (Inscricao i) -> i.getOferta().getDataInicio()
                ).reversed())
                .map(i -> {
                    AvaliacaoResponsavel avaliacao = avaliacaoResponsavelRepository
                            .findByInscricao(i)
                            .orElse(null);
                    return new HistoricoItemDTO(i, avaliacao);
                })
                .toList();
    }

    public int contarSemestresConcluidos(List<HistoricoItemDTO> historico) {
        return (int) historico.stream()
                .filter(item -> item.getInscricao().getStatus() == StatusAlunoOferta.CONCLUIDO_RESPONSAVEL)
                .count();
    }

    public int getSemestresMinimos() {
        return SEMESTRES_MINIMOS;
    }

    private Aluno buscarAluno(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username);
        if (!(usuario instanceof Aluno aluno)) {
            throw new IllegalArgumentException("Usuário não encontrado ou não é aluno.");
        }
        return aluno;
    }
}