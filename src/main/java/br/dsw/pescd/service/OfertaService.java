package br.dsw.pescd.service;

import br.dsw.pescd.domain.Aluno;
import br.dsw.pescd.domain.Inscricao;
import br.dsw.pescd.domain.Oferta;
import br.dsw.pescd.domain.Professor;
import br.dsw.pescd.domain.Secretario;
import br.dsw.pescd.enums.StatusOferta;
import br.dsw.pescd.repository.AlunoRepository;
import br.dsw.pescd.repository.InscricaoRepository;
import br.dsw.pescd.repository.OfertaRepository;
import br.dsw.pescd.repository.ProfessorRepository;
import br.dsw.pescd.repository.SecretarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OfertaService {

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private SecretarioRepository secretarioRepository;


    public List<Inscricao> buscarInscricoesDoAluno(String username) {
        Aluno aluno = alunoRepository.findByUsername(username);
        return inscricaoRepository.findByAluno(aluno);
    }


    public List<Professor> listarProfessores() {
        return professorRepository.findAll();
    }


    public void criarOferta(Oferta oferta, Long professorId, String username) {

        if (!oferta.getDataFim().isAfter(oferta.getDataInicio())) {
            throw new IllegalArgumentException("A data de fim deve ser posterior à data de início.");
        }

        if (oferta.getNome() == null || oferta.getNome().isBlank()) {
            oferta.setNome("PESCD - " + oferta.getSemestre());
        }

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));
        oferta.setProfessorResponsavel(professor);

        Secretario secretario = secretarioRepository.findByUsername(username);
        oferta.setCriadoPor(secretario);
        oferta.setDataHoraCriacao(LocalDateTime.now());

        oferta.setStatus(StatusOferta.EM_ANDAMENTO);

        ofertaRepository.save(oferta);
    }


    public List<Oferta> listarTodasOfertas() {
        return ofertaRepository.findAll();
    }
}