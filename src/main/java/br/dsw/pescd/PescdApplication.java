package br.dsw.pescd;

import br.dsw.pescd.domain.*;
import br.dsw.pescd.enums.StatusAlunoOferta;
import br.dsw.pescd.enums.StatusOferta;
import br.dsw.pescd.enums.TipoConclusao;
import br.dsw.pescd.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class PescdApplication {

    public static void main(String[] args) {
        SpringApplication.run(PescdApplication.class, args);
    }

    @Bean
    public CommandLineRunner testarBanco(UsuarioRepository usuarioRepo, OfertaRepository ofertaRepo, InscricaoRepository inscRepo, PlanoTrabalhoRepository planoRepo, RelatorioFinalRepository relatorioRepo, DocumentacaoRepository docRepo, AvaliacaoResponsavelRepository avaliacaoRepo) {
        return args -> {

            Administrador admin = new Administrador();
            admin.setNomeCompleto("Administrador Mario");
            admin.setEmail("mario@ufscar.br");
            admin.setUsername("mario.admin");
            admin.setSenha("mario1");
            usuarioRepo.save(admin);

            Aluno aluno4 = new Aluno();
            aluno4.setNomeCompleto("Leonardo Teste Exclusao");
            aluno4.setEmail("leonardo@estudante.ufscar.br");
            aluno4.setUsername("leonardo.aluno");
            aluno4.setRA("999888");
            aluno4.setSenha("leonardo1");
            usuarioRepo.save(aluno4);

            Secretario sec = new Secretario();
            sec.setNomeCompleto("Secretario Lucas");
            sec.setEmail("lucas@ufscar.br");
            sec.setUsername("lucas.sec");
            sec.setSenha("lucas1");
            usuarioRepo.save(sec);

            Professor profResponsavel = new Professor();
            profResponsavel.setNomeCompleto("Professor Luis");
            profResponsavel.setEmail("luis@ufscar.br");
            profResponsavel.setUsername("luis.prof");
            profResponsavel.setSenha("luis1");
            usuarioRepo.save(profResponsavel);

            Professor profSupervisor = new Professor();
            profSupervisor.setNomeCompleto("Professora Maria");
            profSupervisor.setEmail("maria.supervisora@ufscar.br");
            profSupervisor.setUsername("maria.sup");
            profSupervisor.setSenha("maria1");
            usuarioRepo.save(profSupervisor);

            Aluno aluno1 = new Aluno();
            aluno1.setNomeCompleto("Pedro Cappelini");
            aluno1.setEmail("pedrocapp@estudante.ufscar.br");
            aluno1.setUsername("pedro.aluno");
            aluno1.setRA("832795");
            aluno1.setSenha("pedro1");
            usuarioRepo.save(aluno1);

            Aluno aluno2 = new Aluno();
            aluno2.setNomeCompleto("Ana Estagiaria");
            aluno2.setEmail("ana@estudante.ufscar.br");
            aluno2.setUsername("ana.aluno");
            aluno2.setRA("112233");
            aluno2.setSenha("ana1");
            usuarioRepo.save(aluno2);

            Oferta ofertaAtual = new Oferta();
            ofertaAtual.setNome("Desenvolvimento de Software para Web 1");
            ofertaAtual.setSemestre("2026/1");
            ofertaAtual.setDataInicio(LocalDate.of(2026, 3, 1));
            ofertaAtual.setDataFim(LocalDate.of(2026, 7, 9));
            ofertaAtual.setStatus(StatusOferta.EM_ANDAMENTO);
            ofertaAtual.setProfessorResponsavel(profResponsavel);
            ofertaAtual.setCriadoPor(sec);
            ofertaAtual.setDataHoraCriacao(LocalDateTime.now());
            ofertaRepo.save(ofertaAtual);

            Oferta ofertaPassada = new Oferta();
            ofertaPassada.setNome("Engenharia de Software 2");
            ofertaPassada.setSemestre("2025/2");
            ofertaPassada.setDataInicio(LocalDate.of(2025, 8, 1));
            ofertaPassada.setDataFim(LocalDate.of(2025, 12, 10));
            ofertaPassada.setStatus(StatusOferta.CONCLUIDA);
            ofertaPassada.setProfessorResponsavel(profSupervisor);
            ofertaPassada.setCriadoPor(sec);
            ofertaPassada.setDataHoraCriacao(LocalDateTime.now().minusMonths(6));
            ofertaRepo.save(ofertaPassada);

            Inscricao inscricaoPedro = new Inscricao();
            inscricaoPedro.setAluno(aluno1);
            inscricaoPedro.setOferta(ofertaAtual);
            inscricaoPedro.setStatus(StatusAlunoOferta.NAO_ENVIADO);
            inscRepo.save(inscricaoPedro);

            Inscricao inscricaoAnaAtual = new Inscricao();
            inscricaoAnaAtual.setAluno(aluno2);
            inscricaoAnaAtual.setOferta(ofertaAtual);
            inscricaoAnaAtual.setProfessorSupervisor(profSupervisor);
            inscricaoAnaAtual.setStatus(StatusAlunoOferta.PLANO_APROVADO);
            inscRepo.save(inscricaoAnaAtual);

            Inscricao inscricaoAnaPassada = new Inscricao();
            inscricaoAnaPassada.setAluno(aluno2);
            inscricaoAnaPassada.setOferta(ofertaPassada);
            inscricaoAnaPassada.setProfessorSupervisor(profResponsavel);
            inscricaoAnaPassada.setStatus(StatusAlunoOferta.CONCLUIDO_RESPONSAVEL);
            inscRepo.save(inscricaoAnaPassada);

            AvaliacaoResponsavel avaliacaoAnaPassada = new AvaliacaoResponsavel();
            avaliacaoAnaPassada.setInscricao(inscricaoAnaPassada);
            avaliacaoAnaPassada.setProfessorResponsavel(profSupervisor);
            avaliacaoAnaPassada.setParecer("Excelente desempenho. Aluna demonstrou domínio do conteúdo e dedicação durante todo o semestre.");
            avaliacaoAnaPassada.setFrequencia(95);
            avaliacaoAnaPassada.setNota("A");
            avaliacaoAnaPassada.setTipo(TipoConclusao.RELATORIO);
            avaliacaoAnaPassada.setDataHoraAvaliacao(LocalDateTime.now().minusMonths(5));
            avaliacaoRepo.save(avaliacaoAnaPassada);

            Aluno aluno3 = new Aluno();
            aluno3.setNomeCompleto("Carlos Relatorio Pronto");
            aluno3.setEmail("carlos@estudante.ufscar.br");
            aluno3.setUsername("carlos.aluno");
            aluno3.setRA("445566");
            aluno3.setSenha("carlos1");
            usuarioRepo.save(aluno3);

            Inscricao inscCarlos = new Inscricao();
            inscCarlos.setAluno(aluno3);
            inscCarlos.setOferta(ofertaAtual);
            inscCarlos.setProfessorSupervisor(profSupervisor);
            inscCarlos.setStatus(StatusAlunoOferta.RELATORIO_APROVADO_SUPERVISOR);
            inscRepo.save(inscCarlos);

            PlanoTrabalho planoCarlos = new PlanoTrabalho();
            planoCarlos.setInscricao(inscCarlos);
            planoCarlos.setCodigoDisciplina("DC100");
            planoCarlos.setNomeDisciplina("Algoritmos e Estruturas de Dados");
            planoCarlos.setCursoDisciplina("Bacharelado em Ciência da Computação");
            planoCarlos.setNomeArquivo("plano_1_1780779000479.pdf");
            planoCarlos.setParecerSupervisor("Plano coerente e bem estruturado. Aprovado.");
            planoCarlos.setDataHoraAvaliacao(LocalDateTime.now().minusDays(60));
            planoRepo.save(planoCarlos);

            RelatorioFinal relCarlos = new RelatorioFinal();
            relCarlos.setInscricao(inscCarlos);
            relCarlos.setNomeArquivo("relatorio_1_1780808894398.pdf");
            relCarlos.setFrequencia(95);
            relCarlos.setSugestaoNota("A");
            relCarlos.setParecerSupervisor("Excelente desempenho. Aluno demonstrou domínio do conteúdo.");
            relCarlos.setDataHoraAvaliacao(LocalDateTime.now().minusDays(5));
            relatorioRepo.save(relCarlos);

            Aluno aluno5 = new Aluno();
            aluno5.setNomeCompleto("Beatriz Já Lecionou");
            aluno5.setEmail("beatriz@estudante.ufscar.br");
            aluno5.setUsername("bia.aluno");
            aluno5.setRA("778899");
            aluno5.setSenha("bia1");
            usuarioRepo.save(aluno5);

            Inscricao inscBia = new Inscricao();
            inscBia.setAluno(aluno4);
            inscBia.setOferta(ofertaAtual);
            inscBia.setStatus(StatusAlunoOferta.DOCUMENTACAO_ENVIADA);
            inscRepo.save(inscBia);

            Documentacao docBia = new Documentacao();
            docBia.setInscricao(inscBia);
            docBia.setInstituicao("Universidade Federal de São Carlos");
            docBia.setNomeDisciplina("Introdução à Programação");
            docBia.setCursoDisciplina("Engenharia Civil");
            docBia.setCargaHoraria(60);
            docBia.setNomeArquivo("doc_ensino_1_1780781498421.pdf");
            docRepo.save(docBia);

        };
    }
}