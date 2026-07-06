package br.dsw.pescd.service;

import br.dsw.pescd.domain.Oferta;
import br.dsw.pescd.domain.Professor;
import br.dsw.pescd.domain.Secretario;
import br.dsw.pescd.enums.StatusAlunoOferta;
import br.dsw.pescd.enums.StatusOferta;
import br.dsw.pescd.repository.InscricaoRepository;
import br.dsw.pescd.repository.OfertaRepository;
import br.dsw.pescd.repository.ProfessorRepository;
import br.dsw.pescd.repository.SecretarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class OfertaService {

    private final OfertaRepository ofertaRepository;
    private final ProfessorRepository professorRepository;
    private final SecretarioRepository secretarioRepository;
    private final InscricaoRepository inscricaoRepository;

    public OfertaService(
            OfertaRepository ofertaRepository,
            ProfessorRepository professorRepository,
            SecretarioRepository secretarioRepository,
            InscricaoRepository inscricaoRepository
    ) {
        this.ofertaRepository = ofertaRepository;
        this.professorRepository = professorRepository;
        this.secretarioRepository = secretarioRepository;
        this.inscricaoRepository = inscricaoRepository;
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
        return ofertaRepository.findAll().stream()
                .sorted(Comparator.comparing(Oferta::getSemestre).reversed())
                .toList();
    }

    public Oferta buscarOfertaPorId(Long ofertaId) {
        return ofertaRepository.findById(ofertaId)
                .orElseThrow(() -> new IllegalArgumentException("Oferta não encontrada."));
    }

    public void solicitarEncerramento(Long ofertaId, String descricaoLicoesAprendidas, String username) {
        Oferta oferta = buscarOfertaPorId(ofertaId);
        Professor professor = professorRepository.findByUsername(username);

        if (professor == null || !professor.equals(oferta.getProfessorResponsavel())) {
            throw new IllegalArgumentException("Você não é o Professor Responsável desta oferta.");
        }

        if (oferta.getStatus() != StatusOferta.EM_ANDAMENTO) {
            throw new IllegalArgumentException("Apenas ofertas em andamento podem ser encaminhadas para encerramento.");
        }

        boolean todosConcluidos = inscricaoRepository.findByOferta(oferta).stream()
                .allMatch(inscricao -> inscricao.getStatus() == StatusAlunoOferta.CONCLUIDO_RESPONSAVEL);

        if (!todosConcluidos) {
            throw new IllegalArgumentException("Todos os alunos precisam estar concluídos pelo responsável.");
        }

        if (descricaoLicoesAprendidas == null || descricaoLicoesAprendidas.isBlank()) {
            throw new IllegalArgumentException("A descrição de lições aprendidas é obrigatória.");
        }

        oferta.setDescricaoLicoesAprendidas(descricaoLicoesAprendidas);
        oferta.setDataHoraSolicitacaoEncerramento(LocalDateTime.now());
        oferta.setStatus(StatusOferta.AGUARDANDO_ENCERRAMENTO);
        ofertaRepository.save(oferta);
    }

    public void encerrarOferta(Long ofertaId, String username) {
        Oferta oferta = buscarOfertaPorId(ofertaId);

        if (oferta.getStatus() != StatusOferta.AGUARDANDO_ENCERRAMENTO) {
            throw new IllegalArgumentException("Apenas ofertas aguardando encerramento podem ser concluídas.");
        }

        Secretario secretario = secretarioRepository.findByUsername(username);
        if (secretario == null) {
            throw new IllegalArgumentException("Secretário não encontrado.");
        }

        oferta.setEncerradoPor(secretario);
        oferta.setDataHoraEncerramento(LocalDateTime.now());
        oferta.setStatus(StatusOferta.CONCLUIDA);
        ofertaRepository.save(oferta);
    }
}
