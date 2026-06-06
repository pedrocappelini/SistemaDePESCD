package br.dsw.pescd;

import br.dsw.pescd.domain.*;
import br.dsw.pescd.enums.StatusAlunoOferta;
import br.dsw.pescd.enums.StatusOferta;
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

    // Inicialização de dados para testar o funcionamento do banco de dados e as estórias implementadas
    @Bean
    public CommandLineRunner testarBanco(UsuarioRepository usuarioRepo, OfertaRepository ofertaRepo, InscricaoRepository inscRepo) {
        return args -> {
            // --- 1. CADASTRO DE USUÁRIOS BASE ---

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

            // --- 2. CADASTRO DE ALUNOS ---

            // para al02
            Aluno aluno1 = new Aluno();
            aluno1.setNomeCompleto("Pedro Cappelini");
            aluno1.setEmail("pedrocapp@estudante.ufscar.br");
            aluno1.setUsername("pedro.aluno");
            aluno1.setRA("832795");
            aluno1.setSenha("pedro1");
            usuarioRepo.save(aluno1);

            //para al01
            Aluno aluno2 = new Aluno();
            aluno2.setNomeCompleto("Ana Estagiaria");
            aluno2.setEmail("ana@estudante.ufscar.br");
            aluno2.setUsername("ana.aluno");
            aluno2.setRA("112233");
            aluno2.setSenha("ana1");
            usuarioRepo.save(aluno2);

            // --- 3. CADASTRO DE OFERTAS ---

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

            //para al01
            Oferta ofertaPassada = new Oferta();
            ofertaPassada.setNome("Engenharia de Software 2");
            ofertaPassada.setSemestre("2025/2");
            ofertaPassada.setDataInicio(LocalDate.of(2025, 8, 1));
            ofertaPassada.setDataFim(LocalDate.of(2025, 12, 10));
            ofertaPassada.setStatus(StatusOferta.CONCLUIDA);
            ofertaPassada.setProfessorResponsavel(profSupervisor); // Professora Maria como responsável aqui
            ofertaPassada.setCriadoPor(sec);
            ofertaPassada.setDataHoraCriacao(LocalDateTime.now().minusMonths(6));
            ofertaRepo.save(ofertaPassada);


            //al02
            Inscricao inscricaoPedro = new Inscricao();
            inscricaoPedro.setAluno(aluno1);
            inscricaoPedro.setOferta(ofertaAtual);
            inscricaoPedro.setStatus(StatusAlunoOferta.NAO_ENVIADO); // Explicitamente não enviado
            inscRepo.save(inscricaoPedro);

            Inscricao inscricaoAnaAtual = new Inscricao();
            inscricaoAnaAtual.setAluno(aluno2);
            inscricaoAnaAtual.setOferta(ofertaAtual);
            inscricaoAnaAtual.setProfessorSupervisor(profSupervisor);
            inscricaoAnaAtual.setStatus(StatusAlunoOferta.PLANO_APROVADO);
            inscRepo.save(inscricaoAnaAtual);

            //al01
            Inscricao inscricaoAnaPassada = new Inscricao();
            inscricaoAnaPassada.setAluno(aluno2);
            inscricaoAnaPassada.setOferta(ofertaPassada);
            inscricaoAnaPassada.setProfessorSupervisor(profResponsavel);
            inscricaoAnaPassada.setStatus(StatusAlunoOferta.CONCLUIDO_RESPONSAVEL);
            inscRepo.save(inscricaoAnaPassada);
        };
    }
}