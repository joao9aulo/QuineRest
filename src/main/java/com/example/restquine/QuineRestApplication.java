package com.example.restquine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@SpringBootApplication
public class QuineRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuineRestApplication.class, args);
    }
}

@RestController
class QuineController {

    @GetMapping("/code")
    public String getCode() throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        createJar();

        return "OK";
    }

    private void createJar() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
// Prepare source somehow.
        String source = "package test; public class Test { static { System.out.println(\"aehoooo\"); } public Test() { System.out.println(\"world\"); } }";

// Save source in .java file.
        File root = Files.createTempDirectory("java").toFile();
        File sourceFile = new File(root, "test/Test.java");
        sourceFile.getParentFile().mkdirs();
        Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));

// Compile source file.
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, sourceFile.getPath());

// Load and instantiate compiled class.
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});
        Class<?> cls = Class.forName("test.Test", true, classLoader); // Should print "hello".
        Object instance = cls.getDeclaredConstructor().newInstance(); // Should print "world".
        System.out.println(instance); // Should print "test.Test@hashcode".
    }
}