package br.dsw.pescd.repository;

import br.dsw.pescd.domain.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Aluno findByUsername(String username);

    Optional<Aluno> findByEmail(String email);
}