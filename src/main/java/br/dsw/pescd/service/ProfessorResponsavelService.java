package br.dsw.pescd.service;

import br.dsw.pescd.domain.AvaliacaoResponsavel;
import br.dsw.pescd.domain.Inscricao;
import br.dsw.pescd.domain.Oferta;
import br.dsw.pescd.domain.Professor;
import br.dsw.pescd.enums.StatusAlunoOferta;
import br.dsw.pescd.enums.TipoConclusao;
import br.dsw.pescd.repository.AvaliacaoResponsavelRepository;
import br.dsw.pescd.repository.InscricaoRepository;
import br.dsw.pescd.repository.OfertaRepository;
import br.dsw.pescd.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class ProfessorResponsavelService {

    private final ProfessorRepository professorRepository;
    private final InscricaoRepository inscricaoRepository;
    private final AvaliacaoResponsavelRepository avaliacaoResponsavelRepository;
    private final OfertaRepository ofertaRepository;

    public ProfessorResponsavelService(
            ProfessorRepository professorRepository,
            InscricaoRepository inscricaoRepository,
            AvaliacaoResponsavelRepository avaliacaoResponsavelRepository,
            OfertaRepository ofertaRepository
    ) {
        this.professorRepository = professorRepository;
        this.inscricaoRepository = inscricaoRepository;
        this.avaliacaoResponsavelRepository = avaliacaoResponsavelRepository;
        this.ofertaRepository = ofertaRepository;
    }

    public List<Inscricao> listarInscricoesDoResponsavel(String username) {
        Professor professor = buscarProfessor(username);
        return inscricaoRepository.findByOferta_ProfessorResponsavel(professor);
    }

    public Inscricao buscarInscricaoPorId(Long id) {
        return inscricaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada."));
    }

    public Inscricao buscarInscricaoDoResponsavel(Long id, String username) {
        Professor professor = buscarProfessor(username);
        Inscricao inscricao = buscarInscricaoPorId(id);
        validarResponsavelDaOferta(professor, inscricao);
        return inscricao;
    }

    public Oferta buscarOfertaDoResponsavel(Long ofertaId, String username) {
        Professor professor = buscarProfessor(username);
        Oferta oferta = ofertaRepository.findById(ofertaId)
                .orElseThrow(() -> new IllegalArgumentException("Oferta não encontrada."));

        if (oferta.getProfessorResponsavel() == null || !oferta.getProfessorResponsavel().equals(professor)) {
            throw new IllegalArgumentException("Você não é o Professor Responsável desta oferta.");
        }

        return oferta;
    }

    public void concluirRelatorio(Long inscricaoId, String parecer,
                                  Integer frequencia, String nota, String username) {

        Professor professor = buscarProfessor(username);
        Inscricao inscricao = buscarInscricaoPorId(inscricaoId);

        validarResponsavelDaOferta(professor, inscricao);
        validarStatus(inscricao, StatusAlunoOferta.RELATORIO_APROVADO_SUPERVISOR);
        validarCampos(parecer, frequencia, nota);

        salvarAvaliacao(inscricao, professor, parecer, frequencia, nota, TipoConclusao.RELATORIO);

        inscricao.setStatus(StatusAlunoOferta.CONCLUIDO_RESPONSAVEL);
        inscricaoRepository.save(inscricao);
    }

    public void analisarDocumentacao(Long inscricaoId, String parecer,
                                     Integer frequencia, String nota, String username) {

        Professor professor = buscarProfessor(username);
        Inscricao inscricao = buscarInscricaoPorId(inscricaoId);

        validarResponsavelDaOferta(professor, inscricao);
        validarStatus(inscricao, StatusAlunoOferta.DOCUMENTACAO_ENVIADA);
        validarCampos(parecer, frequencia, nota);

        salvarAvaliacao(inscricao, professor, parecer, frequencia, nota, TipoConclusao.DOCUMENTACAO);

        inscricao.setStatus(StatusAlunoOferta.CONCLUIDO_RESPONSAVEL);
        inscricaoRepository.save(inscricao);
    }

    private Professor buscarProfessor(String username) {
        Professor professor = professorRepository.findByUsername(username);
        if (professor == null) {
            throw new IllegalArgumentException("Professor não encontrado.");
        }
        return professor;
    }

    private void validarResponsavelDaOferta(Professor professor, Inscricao inscricao) {
        Professor pr = inscricao.getOferta().getProfessorResponsavel();
        if (pr == null || !pr.equals(professor)) {
            throw new IllegalArgumentException(
                    "Você não é o Professor Responsável desta oferta.");
        }
    }

    private void validarStatus(Inscricao inscricao, StatusAlunoOferta esperado) {
        if (inscricao.getStatus() != esperado) {
            throw new IllegalArgumentException(
                    "Esta inscrição não está com o status esperado para esta ação.");
        }
    }

    private void validarCampos(String parecer, Integer frequencia, String nota) {
        if (parecer == null || parecer.isBlank()) {
            throw new IllegalArgumentException("O campo Parecer é obrigatório.");
        }
        if (frequencia == null || frequencia < 0 || frequencia > 100) {
            throw new IllegalArgumentException("A frequência deve estar entre 0 e 100.");
        }
        if (nota == null || !Arrays.asList("A", "B", "C", "D", "E").contains(nota.toUpperCase())) {
            throw new IllegalArgumentException("A nota deve ser A, B, C, D ou E.");
        }
    }

    private void salvarAvaliacao(Inscricao inscricao, Professor professor,
                                 String parecer, Integer frequencia, String nota,
                                 TipoConclusao tipo) {
        AvaliacaoResponsavel avaliacao = new AvaliacaoResponsavel();
        avaliacao.setInscricao(inscricao);
        avaliacao.setProfessorResponsavel(professor);
        avaliacao.setParecer(parecer);
        avaliacao.setFrequencia(frequencia);
        avaliacao.setNota(nota.toUpperCase());
        avaliacao.setTipo(tipo);
        avaliacao.setDataHoraAvaliacao(LocalDateTime.now());
        avaliacaoResponsavelRepository.save(avaliacao);
    }
}
