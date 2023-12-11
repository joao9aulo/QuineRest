package com.example.restquine;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class CreateJar {

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
// Prepare source somehow.
        String source = "package test; public class Test { static { System.out.println(\"aehoooo\"); } public Test() { System.out.println(\"world\"); } }";

// Save source in .java file.
        File root = Files.createTempDirectory("java").toFile();
        File sourceFile = new File(root, "test/Test.java");
        sourceFile.getParentFile().mkdirs();
        Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));

        // Compile source file.
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, "-d", root.getAbsolutePath(), sourceFile.getPath());

// Save compiled class file.
        File classFile = new File(root, "test/Test.class");
        byte[] bytecode = Files.readAllBytes(classFile.toPath());

// Write bytecode to a .jar file.
        File jarFile = new File(root, "test.jar");
        try (JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(jarFile))) {
            JarEntry jarEntry = new JarEntry("test/Test.class");
            jarOutputStream.putNextEntry(jarEntry);
            jarOutputStream.write(bytecode);
            jarOutputStream.closeEntry();
        }

    }
//// Compile source file.
//        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//        compiler.run(null, null, null, sourceFile.getPath());
//
//// Load and instantiate compiled class.
//        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});
//        Class<?> cls = Class.forName("test.Test", true, classLoader); // Should print "hello".
//        Object instance = cls.getDeclaredConstructor().newInstance(); // Should print "world".
//        System.out.println(instance); // Should print "test.Test@hashcode".
//
//    }
}