package ru.job4j.finder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class CriterionFinder {

    private static final String PROMPT = "Usage \"java -jar find.jar\n"
            + "-d = directory where to start searching\n"
            + "-n - file name, mask, or regular expression.\n"
            + "-t - search type: \"mask\" search by mask, \"name\" by "
            + "full name match, \"regex\" by regular expression.\n"
            + "-o - write the result to a file.\n";

    private Path startDir;

    private File output;

    private Predicate<Path> predicate;

    public CriterionFinder(String[] args) {
        check(args);
    }

    public void search() {
        SearchFiles searcher = new SearchFiles(predicate);
        try {
            Files.walkFileTree(startDir, searcher);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        List<Path> paths = searcher.getPaths();
        if (paths.size() == 0) {
            System.out.println("Files not found");
        } else {
            System.out.println("Files found: " + paths.size());
            writePaths(paths);
        }
    }

    private void writePaths(List<Path> paths) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(output))) {
            for (Path path : paths) {
                writer.write(path.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void check(String[] args) {
        if (args.length != 4) {
            throw new IllegalArgumentException("Incorrect number of parameters.\n" + PROMPT);
        }
        final Map<String, String> parameters = getParameters(args);
        startDir = getPath(parameters.get("d"), "start directory");
        if (!Files.exists(startDir)) {
            throw new IllegalArgumentException(
                    String.format("Not exist %s%n", startDir.toFile().getAbsoluteFile()) + PROMPT
            );
        }
        if (!Files.isDirectory(startDir)) {
            throw new IllegalArgumentException(
                    String.format("Not directory %s%n", startDir.toFile().getAbsoluteFile()) + PROMPT
            );
        }
        output = getPath(parameters.get("o"), "output file").toFile();
        predicate = getPredicate(parameters.get("t"), parameters.get("n"));
    }

    private Predicate<Path> getPredicate(String mode, String criterion) {
        Predicate<Path> predicate;
        if (!mode.equals("regex") && !mode.equals("mask") && !mode.equals("name")) {
            throw new IllegalArgumentException("Incorrect mode.\n" + PROMPT);
        }
        if (mode.equals("name")) {
            predicate = p -> p.toFile().getName().equals(criterion);
        } else {
            String regex;
            if (mode.equals("regex")) {
                regex = criterion;
            } else {
                regex = criterion.replace(".", "\\.")
                        .replace("?", ".?")
                        .replace("*", ".+");
            }
            try {
                Pattern pattern = Pattern.compile(regex);
                predicate = p -> {
                    String fileName = p.toFile().getName();
                    return pattern.matcher(fileName).find();
                };
            } catch (PatternSyntaxException e) {
                throw new IllegalArgumentException(
                        "Incorrect criterion. Mode: " + mode + ", criterion: " + criterion + "\n" + PROMPT
                );
            }
        }
        return predicate;
    }

    private Map<String, String> getParameters(String[] args) {
        final Map<String, String> result = new HashMap<>();
        Set<String> params = new HashSet<>(Set.of("d", "n", "t", "o"));
        for (String arg : args) {
            if (!arg.startsWith("-")) {
                throw new IllegalArgumentException("Incorrect pair format. "
                        + "The entry for the parameter must begin with a sign \"-\"." + PROMPT);
            }
            String[] entry = arg.split("=");
            if (entry.length != 2) {
                throw new IllegalArgumentException("Incorrect pair format. "
                        + "Keys and values must be separated by an equal sign.\n" + PROMPT);
            }
            String key = entry[0].substring(1);
            if (!params.remove(key)) {
                throw new IllegalArgumentException("Unexpected key \"" + key + "\"\n" + PROMPT);
            } else {
                result.put(key, entry[1]);
            }
        }
        if (!params.isEmpty()) {
            StringBuilder sb = new StringBuilder("Keys were not received: ");
            boolean firstElem = true;
            for (String param : params) {
                if (!firstElem) {
                    sb.append(", ");
                } else {
                    firstElem = false;
                }
                sb.append("\"").append(param).append("\"");
            }
            sb.append("\n");
            throw new IllegalArgumentException(sb.toString() + PROMPT);
        }
        return result;
    }

    private Path getPath(String path, String message) {
        try {
            return Path.of(path);
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Invalid path " + message + ".\n" + PROMPT, e);
        }
    }

    public static void main(String[] args) {
        new CriterionFinder(args).search();
    }
}
