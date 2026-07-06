package br.dsw.pescd.dto;

import br.dsw.pescd.domain.Administrador;
import br.dsw.pescd.domain.Aluno;
import br.dsw.pescd.domain.AvaliacaoResponsavel;
import br.dsw.pescd.domain.Documentacao;
import br.dsw.pescd.domain.Inscricao;
import br.dsw.pescd.domain.Oferta;
import br.dsw.pescd.domain.PlanoTrabalho;
import br.dsw.pescd.domain.Professor;
import br.dsw.pescd.domain.RelatorioFinal;
import br.dsw.pescd.domain.Secretario;
import br.dsw.pescd.domain.Usuario;
import br.dsw.pescd.dto.ApiDtos.AlunoOfertaResponse;
import br.dsw.pescd.dto.ApiDtos.AvaliacaoResponsavelResponse;
import br.dsw.pescd.dto.ApiDtos.DocumentacaoResponse;
import br.dsw.pescd.dto.ApiDtos.EstatisticasOfertaResponse;
import br.dsw.pescd.dto.ApiDtos.HistoricoAlunoItemResponse;
import br.dsw.pescd.dto.ApiDtos.InscricaoDetalheResponse;
import br.dsw.pescd.dto.ApiDtos.OfertaDetalheResponse;
import br.dsw.pescd.dto.ApiDtos.OfertaResponse;
import br.dsw.pescd.dto.ApiDtos.PlanoResponse;
import br.dsw.pescd.dto.ApiDtos.ProfessorResponse;
import br.dsw.pescd.dto.ApiDtos.RelatorioResponse;
import br.dsw.pescd.dto.ApiDtos.UsuarioResumoResponse;
import br.dsw.pescd.enums.TipoConclusao;

import java.util.List;

public final class ApiMapper {

    private ApiMapper() {
    }

    public static UsuarioResumoResponse usuarioResumo(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return new UsuarioResumoResponse(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getUsername(),
                tipoUsuario(usuario)
        );
    }

    public static ProfessorResponse professor(Professor professor) {
        return new ProfessorResponse(
                professor.getId(),
                professor.getNomeCompleto(),
                professor.getEmail(),
                professor.getUsername()
        );
    }

    public static OfertaResponse oferta(Oferta oferta, int alunosMatriculados) {
        return new OfertaResponse(
                oferta.getId(),
                oferta.getNome(),
                oferta.getSemestre(),
                oferta.getDataInicio(),
                oferta.getDataFim(),
                oferta.getStatus().name(),
                usuarioResumo(oferta.getProfessorResponsavel()),
                alunosMatriculados,
                oferta.getDataHoraCriacao(),
                oferta.getDescricaoLicoesAprendidas(),
                oferta.getDataHoraSolicitacaoEncerramento(),
                oferta.getDataHoraEncerramento(),
                usuarioResumo(oferta.getEncerradoPor())
        );
    }

    public static AlunoOfertaResponse alunoOferta(Inscricao inscricao, int alunosMatriculados) {
        return new AlunoOfertaResponse(
                inscricao.getId(),
                oferta(inscricao.getOferta(), alunosMatriculados),
                inscricao.getStatus().name(),
                usuarioResumo(inscricao.getProfessorSupervisor())
        );
    }

    public static PlanoResponse plano(PlanoTrabalho plano) {
        if (plano == null) {
            return null;
        }

        return new PlanoResponse(
                plano.getId(),
                plano.getInscricao().getId(),
                plano.getCodigoDisciplina(),
                plano.getNomeDisciplina(),
                plano.getCursoDisciplina(),
                plano.getNomeArquivo(),
                plano.getParecerSupervisor(),
                plano.getDataHoraAvaliacao()
        );
    }

    public static DocumentacaoResponse documentacao(Documentacao documentacao) {
        if (documentacao == null) {
            return null;
        }

        return new DocumentacaoResponse(
                documentacao.getId(),
                documentacao.getInscricao().getId(),
                documentacao.getInstituicao(),
                documentacao.getNomeDisciplina(),
                documentacao.getCursoDisciplina(),
                documentacao.getCargaHoraria(),
                documentacao.getNomeArquivo()
        );
    }

