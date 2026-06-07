package br.dsw.pescd.service;

import br.dsw.pescd.domain.Inscricao;
import br.dsw.pescd.domain.PlanoTrabalho;
import br.dsw.pescd.domain.Professor;
import br.dsw.pescd.enums.StatusAlunoOferta;
import br.dsw.pescd.repository.InscricaoRepository;
import br.dsw.pescd.repository.PlanoTrabalhoRepository;
import br.dsw.pescd.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private PlanoTrabalhoRepository planoTrabalhoRepository;

    public List<Inscricao> buscarInscricoesDoSupervisor(String username) {
        Professor professor = professorRepository.findByUsername(username);
        if (professor == null) {
            throw new IllegalArgumentException("Professor não encontrado.");
        }
        return inscricaoRepository.findByProfessorSupervisor(professor);
    }

    public Inscricao buscarInscricaoPorId(Long id) {
        return inscricaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada."));
    }

    public void aprovarPlanoDeTrabalho(Long inscricaoId, String parecer, String username) {
        Professor professor = professorRepository.findByUsername(username);

        Inscricao inscricao = buscarInscricaoPorId(inscricaoId);

        if (!inscricao.getProfessorSupervisor().equals(professor)) {
            throw new IllegalArgumentException("Você não é o supervisor responsável por este aluno.");
        }

        if (inscricao.getStatus() != StatusAlunoOferta.PLANO_ENVIADO) {
            throw new IllegalArgumentException("O plano de trabalho não está com status 'enviado' para ser aprovado.");
        }

        if (parecer == null || parecer.isBlank()) {
            throw new IllegalArgumentException("O campo Parecer é obrigatório.");
        }

        PlanoTrabalho plano = planoTrabalhoRepository.findByInscricao(inscricao)
                .orElseThrow(() -> new IllegalArgumentException("Plano de trabalho não encontrado no banco de dados."));

        plano.setParecerSupervisor(parecer);
        plano.setDataHoraAvaliacao(LocalDateTime.now());
        planoTrabalhoRepository.save(plano);

        inscricao.setStatus(StatusAlunoOferta.PLANO_APROVADO);
        inscricaoRepository.save(inscricao);
    }
}