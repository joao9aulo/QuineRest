package com.example.restquine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@SpringBootApplication
public class QuineRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuineRestApplication.class, args);
    }
}

@RestController
class QuineController {

    @GetMapping("/code")
    public byte[] getCode() throws IOException {
        // Compilar e criar um JAR temporário
        byte[] jarBytes = createJar();

        // Definir o cabeçalho da resposta para indicar que é um arquivo JAR
        // (você pode ajustar o cabeçalho de acordo com suas necessidades)
        // Content-Disposition é usado para sugerir um nome de arquivo quando o navegador faz o download
        return jarBytes;
    }

    private byte[] createJar() throws IOException {
        // Local do arquivo JAR temporário
        Path tempJar = Files.createTempFile("temp", ".jar");

        // Obter o caminho dos arquivos de classe compilados
        Path classPath = Path.of("target/classes");

        // Criar um arquivo JAR temporário
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(tempJar, StandardOpenOption.CREATE))) {
            Files.walk(classPath)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            String entryName = classPath.relativize(file).toString();
                            zipOutputStream.putNextEntry(new ZipEntry(entryName));
                            Files.copy(file, zipOutputStream);
                            zipOutputStream.closeEntry();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }

        // Ler o conteúdo do JAR temporário
        return Files.readAllBytes(tempJar);
    }
}