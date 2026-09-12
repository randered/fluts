package com.randered.flatexdegirotask.service;

import com.randered.flatexdegirotask.domain.FlutResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutputFormatter {

    public String format(List<FlutResult> results) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < results.size(); i++) {
            if (i > 0) {
                sb.append('\n');
            }
            appendCase(sb, i + 1, results.get(i));
        }
        return sb.toString();
    }

    private void appendCase(StringBuilder sb, int caseNumber, FlutResult result) {
        sb.append("schuurs ").append(caseNumber).append('\n');
        sb.append("Maximum profit is ").append(result.maxProfit()).append(".\n");
        sb.append("Number of fluts to buy:");
        for (int count : result.counts()) {
            sb.append(' ').append(count);
        }
        sb.append('\n');
    }
}