    public static RelatorioResponse relatorio(RelatorioFinal relatorio) {
        if (relatorio == null) {
            return null;
        }

        return new RelatorioResponse(
                relatorio.getId(),
                relatorio.getInscricao().getId(),
                relatorio.getFrequencia(),
                relatorio.getNomeArquivo(),
                relatorio.getParecerSupervisor(),
                relatorio.getSugestaoNota(),
                relatorio.getDataHoraAvaliacao()
        );
    }

    public static AvaliacaoResponsavelResponse avaliacao(AvaliacaoResponsavel avaliacao) {
        if (avaliacao == null) {
            return null;
        }

        return new AvaliacaoResponsavelResponse(
                avaliacao.getId(),
                avaliacao.getInscricao().getId(),
                usuarioResumo(avaliacao.getProfessorResponsavel()),
                avaliacao.getParecer(),
                avaliacao.getFrequencia(),
                avaliacao.getNota(),
                avaliacao.getTipo().name(),
                avaliacao.getDataHoraAvaliacao()
        );
    }

    public static InscricaoDetalheResponse inscricaoDetalhe(
            Inscricao inscricao,
            PlanoTrabalho plano,
            Documentacao documentacao,
            RelatorioFinal relatorio,
            AvaliacaoResponsavel avaliacao
    ) {
        return new InscricaoDetalheResponse(
                inscricao.getId(),
                usuarioResumo(inscricao.getAluno()),
                inscricao.getStatus().name(),
                usuarioResumo(inscricao.getProfessorSupervisor()),
                plano(plano),
                documentacao(documentacao),
                relatorio(relatorio),
                avaliacao(avaliacao)
        );
    }

    public static OfertaDetalheResponse ofertaDetalhe(
            Oferta oferta,
            int alunosMatriculados,
            List<InscricaoDetalheResponse> inscricoes,
            EstatisticasOfertaResponse estatisticas
    ) {
        return new OfertaDetalheResponse(
                oferta(oferta, alunosMatriculados),
                inscricoes,
                estatisticas
        );
    }

    public static HistoricoAlunoItemResponse historico(
            Inscricao inscricao,
            int alunosMatriculados,
            AvaliacaoResponsavel avaliacao
    ) {
        return new HistoricoAlunoItemResponse(
                inscricao.getId(),
                oferta(inscricao.getOferta(), alunosMatriculados),
                inscricao.getStatus().name(),
                avaliacao(avaliacao)
        );
    }

    public static String tipoUsuario(Usuario usuario) {
        if (usuario instanceof Administrador) {
            return "ADMINISTRADOR";
        }
        if (usuario instanceof Aluno) {
            return "ALUNO";
        }
        if (usuario instanceof Professor) {
            return "PROFESSOR";
        }
        if (usuario instanceof Secretario) {
            return "SECRETARIO";
        }
        return usuario.getClass().getSimpleName().toUpperCase();
    }

    public static EstatisticasOfertaResponse estatisticas(List<AvaliacaoResponsavel> avaliacoes) {
        double media = avaliacoes.stream()
                .mapToInt(AvaliacaoResponsavel::getFrequencia)
                .average()
                .orElse(0.0);

        return new EstatisticasOfertaResponse(
                media,
                avaliacoes.stream().filter(a -> a.getTipo() == TipoConclusao.RELATORIO).count(),
                avaliacoes.stream().filter(a -> a.getTipo() == TipoConclusao.DOCUMENTACAO).count(),
                countNota(avaliacoes, "A"),
                countNota(avaliacoes, "B"),
                countNota(avaliacoes, "C"),
                countNota(avaliacoes, "D"),
                countNota(avaliacoes, "E")
        );
    }

    private static long countNota(List<AvaliacaoResponsavel> avaliacoes, String nota) {
        return avaliacoes.stream()
                .filter(a -> nota.equalsIgnoreCase(a.getNota()))
                .count();
    }
}
