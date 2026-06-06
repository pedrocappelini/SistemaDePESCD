package br.dsw.pescd.service;

import br.dsw.pescd.domain.Documentacao;
import br.dsw.pescd.repository.DocumentacaoRepository;
import br.dsw.pescd.domain.*;
import br.dsw.pescd.enums.StatusAlunoOferta;
import br.dsw.pescd.enums.StatusOferta;
import br.dsw.pescd.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.StandardCopyOption;
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

    @Autowired
    private DocumentacaoRepository documentacaoRepository;

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

    public void enviarDocumentacao (
            String username,
            Long ofertaId,
            String instituicao,
            String nomeDisciplina,
            String cursoDisciplina,
            Integer cargaHoraria,
            MultipartFile arquivo) throws IOException {

        Inscricao inscricao = buscarInscricao(username, ofertaId);

        // PC-3: Selecionar uma oferta com status "em andamento"
        if (inscricao.getOferta().getStatus() != StatusOferta.EM_ANDAMENTO) {
            throw new IllegalArgumentException("A oferta não está em andamento.");
        }

        // PC-4: O status do aluno nesta oferta deve ser "não enviado"
        if (inscricao.getStatus() != StatusAlunoOferta.NAO_ENVIADO) {
            throw new IllegalArgumentException("Você já realizou um envio nesta oferta ou a mesma não permite novos envios.");
        }

        // Validação básica RN-1 (Campos obrigatórios)
        if (instituicao == null || instituicao.isBlank() ||
                nomeDisciplina == null || nomeDisciplina.isBlank() ||
                cursoDisciplina == null || cursoDisciplina.isBlank() ||
                cargaHoraria == null || cargaHoraria <= 0) {
            throw new IllegalArgumentException("Todos os campos obrigatórios devem ser preenchidos corretamente.");
        }

        // RN-3: O arquivo deve ser um PDF
        if (arquivo.isEmpty() || !isPDF(arquivo)) {
            throw new IllegalArgumentException("O arquivo com a documentação comprobatória deve ser um PDF.");
        }

        // RN-3: O arquivo deve ter no máximo 5MB
        long maxTamanho = 5 * 1024 * 1024;
        if (arquivo.getSize() > maxTamanho) {
            throw new IllegalArgumentException("O arquivo deve ter no máximo 5MB.");
        }

        // Salvar arquivo físico
        String nomeArquivoSalvo = salvarArquivo(arquivo, "doc_ensino", inscricao.getId());

        // Criar e salvar entidade com os dados (RN-1)
        Documentacao doc = new Documentacao();
        doc.setInstituicao(instituicao);
        doc.setNomeDisciplina(nomeDisciplina);
        doc.setCursoDisciplina(cursoDisciplina);
        doc.setCargaHoraria(cargaHoraria);
        doc.setNomeArquivo(nomeArquivoSalvo);
        doc.setInscricao(inscricao);
        documentacaoRepository.save(doc);

        // RN-4: Envio com sucesso deve mudar o status do aluno para "documentação enviada"
        inscricao.setStatus(StatusAlunoOferta.DOCUMENTACAO_ENVIADA);
        inscricaoRepository.save(inscricao);
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
        Path diretorio = Paths.get(uploadDir).toAbsolutePath().normalize();

        if (!Files.exists(diretorio)) {
            Files.createDirectories(diretorio);
        }

        String nomeArquivo = prefixo + "_" + inscricaoId + "_" + System.currentTimeMillis() + ".pdf";
        Path destino = diretorio.resolve(nomeArquivo);

        Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        return nomeArquivo;
    }
}