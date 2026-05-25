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

    //para testar o funcionamento do banco de dados
    @Bean
    public CommandLineRunner testarBanco(UsuarioRepository usuarioRepo, OfertaRepository ofertaRepo, InscricaoRepository inscRepo) {
        return args -> {
            Secretario sec = new Secretario();
            sec.setNomeCompleto("Secretario Lucas");
            sec.setEmail("lucas@ufscar.br");
            sec.setUsername("lucas.sec");
            sec.setSenha("lucas678");
            usuarioRepo.save(sec);

            Professor prof = new Professor();
            prof.setNomeCompleto("Professor Luis");
            prof.setEmail("Luis@ufscar.br");
            prof.setUsername("luis.prof");
            prof.setSenha("luis98765");
            usuarioRepo.save(prof);

            Oferta oferta = new Oferta();
            oferta.setNome("Desenvolvimento de Software para Web 1");
            oferta.setSemestre("2026/1");
            oferta.setDataInicio(LocalDate.of(2026, 3, 1));
            oferta.setDataFim(LocalDate.of(2026, 7, 9));
            oferta.setStatus(StatusOferta.EM_ANDAMENTO);
            oferta.setProfessorResponsavel(prof);
            oferta.setCriadoPor(sec);
            oferta.setDataHoraCriacao(LocalDateTime.now());
            ofertaRepo.save(oferta);

            Aluno aluno = new Aluno();
            aluno.setNomeCompleto("Pedro Cappelini");
            aluno.setEmail("pedrocapp@estudante.ufscar.br");
            aluno.setUsername("pedro.aluno");
            aluno.setRA("832795");
            aluno.setSenha("pedro1234");
            usuarioRepo.save(aluno);

            //inscrevendo o Aluno na Oferta
            Inscricao inscricao = new Inscricao();
            inscricao.setAluno(aluno);
            inscricao.setOferta(oferta);
            inscRepo.save(inscricao);

            // ---------------------------------

            Professor profSupervisor = new Professor();
            profSupervisor.setNomeCompleto("Professora Maria");
            profSupervisor.setEmail("maria.supervisora@ufscar.br");
            profSupervisor.setUsername("maria.sup");
            profSupervisor.setSenha("maria1234");
            usuarioRepo.save(profSupervisor);

            Aluno aluno2 = new Aluno();
            aluno2.setNomeCompleto("Ana Estagiaria");
            aluno2.setEmail("ana@estudante.ufscar.br");
            aluno2.setUsername("ana.aluno");
            aluno2.setRA("112233");
            aluno2.setSenha("ana123456");
            usuarioRepo.save(aluno2);

            Inscricao inscricao2 = new Inscricao();
            inscricao2.setAluno(aluno2);
            inscricao2.setOferta(oferta);

            inscricao2.setProfessorSupervisor(profSupervisor);
            inscricao2.setStatus(StatusAlunoOferta.PLANO_APROVADO);

            inscRepo.save(inscricao2);
        };
    }
}