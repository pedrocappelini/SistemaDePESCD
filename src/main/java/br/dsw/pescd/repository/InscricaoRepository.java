package br.dsw.pescd.repository;

import br.dsw.pescd.domain.Aluno;
import br.dsw.pescd.domain.Inscricao;
import br.dsw.pescd.domain.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    List<Inscricao> findByAluno(Aluno aluno);

    Optional<Inscricao> findByAlunoAndOferta(Aluno aluno, Oferta oferta);
}