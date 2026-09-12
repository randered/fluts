package com.randered.flatexdegirotask.service;

import com.randered.flatexdegirotask.domain.FlutResult;
import com.randered.flatexdegirotask.domain.Pile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Computes the maximum profit a merchant can achieve when buying fluts.
 *
 * <p>Each pile can only be bought as a prefix (top to bottom). Because the piles are independent, the
 * maximum total profit is the sum of the per-pile maximum prefix profit. The total number of fluts that
 * achieve this profit is the Minkowski sum of the per-pile optimal-prefix count sets.
 */
@Service
public class FlutSolver {

    public static final int SELL_PRICE = 10;
    public static final int MAX_REPORTED_COUNTS = 10;

    public FlutResult solve(List<Pile> piles) {
        long totalMaxProfit = 0;
        List<int[]> optimalCountSets = new ArrayList<>(piles.size());

        for (Pile pile : piles) {
            long[] profits = prefixProfits(pile);
            long max = max(profits);
            totalMaxProfit += max;
            optimalCountSets.add(countsAchieving(profits, max));
        }

        List<Integer> counts = smallestAchievableCounts(optimalCountSets);
        return new FlutResult(totalMaxProfit, counts);
    }

    private long[] prefixProfits(Pile pile) {
        List<Integer> prices = pile.prices();
        long[] profits = new long[prices.size() + 1];
        long cost = 0;
        for (int i = 0; i < prices.size(); i++) {
            cost += prices.get(i);
            profits[i + 1] = (long) SELL_PRICE * (i + 1) - cost;
        }
        return profits;
    }

    private long max(long[] values) {
        long result = values[0];
        for (long value : values) {
            result = Math.max(result, value);
        }
        return result;
    }

    private int[] countsAchieving(long[] profits, long max) {
        int size = 0;
        for (long profit : profits) {
            if (profit == max) {
                size++;
            }
        }
        int[] counts = new int[size];
        int index = 0;
        for (int i = 0; i < profits.length; i++) {
            if (profits[i] == max) {
                counts[index++] = i;
            }
        }
        return counts;
    }

    /**
     * Only the ten smallest values of the Minkowski sum are needed. Since every count added afterwards
     * is non-negative, the smallest values can only be produced by the smallest values of each
     * intermediate set, so the intermediate sets are pruned to that size after each step.
     */
    private List<Integer> smallestAchievableCounts(List<int[]> countSets) {
        TreeSet<Integer> current = new TreeSet<>();
        current.add(0);

        for (int[] counts : countSets) {
            TreeSet<Integer> next = new TreeSet<>();
            for (int base : current) {
                for (int count : counts) {
                    next.add(base + count);
                }
            }
            current = keepSmallest(next);
        }

        return new ArrayList<>(current);
    }

    private TreeSet<Integer> keepSmallest(TreeSet<Integer> values) {
        TreeSet<Integer> result = new TreeSet<>();
        int kept = 0;
        for (int value : values) {
            if (kept >= MAX_REPORTED_COUNTS) {
                break;
            }
            result.add(value);
            kept++;
        }
        return result;
    }
}
