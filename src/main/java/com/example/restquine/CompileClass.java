package com.example.restquine;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;

public class CompileClass {

    public static void main(String[] args) throws Exception {
        // Obtém o compilador Java
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        // Obtém a classe
        Class<?> clazz = CompileClass.class;

        // Obtém o nome da classe
        String className = clazz.getName();

        // Obtém o fluxo de entrada da classe
        InputStream inputStream = clazz.getClassLoader().getResourceAsStream(className + ".class");

        // Lê o conteúdo do fluxo de entrada
        byte[] bytes = inputStream.readAllBytes();

        // Compila o código
        compiler.run(null, null, null, Arrays.toString(bytes));

        // Cria um fluxo de saída para o arquivo
        FileOutputStream outputStream = new FileOutputStream("MinhaClasse.class");

        // Grava a classe compilada no arquivo
        outputStream.write(clazz.getClassLoader().getResourceAsStream(className + ".class").readAllBytes());

        // Fecha o fluxo de saída
        outputStream.close();
    }
}