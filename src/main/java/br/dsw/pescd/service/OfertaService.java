package br.dsw.pescd.service;

import br.dsw.pescd.domain.Aluno;
import br.dsw.pescd.domain.Inscricao;
import br.dsw.pescd.repository.AlunoRepository;
import br.dsw.pescd.repository.InscricaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfertaService {

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    public List<Inscricao> buscarInscricoesDoAluno(String username) {
        Aluno aluno = alunoRepository.findByUsername(username);
        return inscricaoRepository.findByAluno(aluno);
    }
}