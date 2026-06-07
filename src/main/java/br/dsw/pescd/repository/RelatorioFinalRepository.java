package br.dsw.pescd.repository;

import br.dsw.pescd.domain.Inscricao;
import br.dsw.pescd.domain.RelatorioFinal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RelatorioFinalRepository extends JpaRepository<RelatorioFinal, Long> {

    Optional<RelatorioFinal> findByInscricao(Inscricao inscricao);
}