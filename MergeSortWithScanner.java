import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MergeSortWithScanner {

    // Order class to store timestamp and whole CSV line
    static class Order implements Comparable<Order> {
        long timestamp;  // epoch millis
        String rowData;  // Full CSV line

        Order(long timestamp, String rowData) {
            this.timestamp = timestamp;
            this.rowData = rowData;
        }

        @Override
        public int compareTo(Order o) {
            return Long.compare(this.timestamp, o.timestamp);
        }
    }

    // Recursive Merge Sort
    public static void mergeSort(Order[] arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    // Merge two sorted halves
    public static void merge(Order[] arr, int left, int mid, int right) {
        Order[] temp = new Order[right - left + 1];
        int i = left, j = mid + 1, k = 0;

        while (i <= mid && j <= right) {
            if (arr[i].compareTo(arr[j]) <= 0)
                temp[k++] = arr[i++];
            else
                temp[k++] = arr[j++];
        }
        while (i <= mid) temp[k++] = arr[i++];
        while (j <= right) temp[k++] = arr[j++];

        for (k = 0; k < temp.length; k++) {
            arr[left + k] = temp[k];
        }
    }

    // Convert timestamp string to epoch milliseconds
    public static long parseTimestamp(String ts) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
            LocalDateTime dateTime = LocalDateTime.parse(ts, formatter);
            return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        } catch (Exception e) {
            return 0; // fallback for bad timestamps
        }
    }

    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(new File("customer_spending_1M_2018_2025.csv"));

            // Skip header
            String header = sc.nextLine();

            Order[] orders = new Order[1_000_000];
            int index = 0;

            // Read rows
            while (sc.hasNextLine() && index < orders.length) {
                String line = sc.nextLine();
                String[] parts = line.split(",", -1);
                String timestampStr = parts[1]; // Timestamp in 2nd column
                long timestamp = parseTimestamp(timestampStr);

                orders[index++] = new Order(timestamp, line);
            }
            sc.close();

            System.out.println("Sorting " + index + " records using Merge Sort...");

            long startTime = System.currentTimeMillis();
            mergeSort(orders, 0, index - 1);
            long endTime = System.currentTimeMillis();

            System.out.println("Sorting completed in " + (endTime - startTime) + " ms");
            System.out.println("First 10 sorted results:");

            for (int i = 0; i < 10; i++) {
                System.out.println(orders[i].rowData);
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found! Please check file path!");
        }
    }
}
