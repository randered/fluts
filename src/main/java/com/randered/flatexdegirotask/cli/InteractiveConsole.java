package com.randered.flatexdegirotask.cli;

import com.randered.flatexdegirotask.domain.FlutResult;
import com.randered.flatexdegirotask.domain.Pile;
import com.randered.flatexdegirotask.service.FlutService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Reads test cases from the console line by line and prints each result.
 *
 * <p>A test case starts with a line containing the number of schuurs {@code Z}, followed by {@code Z}
 * pile lines. Each test case is solved and printed as soon as it is complete, then the console re-prompts.
 * The console stops on a {@code Z = 0} line, on {@code q}/{@code exit}, or on end of input. It runs on a
 * background daemon thread, so the embedded web server keeps serving REST requests at the same time.
 *
 * <p>The console is enabled by default and can be turned off with the {@code app.console.enabled} property.
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InteractiveConsole implements CommandLineRunner {

    static final String ENABLED_PROPERTY = "app.console.enabled";

    FlutService flutService;
    Environment environment;

    @Override
    public void run(String... args) {
        if (!environment.getProperty(ENABLED_PROPERTY, Boolean.class, true)) {
            return;
        }
        Thread consoleThread = new Thread(() -> runConsole(System.in, System.out), "flut-console");
        consoleThread.setDaemon(true);
        consoleThread.start();
    }

    void runConsole(InputStream in, PrintStream out) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        out.print("Flut solver is running.\n");
        out.print("Paste a test case: a line with the number of schuurs Z, followed by Z pile lines. Enter 0 to finish.\n");
        out.print("Type 'q' or send EOF (Ctrl+Z on Windows / Ctrl+D on Unix) to stop the console. The REST API keeps running.\n");

        int caseNumber = 1;
        while (true) {
            out.print("> ");
            out.flush();

            String line;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                out.print("Error reading input: " + e.getMessage() + "\n");
                break;
            }
            if (line == null) {
                break;
            }

            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if ("q".equalsIgnoreCase(trimmed) || "exit".equalsIgnoreCase(trimmed)) {
                break;
            }

            int z;
            try {
                z = Integer.parseInt(trimmed);
            } catch (NumberFormatException e) {
                out.print("Ignoring invalid input: " + trimmed + "\n");
                continue;
            }
            if (z == 0) {
                break;
            }

            List<Pile> piles;
            try {
                piles = readPiles(reader, z);
            } catch (IOException e) {
                out.print("Error reading input: " + e.getMessage() + "\n");
                break;
            }

            FlutResult result;
            try {
                result = flutService.solve(piles);
            } catch (RuntimeException e) {
                out.print("Error solving test case: " + e.getMessage() + "\n");
                break;
            }

            if (caseNumber > 1) {
                out.print("\n");
            }
            out.print("schuurs " + caseNumber + "\n");
            out.print("Maximum profit is " + result.maxProfit() + ".\n");
            out.print("Number of fluts to buy:");
            for (int count : result.counts()) {
                out.print(" " + count);
            }
            out.print("\n");
            caseNumber++;
        }
        out.print("Console closed. The REST API keeps running.\n");
    }

    private List<Pile> readPiles(BufferedReader reader, int z) throws IOException {
        List<Pile> piles = new ArrayList<>(z);
        for (int i = 0; i < z; i++) {
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("Unexpected end of input while reading a pile");
            }
            StringTokenizer tokenizer = new StringTokenizer(line);
            if (!tokenizer.hasMoreTokens()) {
                throw new IOException("Empty pile line");
            }
            int e = nextInt(tokenizer, i);
            List<Integer> prices = new ArrayList<>(e);
            for (int j = 0; j < e; j++) {
                if (!tokenizer.hasMoreTokens()) {
                    throw new IOException("Pile " + (i + 1) + " expects " + e + " prices but has fewer");
                }
                prices.add(nextInt(tokenizer, i));
            }
            piles.add(new Pile(prices));
        }
        return piles;
    }

    private int nextInt(StringTokenizer tokenizer, int pileIndex) throws IOException {
        try {
            return Integer.parseInt(tokenizer.nextToken());
        } catch (NumberFormatException e) {
            throw new IOException("Invalid number in pile " + (pileIndex + 1));
        }
    }
}
