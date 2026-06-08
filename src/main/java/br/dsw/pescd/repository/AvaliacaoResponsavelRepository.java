package br.dsw.pescd.repository;

import br.dsw.pescd.domain.AvaliacaoResponsavel;
import br.dsw.pescd.domain.Inscricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvaliacaoResponsavelRepository extends JpaRepository<AvaliacaoResponsavel, Long> {

    Optional<AvaliacaoResponsavel> findByInscricao(Inscricao inscricao);
}