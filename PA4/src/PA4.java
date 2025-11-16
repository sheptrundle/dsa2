// Shep Trundle
// dvf5rd
// DSA2 - PA4

// Sources: LeetCode #53 https://leetcode.com/problems/maximum-subarray/description/
// Remembered doing this LeetCode which uses similar solution to this UVA/Tech problem

import java.util.Scanner;

public class PA4 {
    public static void main(String[] args) {
        // Read input
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        int numFans = line.length();

        // Turn fans into int values, +1 or -1
        int[] val = new int[numFans];
        for (int i = 0; i < numFans; i++) {
            val[i] = fanValue(line.charAt(i));
        }

        int[] posDP = new int[numFans];
        int[] negDP = new int[numFans];
        int posStart = 0;
        int negStart = 0;

        // Base cases, line ends at index 0
        posDP[0] = val[0];
        negDP[0] = val[0];

        for (int i = 1; i < numFans; i++) {
            posDP[i] = Math.max(val[i], posDP[i - 1] + val[i]);
            negDP[i] = Math.min(val[i], negDP[i - 1] + val[i]);
        }
    }

    // Helper that just turns each fan into an int value
    public static int fanValue(char fan) {
        if (fan == 'm') return -1;
        if (fan == 'B') return 1;
        else throw new IllegalArgumentException("String error. Invalid fan character: " + fan);
    }
}
