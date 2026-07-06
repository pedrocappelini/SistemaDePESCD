package br.dsw.pescd.service;

import br.dsw.pescd.domain.Aluno;
import br.dsw.pescd.domain.Inscricao;
import br.dsw.pescd.domain.Oferta;
import br.dsw.pescd.enums.StatusAlunoOferta;
import br.dsw.pescd.repository.AlunoRepository;
import br.dsw.pescd.repository.InscricaoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class InscricaoService {

    private final InscricaoRepository inscricaoRepository;
    private final AlunoRepository alunoRepository;
    private final OfertaService ofertaService;
    private final PasswordEncoder passwordEncoder;

    public InscricaoService(
            InscricaoRepository inscricaoRepository,
            AlunoRepository alunoRepository,
            OfertaService ofertaService,
            PasswordEncoder passwordEncoder
    ) {
        this.inscricaoRepository = inscricaoRepository;
        this.alunoRepository = alunoRepository;
        this.ofertaService = ofertaService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Inscricao> buscarInscricoesDoAluno(String username) {
        Aluno aluno = alunoRepository.findByUsername(username);
        return inscricaoRepository.findByAluno(aluno);
    }

    public Inscricao buscarInscricao(String username, Long ofertaId) {
        Aluno aluno = alunoRepository.findByUsername(username);
        Oferta oferta = ofertaService.buscarOfertaPorId(ofertaId);

        return inscricaoRepository.findByAlunoAndOferta(aluno, oferta)
                .orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada."));
    }

    public List<Inscricao> listarInscricoesDaOferta(Long ofertaId) {
        Oferta oferta = ofertaService.buscarOfertaPorId(ofertaId);
        return inscricaoRepository.findByOferta(oferta);
    }

    private void criarInscricao(Aluno aluno, Oferta oferta) {
        if (inscricaoRepository.findByAlunoAndOferta(aluno, oferta).isPresent()) {
            throw new IllegalArgumentException("O aluno " + aluno.getEmail() + " já está inserido nesta oferta.");
        }
        Inscricao inscricao = new Inscricao();
        inscricao.setAluno(aluno);
        inscricao.setOferta(oferta);
        inscricao.setStatus(StatusAlunoOferta.NAO_ENVIADO);
        inscricaoRepository.save(inscricao);
    }

    public void adicionarAlunoExistente(Long ofertaId, String email) {
        Oferta oferta = ofertaService.buscarOfertaPorId(ofertaId);
        Aluno aluno = alunoRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Nenhum aluno encontrado no sistema com o e-mail: " + email));

        criarInscricao(aluno, oferta);
    }

    public void cadastrarNovoAlunoEMatricular(Long ofertaId, String ra, String nomeCompleto, String email) {
        Oferta oferta = ofertaService.buscarOfertaPorId(ofertaId);

        if (alunoRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Já existe um aluno no sistema com este e-mail. Utilize a opção de 'Adicionar Aluno Existente'.");
        }

        Aluno aluno = new Aluno();
        aluno.setRA(ra);
        aluno.setNomeCompleto(nomeCompleto);
        aluno.setEmail(email);
        aluno.setUsername(email);
        aluno.setSenha(passwordEncoder.encode(ra));
        aluno = alunoRepository.save(aluno);

        criarInscricao(aluno, oferta);
    }

    public void adicionarAlunosPorCsv(Long ofertaId, MultipartFile arquivoCsv) throws IOException {
        if (arquivoCsv.isEmpty()) {
            throw new IllegalArgumentException("O arquivo CSV não pode estar vazio.");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(arquivoCsv.getInputStream(), StandardCharsets.UTF_8))) {
            String linha;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                String[] dados = linha.split(",");
                if (dados.length >= 3) {
                    String ra = dados[0].trim();
                    String nome = dados[1].trim();
                    String email = dados[2].trim();

                    try {
                        if (alunoRepository.findByEmail(email).isPresent()) {
                            adicionarAlunoExistente(ofertaId, email);
                        } else {
                            cadastrarNovoAlunoEMatricular(ofertaId, ra, nome, email);
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Aviso no CSV (" + email + "): " + e.getMessage());
                    }
                }
            }
        }
    }
}
