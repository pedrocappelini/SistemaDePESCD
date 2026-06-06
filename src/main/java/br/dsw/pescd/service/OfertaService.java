package br.dsw.pescd.service;

import br.dsw.pescd.domain.*;
import br.dsw.pescd.enums.StatusAlunoOferta;
import br.dsw.pescd.enums.StatusOferta;
import br.dsw.pescd.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Autowired
    private PlanoTrabalhoRepository planoTrabalhoRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

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

    public Inscricao buscarInscricao(String username, Long ofertaId) {
        Aluno aluno = alunoRepository.findByUsername(username);
        Oferta oferta = ofertaRepository.findById(ofertaId)
                .orElseThrow(() -> new IllegalArgumentException("Oferta não encontrada."));

        return inscricaoRepository.findByAlunoAndOferta(aluno, oferta)
                .orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada."));
    }

    public void enviarPlano(
            String username,
            Long ofertaId,
            String codigoDisciplina,
            String nomeDisciplina,
            String cursoDisciplina,
            Long professorSupId,
            MultipartFile arquivo) throws IOException {

        Inscricao inscricao = buscarInscricao(username, ofertaId);

        if (inscricao.getOferta().getStatus() != StatusOferta.EM_ANDAMENTO) {
            throw new IllegalArgumentException("A oferta não está em andamento.");
        }

        if (inscricao.getStatus() != StatusAlunoOferta.NAO_ENVIADO) {
            throw new IllegalArgumentException("Você já realizou um envio nesta oferta.");
        }

        if (arquivo.isEmpty() || !isPDF(arquivo)) {
            throw new IllegalArgumentException("O arquivo deve ser um PDF.");
        }

        long maxTamanho = 5 * 1024 * 1024; 
        if (arquivo.getSize() > maxTamanho) {
            throw new IllegalArgumentException("O arquivo deve ter no máximo 5MB.");
        }

        String nomeArquivoSalvo = salvarArquivo(arquivo, "plano", inscricao.getId());

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

    private boolean isPDF(MultipartFile arquivo) {
        String contentType = arquivo.getContentType();
        String nomeOriginal = arquivo.getOriginalFilename();

        boolean tipoCorreto = "application/pdf".equalsIgnoreCase(contentType);
        boolean extensaoCorreta = nomeOriginal != null && nomeOriginal.toLowerCase().endsWith(".pdf");

        return tipoCorreto && extensaoCorreta;
    }

    private String salvarArquivo(MultipartFile arquivo, String prefixo, Long inscricaoId) throws IOException {
        Path diretorio = Paths.get(uploadDir);
        if (!Files.exists(diretorio)) {
            Files.createDirectories(diretorio);
        }

        String nomeArquivo = prefixo + "_" + inscricaoId + "_" + System.currentTimeMillis() + ".pdf";
        Path destino = diretorio.resolve(nomeArquivo);

        arquivo.transferTo(destino.toFile());

        return nomeArquivo;
    }
}