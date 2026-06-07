package br.dsw.pescd.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ArquivoService {

    @Value("${app.upload.dir}")
    private String uploadDir;

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

    public String salvarArquivo(MultipartFile arquivo, String prefixo, Long inscricaoId) throws IOException {
        Path diretorio = Paths.get(uploadDir).toAbsolutePath().normalize();

        if (!Files.exists(diretorio)) {
            Files.createDirectories(diretorio);
        }

        String nomeArquivo = prefixo + "_" + inscricaoId + "_" + System.currentTimeMillis() + ".pdf";
        Path destino = diretorio.resolve(nomeArquivo);

        Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        return nomeArquivo;
    }
}