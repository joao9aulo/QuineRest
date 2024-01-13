package com.example.restquine;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.jar.*;

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

            // Escreve a resposta
            try (OutputStream output = socket.getOutputStream()) {
                File file = new File("example.jar");
                // Escreve cabeçalhos HTTP para download do arquivo
                PrintWriter writer = new PrintWriter(output, true);
                writer.println("HTTP/1.1 200 OK");
                writer.println("Server: Java HTTP Server: 1.0");
                writer.println("Date: " + new java.util.Date());
                writer.println("Content-type: application/java-archive");
                writer.println("Content-length: " + file.length());
                writer.println("Content-Disposition: attachment; filename=\"Quine.jar\"");
                writer.println(); // Linha em branco entre os cabeçalhos e o conteúdo
                writer.flush();

                // Lê o arquivo JAR e envia os dados
                FileInputStream fileIn = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int length;
                while ((length = fileIn.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                output.flush();

                // Fecha o input stream do arquivo
                fileIn.close();
            }

            // Fecha a conexão
            socket.close();
        }
    }

    private static void createJarFile() throws IOException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        try (JarOutputStream target = new JarOutputStream(new FileOutputStream("example.jar"), manifest)) {
            //addFileToJar("com/example/restquine/MinimalServer.class", target);
            // Adicione mais arquivos conforme necessário
        }
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