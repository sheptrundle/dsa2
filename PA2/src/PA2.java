// PA2.java skeleton code
import java.util.*;
import java.io.*;

// Shep Trundle
// dvf5rd
// DSA2 - PA2

/*
 Sources:
 Geeks for Geeks - Skyline Problem: https://www.geeksforgeeks.org/dsa/the-skyline-problem-using-divide-and-conquer-algorithm/
 Design Gurus - Skyline Problem: https://www.designgurus.io/answers/detail/218-the-skyline-problem-2prob8sky
 */


public class PA2 {

    // used for silhouette
    static class Silhouette {
        int startTime, height;
        String name;

        Silhouette(int startTime, int height, String name) {
            this.startTime = startTime;
            this.height = height;
            this.name = name;
        }

        public String toString() {
            return "(" + startTime + "," + height + ")";
        }
    }

    // object for each usage period
    static class Period {
        int left, right, height;
        String deviceName;

        Period(String deviceName, int left, int right, int height) {
            this.deviceName = deviceName;
            this.left = left;
            this.right = right;
            this.height = height;
        }

        public String toString(){
            return (deviceName + " " + left + " " + right + " " + height);
        }

        // Check if a period is active during a specific time
        public boolean occursDuring(int time) {
            return (time >= left && time <= right);
        }

    }

    public static List<Silhouette> createSilhouette(List<Period> periods) {
        // Null sublist
        if (periods.isEmpty()) return new ArrayList<>();

        // Base case
        if (periods.size() == 1) {
            Period p = periods.get(0);
            // Create list and add start/end for period
            List<Silhouette> res = new ArrayList<>();
            res.add(new Silhouette(p.left, p.height, p.deviceName));
            res.add(new Silhouette(p.right, 0, p.deviceName));
            return res;
        }

        int mid = periods.size() / 2;

        List<Period> leftList = periods.subList(0, mid);
        List<Period> rightList = periods.subList(mid, periods.size());

        List<Silhouette> leftSol = createSilhouette(leftList);
        List<Silhouette> rightSol = createSilhouette(rightList);

        return merge(leftSol, rightSol);

    }

    private static List<Silhouette> merge(List<Silhouette> left, List<Silhouette> right) {
        List<Silhouette> result = new ArrayList<>();
        int i = 0, j = 0;
        int heightLeft = 0, heightRight = 0, prevHeight = 0;
        String currentName = "";

        while (i < left.size() && j < right.size()) {
            Silhouette l = left.get(i);
            Silhouette r = right.get(j);

            int time;
            // Left starts first
            if (l.startTime < r.startTime) {
                time = l.startTime;
                heightLeft = l.height;
                i++;
            }
            // Right starts first
            else if (l.startTime > r.startTime) {
                time = r.startTime;
                heightRight = r.height;
                j++;
            }
            // Same time
            else {
                time = l.startTime;
                heightLeft = l.height;
                heightRight = r.height;
                i++;
                j++;
            }

            // Find higher height and apply
            int heightMax;
            String nameMax;
            // Left is higher
            if (heightLeft > heightRight) {
                heightMax = heightLeft;
                nameMax = l.name;
            }
            // Right is higher
            else if (heightRight > heightLeft) {
                heightMax = heightRight;
                nameMax = r.name;
            }
            // Same height case
            else {
                heightMax = heightLeft;
                if (currentName.isEmpty()) {
                    nameMax = l.name;
                } else {
                    nameMax = currentName;
                }
            }

            if (heightMax != prevHeight) {
                result.add(new Silhouette(time, heightMax, nameMax));
                prevHeight = heightMax;
                currentName = nameMax;
            }
        }

        while (i < left.size()) {
            Silhouette s = left.get(i++);
            if (s.height != prevHeight) {
                result.add(s);
                prevHeight = s.height;
            }
        }
        while (j < right.size()) {
            Silhouette s = right.get(j++);
            if (s.height != prevHeight) {
                result.add(s);
                prevHeight = s.height;
            }
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        List<String> names = new ArrayList<>();
        List<Period> periods = new ArrayList<>();

        //Read in d and c
        int d = scanner.nextInt();
        int u = scanner.nextInt();
        scanner.nextLine();

        /* Read in the names */
        for(int i=0; i<d; i++){
            names.add(scanner.nextLine());
        }

        // Read input
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            if (parts.length != 4) continue;

            String name = parts[0];
            int left = Integer.parseInt(parts[1]);
            int right = Integer.parseInt(parts[2]);
            int height = Integer.parseInt(parts[3]);

            periods.add(new Period(name, left, right, height));
        }

        // Begin finding max usage period
        List<Silhouette> silhouettes = createSilhouette(periods);
        // Print all periods by max usage
        for (Silhouette silhouette : silhouettes) {
            System.out.print(silhouette);
            System.out.print(" ");
        }

        // Get total minutes as max usage for all periods
        Map<String, Integer> totalMinutes = new HashMap<>();
        Map<String, Integer> timeFinished = new HashMap<>();
        for (int i = 0; i < silhouettes.size() - 1; i++) {
            Silhouette current = silhouettes.get(i);
            Silhouette next = silhouettes.get(i + 1);
            if (current.height > 0) {
                Integer duration = next.startTime - current.startTime;
                totalMinutes.put(current.name, totalMinutes.getOrDefault(current.name, 0) + duration);
                // Tie Breaker
                timeFinished.put(current.name, next.startTime);
            }
        }

        // Find shortest and longest duration
        String longestName = "";
        String shortestName = "";
        Integer longestDuration = -1;
        int shortestDuration = Integer.MAX_VALUE;
        for (Map.Entry<String, Integer> entry : totalMinutes.entrySet()) {
            String name = entry.getKey();
            Integer duration = entry.getValue();
            // Check longest
            if (duration > longestDuration) {
                longestName = name;
                longestDuration = duration;
            }
            // Tie-breaker for longest
            else if (duration == longestDuration) {
                if (timeFinished.get(name) < timeFinished.get(longestName)) {
                    longestName = name;
                }
            }
            // Check shortest
            if (duration < shortestDuration) {
                shortestName = name;
                shortestDuration = duration;
            }
            // Tie-breaker for shortest
            else if (duration == shortestDuration) {
                if (timeFinished.get(name) < timeFinished.get(shortestName)) {
                    shortestName = name;
                }
            }
        }
        System.out.println();
        System.out.println(longestName + " " + longestDuration);
        System.out.println(shortestName + " " + shortestDuration);
    }
}