package com.example.restquine;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MinimalServer {

    public static void main(String[] args) throws IOException {
        // Cria o socket do servidor
        ServerSocket serverSocket = new ServerSocket(8080);

        // Espera por uma conexão
        Socket socket = serverSocket.accept();
        System.out.println("Aceitou a conexão");
        // Escreve a resposta
        OutputStream output = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);
        writer.println("Olá mundo");

        // Fecha a conexão
        socket.close();
    }
}