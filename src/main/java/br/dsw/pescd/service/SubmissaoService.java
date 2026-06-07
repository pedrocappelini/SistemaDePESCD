package br.dsw.pescd.service;

import br.dsw.pescd.domain.*;
import br.dsw.pescd.enums.StatusAlunoOferta;
import br.dsw.pescd.enums.StatusOferta;
import br.dsw.pescd.repository.DocumentacaoRepository;
import br.dsw.pescd.repository.PlanoTrabalhoRepository;
import br.dsw.pescd.repository.ProfessorRepository;
import br.dsw.pescd.repository.RelatorioFinalRepository;
import br.dsw.pescd.repository.InscricaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class SubmissaoService {

    @Autowired
    private InscricaoService inscricaoService;

    @Autowired
    private ArquivoService arquivoService;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private DocumentacaoRepository documentacaoRepository;

    @Autowired
    private RelatorioFinalRepository relatorioFinalRepository;

    @Autowired
    private PlanoTrabalhoRepository planoTrabalhoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    public void enviarDocumentacao(String username, Long ofertaId, String instituicao, String nomeDisciplina,
                                   String cursoDisciplina, Integer cargaHoraria, MultipartFile arquivo) throws IOException {

        Inscricao inscricao = inscricaoService.buscarInscricao(username, ofertaId);

        if (inscricao.getOferta().getStatus() != StatusOferta.EM_ANDAMENTO) {
            throw new IllegalArgumentException("A oferta não está em andamento.");
        }

        if (inscricao.getStatus() != StatusAlunoOferta.NAO_ENVIADO) {
            throw new IllegalArgumentException("Você já realizou um envio nesta oferta ou a mesma não permite novos envios.");
        }

        if (instituicao == null || instituicao.isBlank() || nomeDisciplina == null || nomeDisciplina.isBlank() ||
                cursoDisciplina == null || cursoDisciplina.isBlank() || cargaHoraria == null || cargaHoraria <= 0) {
            throw new IllegalArgumentException("Todos os campos obrigatórios devem ser preenchidos corretamente.");
        }

        arquivoService.validarPdf(arquivo);
        String nomeArquivoSalvo = arquivoService.salvarArquivo(arquivo, "doc_ensino", inscricao.getId());

        Documentacao doc = new Documentacao();
        doc.setInstituicao(instituicao);
        doc.setNomeDisciplina(nomeDisciplina);
        doc.setCursoDisciplina(cursoDisciplina);
        doc.setCargaHoraria(cargaHoraria);
        doc.setNomeArquivo(nomeArquivoSalvo);
        doc.setInscricao(inscricao);
        documentacaoRepository.save(doc);

        inscricao.setStatus(StatusAlunoOferta.DOCUMENTACAO_ENVIADA);
        inscricaoRepository.save(inscricao);
    }

    public void enviarRelatorioFinal(String username, Long ofertaId, Integer frequencia, MultipartFile arquivo) throws IOException {
        Inscricao inscricao = inscricaoService.buscarInscricao(username, ofertaId);

        if (inscricao.getOferta().getStatus() != StatusOferta.EM_ANDAMENTO) {
            throw new IllegalArgumentException("A oferta não está em andamento.");
        }

        if (inscricao.getStatus() != StatusAlunoOferta.PLANO_APROVADO) {
            throw new IllegalArgumentException("Você só pode enviar o relatório final se o seu plano de trabalho estiver aprovado.");
        }

        if (frequencia == null || frequencia < 0 || frequencia > 100) {
            throw new IllegalArgumentException("A frequência deve ser um valor entre 0 e 100.");
        }

        arquivoService.validarPdf(arquivo);
        String nomeArquivoSalvo = arquivoService.salvarArquivo(arquivo, "relatorio", inscricao.getId());

        RelatorioFinal relatorio = new RelatorioFinal();
        relatorio.setFrequencia(frequencia);
        relatorio.setNomeArquivo(nomeArquivoSalvo);
        relatorio.setInscricao(inscricao);
        relatorioFinalRepository.save(relatorio);

        inscricao.setStatus(StatusAlunoOferta.RELATORIO_ENVIADO);
        inscricaoRepository.save(inscricao);
    }

    public void enviarPlano(String username, Long ofertaId, String codigoDisciplina, String nomeDisciplina,
                            String cursoDisciplina, Long professorSupId, MultipartFile arquivo) throws IOException {

        Inscricao inscricao = inscricaoService.buscarInscricao(username, ofertaId);

        if (inscricao.getOferta().getStatus() != StatusOferta.EM_ANDAMENTO) {
            throw new IllegalArgumentException("A oferta não está em andamento.");
        }

        if (inscricao.getStatus() != StatusAlunoOferta.NAO_ENVIADO) {
            throw new IllegalArgumentException("Você já realizou um envio nesta oferta.");
        }

        arquivoService.validarPdf(arquivo);
        String nomeArquivoSalvo = arquivoService.salvarArquivo(arquivo, "plano", inscricao.getId());

        Professor supervisor = professorRepository.findById(professorSupId)
                .orElseThrow(() -> new IllegalArgumentException("Professor supervisor não encontrado."));

        PlanoTrabalho plano = new PlanoTrabalho();
        plano.setCodigoDisciplina(codigoDisciplina);
        plano.setNomeDisciplina(nomeDisciplina);
        plano.setCursoDisciplina(cursoDisciplina);
        plano.setNomeArquivo(nomeArquivoSalvo);
        plano.setInscricao(inscricao);
        planoTrabalhoRepository.save(plano);

        inscricao.setProfessorSupervisor(supervisor);
        inscricao.setStatus(StatusAlunoOferta.PLANO_ENVIADO);
        inscricaoRepository.save(inscricao);
    }
}