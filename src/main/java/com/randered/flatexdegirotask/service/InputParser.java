package com.randered.flatexdegirotask.service;

import com.randered.flatexdegirotask.domain.Pile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Parses the plain-text problem format into test cases.
 */
@Component
public class InputParser {

    public List<List<Pile>> parse(String input) {
        return readBatch(new Scanner(input));
    }

    /**
     * Reads one batch of test cases, stopping at the terminating {@code Z = 0} (which is consumed).
     * Returns an empty list when the batch contains no test cases.
     */
    public List<List<Pile>> readBatch(Scanner scanner) {
        List<List<Pile>> testCases = new ArrayList<>();
        while (scanner.hasNextInt()) {
            int z = scanner.nextInt();
            if (z == 0) {
                break;
            }
            testCases.add(readPiles(scanner, z));
        }
        return testCases;
    }

    private List<Pile> readPiles(Scanner scanner, int z) {
        List<Pile> piles = new ArrayList<>(z);
        for (int i = 0; i < z; i++) {
            int e = scanner.nextInt();
            List<Integer> prices = new ArrayList<>(e);
            for (int j = 0; j < e; j++) {
                prices.add(scanner.nextInt());
            }
            piles.add(new Pile(prices));
        }
        return piles;
    }
}
