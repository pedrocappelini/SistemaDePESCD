package br.dsw.pescd.dto;

import br.dsw.pescd.domain.AvaliacaoResponsavel;
import br.dsw.pescd.domain.Inscricao;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HistoricoItemDTO {
    private Inscricao inscricao;
    private AvaliacaoResponsavel avaliacao;  // pode ser null se ainda não concluiu
}