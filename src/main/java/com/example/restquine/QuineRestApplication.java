package com.example.restquine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    @GetMapping("/jar")
    public ResponseEntity<byte[]> getJar() throws IOException {
        String textBlockQuotes = new String(new char[]{'"', '"', '"'});
        char newLine = 10;
        String code = """
                package com.example.restquine;

                import org.springframework.boot.SpringApplication;
                import org.springframework.boot.autoconfigure.SpringBootApplication;
                import org.springframework.http.*;
                import org.springframework.web.bind.annotation.GetMapping;
                import org.springframework.web.bind.annotation.RestController;

                import javax.tools.JavaCompiler;
                import javax.tools.ToolProvider;
                import java.io.ByteArrayOutputStream;
                import java.io.File;
                import java.io.IOException;
                import java.nio.charset.StandardCharsets;
                import java.nio.file.Files;
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

                    @GetMapping("/jar")
                    public ResponseEntity<byte[]> getJar() throws IOException {
                        // Prepare source code
                        String source = %s;

                        // Compile source code
                        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                        byte[] classBytes = compileSource(source, compiler);

                        // Create JAR in memory
                        ByteArrayOutputStream jarOutputStream = createJar(classBytes);

                        // Create response
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                        ContentDisposition  contentDisposition = ContentDisposition.builder("attachment").filename("test.jar").build();
                        headers.setContentDisposition(contentDisposition);

                        return new ResponseEntity<>(jarOutputStream.toByteArray(), headers, HttpStatus.OK);
                    }

                    private byte[] compileSource(String source, JavaCompiler compiler) throws IOException {
                        File tempDir = Files.createTempDirectory("java").toFile();
                        File sourceFile = new File(tempDir, "test/Test.java");
                        sourceFile.getParentFile().mkdirs();
                        Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));

                        // Compile source file
                        compiler.run(null, null, null, "-d", tempDir.getAbsolutePath(), sourceFile.getPath());

                        // Read compiled class file
                        File classFile = new File(tempDir, "test/Test.class");
                        return Files.readAllBytes(classFile.toPath());
                    }

                    private ByteArrayOutputStream createJar(byte[] classBytes) throws IOException {
                        ByteArrayOutputStream jarOutputStream = new ByteArrayOutputStream();
                        ZipOutputStream zipOutputStream = new ZipOutputStream(jarOutputStream);

                        // Add class file to JAR
                        ZipEntry classEntry = new ZipEntry("test/Test.class");
                        zipOutputStream.putNextEntry(classEntry);
                        zipOutputStream.write(classBytes);
                        zipOutputStream.close();

                        // Close ZIP stream
                        zipOutputStream.close();

                        return jarOutputStream;
                    }
                }
                """;
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        String formatedCode = code.formatted(textBlockQuotes + newLine + code + textBlockQuotes);
        System.out.print(formatedCode);
        byte[] classBytes = compileSource(formatedCode, compiler);

        ByteArrayOutputStream jarOutputStream = createJar(classBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename("test.jar").build();
        headers.setContentDisposition(contentDisposition);

        return new ResponseEntity<>(jarOutputStream.toByteArray(), headers, HttpStatus.OK);
    }

    private byte[] compileSource(String source, JavaCompiler compiler) throws IOException {
        File tempDir = Files.createTempDirectory("java").toFile();
        File sourceFile = new File(tempDir, "test/Test.java");
        sourceFile.getParentFile().mkdirs();
        Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));

        compiler.run(null, null, null, "-d", tempDir.getAbsolutePath(), sourceFile.getPath());

        File classFile = new File(tempDir, "test/Test.class");
        return Files.readAllBytes(classFile.toPath());
    }

    private ByteArrayOutputStream createJar(byte[] classBytes) throws IOException {
        ByteArrayOutputStream jarOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(jarOutputStream);
        ZipEntry classEntry = new ZipEntry("test/Test.class");
        zipOutputStream.putNextEntry(classEntry);
        zipOutputStream.write(classBytes);
        zipOutputStream.close();
        zipOutputStream.close();
        return jarOutputStream;
    }
}