package com.randered.flatexdegirotask.service;

import com.randered.flatexdegirotask.domain.FlutResult;
import com.randered.flatexdegirotask.domain.Pile;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class FlutSolverTest {

    private final FlutSolver solver = new FlutSolver();

    @Test
    void solvesSinglePile() {
        FlutResult result = solver.solve(List.of(new Pile(List.of(12, 3, 10, 7, 16, 5))));

        assertThat(result.maxProfit()).isEqualTo(8);
        assertThat(result.counts()).containsExactly(4);
    }

    @Test
    void solvesTwoPilesAndCombinesCounts() {
        List<Pile> piles = List.of(
                new Pile(List.of(7, 3, 11, 9, 10)),
                new Pile(List.of(1, 2, 3, 4, 10, 16, 10, 4, 16))
        );

        FlutResult result = solver.solve(piles);

        assertThat(result.maxProfit()).isEqualTo(40);
        assertThat(result.counts()).containsExactly(6, 7, 8, 9, 10, 12, 13);
    }

    @Test
    void noProfitMeansBuyNothing() {
        FlutResult result = solver.solve(List.of(new Pile(List.of(11, 12))));

        assertThat(result.maxProfit()).isEqualTo(0);
        assertThat(result.counts()).containsExactly(0);
    }

    @Test
    void mustBuyThroughALossToReachProfitableBoxes() {
        // The first box is expensive, but the following cheap boxes make the whole prefix profitable.
        FlutResult result = solver.solve(List.of(new Pile(List.of(12, 1, 1, 1, 1))));

        assertThat(result.maxProfit()).isEqualTo(34);
        assertThat(result.counts()).containsExactly(5);
    }

    @Test
    void largePricesDoNotOverflow() {
        // Cost sum exceeds Integer.MAX_VALUE; the cumulative cost must be computed with long.
        FlutResult result = solver.solve(List.of(new Pile(List.of(2_000_000_000, 2_000_000_000))));

        assertThat(result.maxProfit()).isEqualTo(0);
        assertThat(result.counts()).containsExactly(0);
    }

    @Test
    void emptyPileIsHandled() {
        FlutResult result = solver.solve(List.of(new Pile(List.of())));

        assertThat(result.maxProfit()).isEqualTo(0);
        assertThat(result.counts()).containsExactly(0);
    }

    @Test
    void reportsOnlyTenSmallestCounts() {
        // Every box costs exactly the selling price, so any prefix yields zero profit.
        List<Integer> prices = java.util.Collections.nCopies(15, 10);

        FlutResult result = solver.solve(List.of(new Pile(prices)));

        assertThat(result.maxProfit()).isEqualTo(0);
        assertThat(result.counts()).containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    @Test
    void deduplicatesCountsFromMultiplePiles() {
        // Two identical piles: each can contribute 0 or 1 boxes for zero profit.
        List<Pile> piles = List.of(
                new Pile(List.of(10)),
                new Pile(List.of(10))
        );

        FlutResult result = solver.solve(piles);

        assertThat(result.maxProfit()).isEqualTo(0);
        assertThat(result.counts()).containsExactly(0, 1, 2);
    }

    @Test
    void emptyPilesYieldZeroProfitAndZeroCount() {
        FlutResult result = solver.solve(List.of());

        assertThat(result.maxProfit()).isEqualTo(0);
        assertThat(result.counts()).containsExactly(0);
    }

    @Test
    void partialProfitablePrefix() {
        // Only the first box is profitable; buying two boxes would lose money.
        FlutResult result = solver.solve(List.of(new Pile(List.of(1, 100))));

        assertThat(result.maxProfit()).isEqualTo(9);
        assertThat(result.counts()).containsExactly(1);
    }

    /**
     * Cross-checks the optimised solver against a naive, non-pruned dynamic-programming reference
     * implementation on many random inputs. This verifies the independence reasoning and the
     * ten-smallest pruning are both correct.
     */
    @Test
    void matchesBruteForceOnRandomCases() {
        Random random = new Random(42);
        for (int trial = 0; trial < 500; trial++) {
            List<Pile> piles = randomPiles(random);
            FlutResult expected = bruteForce(piles);
            FlutResult actual = solver.solve(piles);

            assertThat(actual.maxProfit())
                    .as("max profit for trial %d", trial)
                    .isEqualTo(expected.maxProfit());
            assertThat(actual.counts())
                    .as("counts for trial %d", trial)
                    .isEqualTo(expected.counts());
        }
    }

    private List<Pile> randomPiles(Random random) {
        int piles = random.nextInt(4) + 1;
        List<Pile> result = new ArrayList<>();
        for (int i = 0; i < piles; i++) {
            int boxes = random.nextInt(8) + 1;
            List<Integer> prices = new ArrayList<>();
            for (int j = 0; j < boxes; j++) {
                prices.add(random.nextInt(20) + 1);
            }
            result.add(new Pile(prices));
        }
        return result;
    }

    private FlutResult bruteForce(List<Pile> piles) {
        Map<Integer, Long> dp = new HashMap<>();
        dp.put(0, 0L);
        for (Pile pile : piles) {
            long[] profits = prefixProfits(pile);
            Map<Integer, Long> next = new HashMap<>();
            for (Map.Entry<Integer, Long> entry : dp.entrySet()) {
                for (int k = 0; k < profits.length; k++) {
                    int count = entry.getKey() + k;
                    long profit = entry.getValue() + profits[k];
                    next.merge(count, profit, Math::max);
                }
            }
            dp = next;
        }

        long maxProfit = dp.values().stream().mapToLong(Long::longValue).max().orElse(0);
        List<Integer> counts = dp.entrySet().stream()
                .filter(entry -> entry.getValue() == maxProfit)
                .map(Map.Entry::getKey)
                .sorted()
                .limit(10)
                .toList();
        return new FlutResult(maxProfit, counts);
    }

    private long[] prefixProfits(Pile pile) {
        List<Integer> prices = pile.prices();
        long[] profits = new long[prices.size() + 1];
        long cost = 0;
        for (int i = 0; i < prices.size(); i++) {
            cost += prices.get(i);
            profits[i + 1] = (long) FlutSolver.SELL_PRICE * (i + 1) - cost;
        }
        return profits;
    }
}
