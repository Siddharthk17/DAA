//Name : Siddharth Kakade
//PRN : 123B1F040
//Date : 7-7-25
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

// OUTPUT
// Sorting 1000000 records using Merge Sort...
// Sorting completed in 155 ms
// First 10 sorted results:
// 1000,2018-01-01T00:04:00.000000,Female,39,Single,Oklahoma,Platinum,Unemployment,Card,0,1557.5
// 1001,2018-01-01T00:06:00.000000,Male,34,Married,Hawaii,Basic,workers,PayPal,1,153.55
// 1002,2018-01-01T00:14:00.000000,Female,53,Married,Iowa,Basic,self-employed,PayPal,1,2640.96
// 1003,2018-01-01T00:23:00.000000,Male,33,Married,Wisconsin,Basic,self-employed,Card,1,293.58
// 1004,2018-01-01T00:25:00.000000,Female,36,Married,Texas,Platinum,Employees,Card,0,1608.01
// 1005,2018-01-01T00:27:00.000000,,48,Married,North Carolina,Silver,self-employed,Other,,1001.32
// 1006,2018-01-01T00:29:00.000000,Female,55,Single,Ohio,Silver,Unemployment,PayPal,0,1553.61
// 1007,2018-01-01T00:29:00.000000,Female,35,Married,Hawaii,Basic,workers,Other,1,1851.58
// 1008,2018-01-01T00:31:00.000000,Male,18,Married,Nevada,Basic,Employees,Card,1,
// 1009,2018-01-01T00:33:00.000000,Female,50,Married,North Dakota,Basic,workers,Card,1,541.23
