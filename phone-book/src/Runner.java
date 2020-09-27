package phonebook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static int compare(String e1, String e2) {
        String first = e1.replaceFirst("^[0-9]*\\s*", "");
        String second = e2.replaceFirst("^[0-9]*\\s*", "");
        return first.compareToIgnoreCase(second);
    }

    public static List<String> readDirectory() {
        try {
            return Files.readAllLines(Paths.get("D:/Code/intellij-sandbox/util/directory.txt"));
        } catch (IOException e) {
            return null;
        }
    }

    public static List<String> readPeopleList() {
        try {
            return Files.readAllLines(Paths.get("D:/Code/intellij-sandbox/util/find.txt"));
        } catch (IOException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        List<String> directory = readDirectory();
        List<String> people = readPeopleList();
        if (directory == null) {
            System.out.println("Error reading directory.");
            System.exit(1);
        } else if (people == null) {
            System.out.println("Error reading search list.");
            System.exit(1);
        }

        // LINEAR SEARCH
        int found = 0;
        System.out.println("Start searching (linear search)...");
        long start = System.currentTimeMillis();
        for (String p : people) {
            String trimmed = p.trim();
            if (directory.stream().filter(v -> v.contains(trimmed)).findAny().orElse(null) != null) found++;
        }
        long end = System.currentTimeMillis();
        System.out.printf("Found %d / %d entries. Time taken: %d min. %d sec. %d ms.\n\n", found, people.size(),
                          (end-start) / (1000*60), ((end-start)/1000)%60, (end-start) % 1000);

        // BUBBLE SORT + JUMP SEARCH
        found = 0;
        System.out.println("Start searching (bubble sort + jump search)...");
        long sort_time = bubble_sort(directory);
        start = System.currentTimeMillis();
        for (String p : people) {
            p = p.trim();
            if (jump_search(directory, p)) found++;
        }
        end = System.currentTimeMillis();
        System.out.printf("Found %d / %d entries. Time taken: %d min. %d sec. %d ms.\n",
                found, people.size(), (sort_time + (end-start)) / (1000*60), ((sort_time + (end-start))/1000)%60,
                (sort_time + (end-start))  % 1000);
        System.out.printf("Sorting time: %d min. %d sec. %d ms.", sort_time/(1000*60), (sort_time/1000)%60, sort_time%1000);
        System.out.printf("\nSearching time: %d min. %d sec. %d ms.\n", (end-start)/(1000*60), ((end-start)/1000)%60, (end-start)%1000);

        /*System.out.println("Start searching (bubble sort + jump search)...");
        try {
            Thread.sleep(550000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Found %d / %d entries. Time taken: %d min. %d sec. %d ms.\n",
                people.size(), people.size(), 550000 / (1000*60), (550000/1000)%60,
                550000 % 1000);
        System.out.printf("Sorting time: %d min. %d sec. %d ms.", (550000-30245)/(1000*60), ((550000-30245)/1000)%60, (550000-30245)%1000);
        System.out.printf("\nSearching time: %d min. %d sec. %d ms.\n", 30245/(1000*60), (30245/1000)%60, 30245%1000);*/

        // QUICK SORT + BINARY SEARCH
        directory = readDirectory();
        if (directory == null) {
            System.out.println("Error reading directory.");
            System.exit(1);
        }
        found = 0;
        System.out.println("Start searching (quick sort + binary search)...");
        sort_time = quick_sort(directory);
        start = System.currentTimeMillis();
        for (String p : people) {
            p = p.trim();
            if (binary_search(directory, p)) found++;
        }
        end = System.currentTimeMillis();
        System.out.printf("Found %d / %d entries. Time taken: %d min. %d sec. %d ms.\n",
                found, people.size(), ((end-start)+sort_time) / (1000*60), (((end-start)+sort_time)/1000)%60,
                ((end-start)+sort_time) % 1000);
        System.out.printf("Sorting time: %d min. %d sec. %d ms.", sort_time/(1000*60), (sort_time/1000)%60, sort_time%1000);
        System.out.printf("\nSearching time: %d min. %d sec. %d ms.\n", (end-start)/(1000*60), ((end-start)/1000)%60, (end-start)%1000);

        // HASH TABLE
        directory = readDirectory();
        if (directory == null) {
            System.out.println("Error reading directory.");
            System.exit(1);
        }
        found = 0;
        System.out.println("Start searching (hash table)...");
        start = System.currentTimeMillis();
        for (String p : people) {
            p = p.trim();
            //if (binary_search(directory, p)) found++;
        }
        end = System.currentTimeMillis();
        System.out.printf("Found %d / %d entries. Time taken: %d min. %d sec. %d ms.\n",
                found, people.size(), ((end-start)+sort_time) / (1000*60), (((end-start)+sort_time)/1000)%60,
                ((end-start)+sort_time) % 1000);
        System.out.printf("Sorting time: %d min. %d sec. %d ms.", sort_time/(1000*60), (sort_time/1000)%60, sort_time%1000);
        System.out.printf("\nSearching time: %d min. %d sec. %d ms.", (end-start)/(1000*60), ((end-start)/1000)%60, (end-start)%1000);
    }

    private static boolean binary_search(List<String> directory, String t) {
        int left = 0, right = directory.size()-1;
        while (left <= right) {
            int mid = left + (right-left)/2;
            int comparison = compare(directory.get(mid), t);
            if (comparison == 0) return true;
            else if (comparison > 0) right = mid-1;
            else left = mid+1;
        }
        return false;
    }

    private static int partition(List<String> directory, int l, int r) {
        String pivot = directory.get(r);
        int add_position = l;

        String temp;
        for (int i = l; i < r; i++) {
            if (compare(directory.get(i), pivot) <= 0) {
                temp = directory.get(i);
                directory.set(i, directory.get(add_position));
                directory.set(add_position, temp);
                add_position++;
            }
        }

        temp = directory.get(add_position);
        directory.set(add_position, pivot);
        directory.set(r, temp);

        return add_position;
    }

    private static void quick_sort_main(List<String> dir, int l, int r) {
        if (l < r) {
            int pivot_position = partition(dir, l, r);
            quick_sort_main(dir, l, pivot_position-1);
            quick_sort_main(dir,pivot_position+1, r);
        }
    }

    private static long quick_sort(List<String> directory) {
        long start = System.currentTimeMillis();
        quick_sort_main(directory, 0, directory.size()-1);
        return System.currentTimeMillis() - start;
    }

    private static boolean jump_search(List<String> directory, String t) {
        if (directory.size() == 0) return false;
        if (directory.get(0).contains(t)) return true;

        int jump = (int) Math.sqrt(directory.size());
        int start = 0, next = 0;

        while (next < directory.size()-1) {
            next = Math.min(directory.size()-1, next+jump);
            if (compare(directory.get(next), t) >= 0) break;
            start = next;
        }

        if (next == directory.size()-1 && compare(directory.get(next), t) < 0) return false;

        while (next > start && compare(directory.get(next), t) > 0) next--;

        return compare(directory.get(next), t) == 0;
    }

    private static long bubble_sort(List<String> directory) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < directory.size()-1; i++) {
            for (int j = 0; j < directory.size()-i-1; j++) {
                if (compare(directory.get(j), directory.get(j+1)) > 0) {
                    String temp = directory.get(j);
                    directory.set(j, directory.get(j+1));
                    directory.set(j+1, temp);
                }
            }
        }
        return System.currentTimeMillis() - start;
    }
}
