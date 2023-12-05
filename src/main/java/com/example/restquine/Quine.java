package com.example.restquine;

public class Quine {
    public static void main(String[] args) {
        String textBlockQuotes = new String(new char[]{'"', '"', '"'});
        char newLine = 10;
        String source = """
                public class Quine {
                	public static void main(String[] args) {
                		String textBlockQuotes = new String(new char[]{'"', '"', '"'});
                		char newLine = 10;
                		String source = %s;
                		System.out.print(source.formatted(textBlockQuotes + newLine + source + textBlockQuotes));
                	}
                }
                """;
        System.out.print(source.formatted(textBlockQuotes + newLine + source + textBlockQuotes));
    }
}