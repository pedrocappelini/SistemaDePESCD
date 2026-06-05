package br.dsw.pescd.repository;

import br.dsw.pescd.domain.Secretario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretarioRepository extends JpaRepository<Secretario, Long> {

    Secretario findByUsername(String username);
}