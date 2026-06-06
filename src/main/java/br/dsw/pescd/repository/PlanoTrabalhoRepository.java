package br.dsw.pescd.repository;

import br.dsw.pescd.domain.Inscricao;
import br.dsw.pescd.domain.PlanoTrabalho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanoTrabalhoRepository extends JpaRepository<PlanoTrabalho, Long> {

    Optional<PlanoTrabalho> findByInscricao(Inscricao inscricao);
}