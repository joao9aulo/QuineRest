package com.example.restquine;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.jar.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MinimalServer {

    public static void main(String[] args) throws IOException {
        // Cria um arquivo JAR
        createJarFile();

        // Cria o socket do servidor
        ServerSocket serverSocket = new ServerSocket(8080);

        while (true) {
            // Espera por uma conexão
            Socket socket = serverSocket.accept();
            System.out.println("Aceitou a conexão");

            ByteArrayOutputStream jarOutputStream =  createJarFile();
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

                // Fecha o input stream do arquivo
            }

            // Fecha a conexão
            socket.close();
        }
    }

    private static ByteArrayOutputStream createJarFile() throws IOException {
        String source = "package test; public class Quine{ public static void main(String[] args) { System.out.println(\"teste\"); } }";
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        File tempDir = Files.createTempDirectory("test").toFile();
        File sourceFile = new File(tempDir, "test/Quine.java");
        sourceFile.getParentFile().mkdirs();
        Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));

        compiler.run(null, null, null, "-d", tempDir.getAbsolutePath(), sourceFile.getPath());

        File classFile = new File(tempDir, "test/Quine.class");
        byte[] classBytes = Files.readAllBytes(classFile.toPath());

        // Criar um Manifesto
        Manifest manifest = new Manifest();
        Attributes mainAttributes = manifest.getMainAttributes();
        mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        mainAttributes.put(Attributes.Name.MAIN_CLASS, "test/Quine");

        // Adicionar atributos personalizados, se necessário
        // mainAttributes.put(new Attributes.Name("Chave"), "Valor");

        // Escrever o Manifesto em um Arquivo
        FileOutputStream fos = new FileOutputStream("manifest.mf");
        manifest.write(fos);
        fos.close();

        ByteArrayOutputStream jarOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(jarOutputStream);
        ZipEntry classEntry = new ZipEntry("test/Quine.class");
        ZipEntry manifestEntry = new ZipEntry("manifest.mf");
        zipOutputStream.putNextEntry(classEntry);
        zipOutputStream.putNextEntry(manifestEntry);
        zipOutputStream.write(classBytes);
        zipOutputStream.close();
        zipOutputStream.close();

        return jarOutputStream;
    }

    private static void addFileToJar(String source, JarOutputStream target) throws IOException {
        File file = new File(source);
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            JarEntry entry = new JarEntry(source.replace("\\", "/"));
            target.putNextEntry(entry);

            byte[] buffer = new byte[1024];
            int count;
            while ((count = in.read(buffer)) > 0) {
                target.write(buffer, 0, count);
            }
            target.closeEntry();
        }
    }
}