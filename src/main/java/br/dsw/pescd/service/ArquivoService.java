package br.dsw.pescd.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ArquivoService {

    public record ArquivoPdf(
            String nomeArquivo,
            String nomeArquivoOriginal,
            String contentType,
            Long tamanhoBytes,
            byte[] conteudo
    ) {
    }

    public ArquivoPdf prepararPdf(MultipartFile arquivo, String prefixo, Long inscricaoId) throws IOException {
        validarPdf(arquivo);

        String nomeOriginal = arquivo.getOriginalFilename();
        String nomeArquivo = prefixo + "_" + inscricaoId + "_" + System.currentTimeMillis() + ".pdf";

        return new ArquivoPdf(
                nomeArquivo,
                nomeOriginal == null || nomeOriginal.isBlank() ? nomeArquivo : nomeOriginal,
                "application/pdf",
                arquivo.getSize(),
                arquivo.getBytes()
        );
    }

    public void validarPdf(MultipartFile arquivo) {
        if (arquivo.isEmpty()) {
            throw new IllegalArgumentException("O arquivo não pode estar vazio.");
        }

        String contentType = arquivo.getContentType();
        String nomeOriginal = arquivo.getOriginalFilename();

        boolean tipoCorreto = "application/pdf".equalsIgnoreCase(contentType);
        boolean extensaoCorreta = nomeOriginal != null && nomeOriginal.toLowerCase().endsWith(".pdf");

        if (!tipoCorreto || !extensaoCorreta) {
            throw new IllegalArgumentException("O arquivo deve ser um PDF.");
        }

        long maxTamanho = 5 * 1024 * 1024;
        if (arquivo.getSize() > maxTamanho) {
            throw new IllegalArgumentException("O arquivo deve ter no máximo 5MB.");
        }
    }
}
