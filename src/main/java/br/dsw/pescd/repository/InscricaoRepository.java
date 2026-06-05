package br.dsw.pescd.repository;

import br.dsw.pescd.domain.Aluno;
import br.dsw.pescd.domain.Inscricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    List<Inscricao> findByAluno(Aluno aluno);
}