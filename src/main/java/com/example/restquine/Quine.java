package com.example.restquine;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class Quine {

    public static void main(String[] args) throws IOException {
        // Cria um arquivo JAR
        ByteArrayOutputStream jarOutputStream = createJarFile();

        // Cria o socket do servidor
        ServerSocket serverSocket = new ServerSocket(8080);

        while (true) {
            // Espera por uma conexão
            Socket socket = serverSocket.accept();
            System.out.println("Aceitou a conexão");

            // Escreve a resposta
            try (OutputStream output = socket.getOutputStream()) {
                // Escreve cabeçalhos HTTP para download do arquivo
                PrintWriter writer = new PrintWriter(output, true);
                writer.println("HTTP/1.1 200 OK");
                writer.println("Server: Java HTTP Server: 1.0");
                writer.println("Date: " + new java.util.Date());
                writer.println("Content-type: application/java-archive");
                writer.println("Content-length: " + jarOutputStream.toByteArray().length);
                writer.println("Content-Disposition: attachment; filename=\"Quine.jar\"");
                writer.println(); // Linha em branco entre os cabeçalhos e o conteúdo
                writer.flush();

                output.write(jarOutputStream.toByteArray());
                output.flush();
            }

            // Fecha a conexão
            socket.close();
        }
    }

    private static ByteArrayOutputStream createJarFile() throws IOException {
        String source = buildSourceCOde();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        File tempDir = Files.createTempDirectory("test").toFile();
        File sourceFile = new File(tempDir, "test/Quine.java");
        sourceFile.getParentFile().mkdirs();
        Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));

        compiler.run(null, null, null, "-d", tempDir.getAbsolutePath(), sourceFile.getPath());

        File classFile = new File(tempDir, "com/example/restquine/Quine.class");
        byte[] classBytes = Files.readAllBytes(classFile.toPath());

        // Cria o Manifesto
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, "com.example.restquine.Quine");

        ByteArrayOutputStream jarOutputStream = new ByteArrayOutputStream();
        JarOutputStream jarOut = new JarOutputStream(jarOutputStream, manifest);

        // Adiciona a classe ao JAR
        JarEntry classEntry = new JarEntry("com/example/restquine/Quine.class");
        jarOut.putNextEntry(classEntry);
        jarOut.write(classBytes);
        jarOut.closeEntry();
        // Fecha o JarOutputStream
        jarOut.close();

        return jarOutputStream;
    }

    private static String buildSourceCOde() {
        String textBlockQuotes = new String(new char[]{'"', '"', '"'});
        char newLine = 10;
        String fileName = "Quine.jar";
        String code = """
                package com.example.restquine;

                import javax.tools.JavaCompiler;
                import javax.tools.ToolProvider;
                import java.io.*;
                import java.net.ServerSocket;
                import java.net.Socket;
                import java.nio.charset.StandardCharsets;
                import java.nio.file.Files;
                import java.util.jar.Attributes;
                import java.util.jar.JarEntry;
                import java.util.jar.JarOutputStream;
                import java.util.jar.Manifest;

                public class Quine {

                    public static void main(String[] args) throws IOException {
                        // Cria um arquivo JAR
                        ByteArrayOutputStream jarOutputStream = createJarFile();

                        // Cria o socket do servidor
                        ServerSocket serverSocket = new ServerSocket(8080);

                        while (true) {
                            // Espera por uma conexão
                            Socket socket = serverSocket.accept();
                            System.out.println("Aceitou a conexão");

                            // Escreve a resposta
                            try (OutputStream output = socket.getOutputStream()) {
                                // Escreve cabeçalhos HTTP para download do arquivo
                                PrintWriter writer = new PrintWriter(output, true);
                                writer.println("HTTP/1.1 200 OK");
                                writer.println("Server: Java HTTP Server: 1.0");
                                writer.println("Date: " + new java.util.Date());
                                writer.println("Content-type: application/java-archive");
                                writer.println("Content-length: " + jarOutputStream.toByteArray().length);
                                writer.println("Content-Disposition: attachment; filename=%s");
                                writer.println(); // Linha em branco entre os cabeçalhos e o conteúdo
                                writer.flush();

                                output.write(jarOutputStream.toByteArray());
                                output.flush();
                            }

                            // Fecha a conexão
                            socket.close();
                        }
                    }

                    private static ByteArrayOutputStream createJarFile() throws IOException {
                        String source = buildSourceCOde();
                        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                        File tempDir = Files.createTempDirectory("test").toFile();
                        File sourceFile = new File(tempDir, "test/Quine.java");
                        sourceFile.getParentFile().mkdirs();
                        Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));

                        compiler.run(null, null, null, "-d", tempDir.getAbsolutePath(), sourceFile.getPath());

                        File classFile = new File(tempDir, "com/example/restquine/Quine.class");
                        byte[] classBytes = Files.readAllBytes(classFile.toPath());

                        // Cria o Manifesto
                        Manifest manifest = new Manifest();
                        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
                        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, "com.example.restquine.Quine");

                        ByteArrayOutputStream jarOutputStream = new ByteArrayOutputStream();
                        JarOutputStream jarOut = new JarOutputStream(jarOutputStream, manifest);

                        // Adiciona a classe ao JAR
                        JarEntry classEntry = new JarEntry("com/example/restquine/Quine.class");
                        jarOut.putNextEntry(classEntry);
                        jarOut.write(classBytes);
                        jarOut.closeEntry();
                        // Fecha o JarOutputStream
                        jarOut.close();

                        return jarOutputStream;
                    }

                    private static String buildSourceCOde() {
                        String textBlockQuotes = new String(new char[]{'"', '"', '"'});
                        char newLine = 10;
                        String fileName = "Quine.jar";
                        String code = %s;
                        String formatedCode = code.formatted(fileName, textBlockQuotes + newLine + code + textBlockQuotes);
                        System.out.println(formatedCode);
                        return formatedCode;
                    }
                }
                """;
        String formatedCode = code.formatted(fileName, textBlockQuotes + newLine + code + textBlockQuotes);
        System.out.println(formatedCode);
        return formatedCode;
    }
}