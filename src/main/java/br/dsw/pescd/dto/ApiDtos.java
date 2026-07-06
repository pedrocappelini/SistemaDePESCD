package br.dsw.pescd.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class ApiDtos {

    private ApiDtos() {
    }

    public record ApiErrorResponse(
            int status,
            String error,
            String message,
            String path,
            LocalDateTime timestamp
    ) {
    }

    public record ApiMessageResponse(String message) {
    }

    public record UsuarioResumoResponse(
            Long id,
            String nomeCompleto,
            String email,
            String username,
            String tipo
    ) {
    }

    public record ProfessorResponse(
            Long id,
            String nomeCompleto,
            String email,
            String username
    ) {
    }

    public record OfertaRequest(
            String nome,
            String semestre,
            LocalDate dataInicio,
            LocalDate dataFim,
            Long professorId
    ) {
    }

    public record OfertaResponse(
            Long id,
            String nome,
            String semestre,
            LocalDate dataInicio,
            LocalDate dataFim,
            String status,
            UsuarioResumoResponse professorResponsavel,
            Integer alunosMatriculados,
            LocalDateTime dataHoraCriacao
    ) {
    }

    public record AlunoOfertaResponse(
            Long inscricaoId,
            OfertaResponse oferta,
            String statusAluno,
            UsuarioResumoResponse professorSupervisor
    ) {
    }

    public record PlanoResponse(
            Long id,
            Long inscricaoId,
            String codigoDisciplina,
            String nomeDisciplina,
            String cursoDisciplina,
            String nomeArquivo,
            String parecerSupervisor,
            LocalDateTime dataHoraAvaliacao
    ) {
    }

    public record DocumentacaoResponse(
            Long id,
            Long inscricaoId,
            String instituicao,
            String nomeDisciplina,
            String cursoDisciplina,
            Integer cargaHoraria,
            String nomeArquivo
    ) {
    }

    public record RelatorioResponse(
            Long id,
            Long inscricaoId,
            Integer frequencia,
            String nomeArquivo,
            String parecerSupervisor,
            String sugestaoNota,
            LocalDateTime dataHoraAvaliacao
    ) {
    }

    public record AvaliacaoResponsavelResponse(
            Long id,
            Long inscricaoId,
            UsuarioResumoResponse professorResponsavel,
            String parecer,
            Integer frequencia,
            String nota,
            String tipo,
            LocalDateTime dataHoraAvaliacao
    ) {
    }

    public record InscricaoDetalheResponse(
            Long id,
            UsuarioResumoResponse aluno,
            String status,
            UsuarioResumoResponse professorSupervisor,
            PlanoResponse plano,
            DocumentacaoResponse documentacao,
            RelatorioResponse relatorio,
            AvaliacaoResponsavelResponse avaliacao
    ) {
    }

    public record OfertaDetalheResponse(
            OfertaResponse oferta,
            List<InscricaoDetalheResponse> inscricoes,
            EstatisticasOfertaResponse estatisticas
    ) {
    }

    public record EstatisticasOfertaResponse(
            Double mediaFrequencia,
            Long concluidosPorRelatorio,
            Long concluidosPorDocumentacao,
            Long notaA,
            Long notaB,
            Long notaC,
            Long notaD,
            Long notaE
    ) {
    }

    public record AvaliacaoRequest(
            String parecer,
            Integer frequencia,
            String nota
    ) {
    }

    public record AprovacaoPlanoRequest(String parecer) {
    }

    public record AprovacaoRelatorioSupervisorRequest(
            String parecer,
            Integer frequencia,
            String sugestaoNota
    ) {
    }

    public record EncerramentoRequest(String descricaoLicoesAprendidas) {
    }

    public record ProgressoResponse(
            List<HistoricoAlunoItemResponse> historico,
            Integer semestresConcluidos,
            Integer semestresMinimos,
            Boolean cumpriuMinimo
    ) {
    }

    public record HistoricoAlunoItemResponse(
            Long inscricaoId,
            OfertaResponse oferta,
            String statusAluno,
            AvaliacaoResponsavelResponse avaliacao
    ) {
    }

    public record UsuarioRequest(
            String tipo,
            String nomeCompleto,
            String email,
            String username,
            String senha
    ) {
    }

    public record UsuarioUpdateRequest(
            String nomeCompleto,
            String email,
            String username,
            String senha
    ) {
    }

    public record EmailRequest(String email) {
    }

    public record NovoAlunoRequest(
            String ra,
            String nomeCompleto,
            String email
    ) {
    }
}
