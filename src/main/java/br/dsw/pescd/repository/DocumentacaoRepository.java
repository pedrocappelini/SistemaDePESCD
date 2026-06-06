package br.dsw.pescd.repository;

import br.dsw.pescd.domain.Documentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentacaoRepository extends JpaRepository<Documentacao, Long> {
}