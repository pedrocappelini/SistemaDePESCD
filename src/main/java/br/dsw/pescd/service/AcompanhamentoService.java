package br.dsw.pescd.service;

import br.dsw.pescd.domain.AvaliacaoResponsavel;
import br.dsw.pescd.domain.Documentacao;
import br.dsw.pescd.domain.Inscricao;
import br.dsw.pescd.domain.Oferta;
import br.dsw.pescd.domain.PlanoTrabalho;
import br.dsw.pescd.domain.RelatorioFinal;
import br.dsw.pescd.dto.ApiDtos.InscricaoDetalheResponse;
import br.dsw.pescd.dto.ApiDtos.OfertaDetalheResponse;
import br.dsw.pescd.dto.ApiMapper;
import br.dsw.pescd.repository.AvaliacaoResponsavelRepository;
import br.dsw.pescd.repository.DocumentacaoRepository;
import br.dsw.pescd.repository.InscricaoRepository;
import br.dsw.pescd.repository.PlanoTrabalhoRepository;
import br.dsw.pescd.repository.RelatorioFinalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AcompanhamentoService {

    private final InscricaoRepository inscricaoRepository;
    private final PlanoTrabalhoRepository planoTrabalhoRepository;
    private final DocumentacaoRepository documentacaoRepository;
    private final RelatorioFinalRepository relatorioFinalRepository;
    private final AvaliacaoResponsavelRepository avaliacaoResponsavelRepository;
    private final OfertaService ofertaService;

    public AcompanhamentoService(
            InscricaoRepository inscricaoRepository,
            PlanoTrabalhoRepository planoTrabalhoRepository,
            DocumentacaoRepository documentacaoRepository,
            RelatorioFinalRepository relatorioFinalRepository,
            AvaliacaoResponsavelRepository avaliacaoResponsavelRepository,
            OfertaService ofertaService
    ) {
        this.inscricaoRepository = inscricaoRepository;
        this.planoTrabalhoRepository = planoTrabalhoRepository;
        this.documentacaoRepository = documentacaoRepository;
        this.relatorioFinalRepository = relatorioFinalRepository;
        this.avaliacaoResponsavelRepository = avaliacaoResponsavelRepository;
        this.ofertaService = ofertaService;
    }

    public OfertaDetalheResponse detalharOferta(Long ofertaId) {
        Oferta oferta = ofertaService.buscarOfertaPorId(ofertaId);
        List<Inscricao> inscricoes = inscricaoRepository.findByOferta(oferta);
        List<InscricaoDetalheResponse> inscricoesResponse = inscricoes.stream()
                .map(this::detalharInscricao)
                .toList();
        List<AvaliacaoResponsavel> avaliacoes = inscricoes.stream()
                .map(inscricao -> avaliacaoResponsavelRepository.findByInscricao(inscricao).orElse(null))
                .filter(avaliacao -> avaliacao != null)
                .toList();

        return ApiMapper.ofertaDetalhe(
                oferta,
                inscricoes.size(),
                inscricoesResponse,
                ApiMapper.estatisticas(avaliacoes)
        );
    }

    public InscricaoDetalheResponse detalharInscricao(Inscricao inscricao) {
        PlanoTrabalho plano = planoTrabalhoRepository.findByInscricao(inscricao).orElse(null);
        Documentacao documentacao = documentacaoRepository.findByInscricao(inscricao).orElse(null);
        RelatorioFinal relatorio = relatorioFinalRepository.findByInscricao(inscricao).orElse(null);
        AvaliacaoResponsavel avaliacao = avaliacaoResponsavelRepository.findByInscricao(inscricao).orElse(null);

        return ApiMapper.inscricaoDetalhe(inscricao, plano, documentacao, relatorio, avaliacao);
    }
}
