package util;

import java.util.ArrayList;
import model.Displayable;

public class TableFormatter {

    private static String generateLine(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append('-');
        }
        return sb.toString();
    }

    public static void printTable(ArrayList<? extends Displayable> items) {
        if (items == null || items.isEmpty()) {
            System.out.println("  (No records found)");
            return;
        }

        String header = items.get(0).getDisplayHeader();
        String line = generateLine(header.length());

        System.out.println(line);
        System.out.println(header);
        System.out.println(line);

        for (int i = 0; i < items.size(); i++) {
            System.out.println(items.get(i).getDisplayRow());
        }
        System.out.println(line);
    }

    public static void printGrid(String[] headers, String[][] rows) {
        if (headers == null || rows == null || headers.length == 0) {
            System.out.println("  (Empty grid)");
            return;
        }

        int colCount = headers.length;
        int[] colWidths = new int[colCount];

        for (int i = 0; i < colCount; i++) {
            colWidths[i] = headers[i].length();
        }

        for (int r = 0; r < rows.length; r++) {
            for (int c = 0; c < colCount && c < rows[r].length; c++) {
                if (rows[r][c] != null && rows[r][c].length() > colWidths[c]) {
                    colWidths[c] = rows[r][c].length();
                }
            }
        }

        StringBuffer headerBuffer = new StringBuffer();
        StringBuffer dividerBuffer = new StringBuffer();

        for (int i = 0; i < colCount; i++) {
            headerBuffer.append(String.format(" %-" + colWidths[i] + "s |", headers[i]));
            for (int k = 0; k < colWidths[i] + 3; k++) {
                dividerBuffer.append('-');
            }
        }

        System.out.println(dividerBuffer.toString());
        System.out.println(headerBuffer.toString());
        System.out.println(dividerBuffer.toString());

        for (int r = 0; r < rows.length; r++) {
            StringBuilder rowBuilder = new StringBuilder();
            for (int c = 0; c < colCount; c++) {
                String val = (c < rows[r].length && rows[r][c] != null) ? rows[r][c] : "";
                rowBuilder.append(String.format(" %-" + colWidths[c] + "s |", val));
            }
            System.out.println(rowBuilder.toString());
        }
        System.out.println(dividerBuffer.toString());
    }

    public static void printDetails(String sectionTitle, String[] labels, String[] values) {
        System.out.println();
        System.out.println("=== " + sectionTitle + " ===");
        int maxLabelLen = 0;
        for (int i = 0; i < labels.length; i++) {
            if (labels[i].length() > maxLabelLen) {
                maxLabelLen = labels[i].length();
            }
        }

        for (int i = 0; i < labels.length; i++) {
            String val = (i < values.length && values[i] != null) ? values[i] : "N/A";
            System.out.println(String.format("  %-" + (maxLabelLen + 2) + "s : %s", labels[i], val));
        }
        System.out.println();
    }
}
