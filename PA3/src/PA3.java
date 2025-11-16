import java.util.*;

// Shep Trundle
// dvf5rd
// DSA2 - PA3

public class PA3 {

    // Class for each room
    public static class Room {
        private final int initial;
        private final int renovated;

        Room(int initial, int renovated) {
            this.initial = initial;
            this.renovated = renovated;
        }

        public int getChangeInSize() {return renovated - initial;}
        public int getInitial() {return initial;}
    }

    // Class so I can modify primitive types during renovation process
    public static class RenovationProcess {
        int trailerSize;
        int availableSpace;

        RenovationProcess() {
            this.trailerSize = 0;
            this.availableSpace = 0;
        }

        public int getTrailerSize() {return trailerSize;}
        public int getAvailableSpace() {return availableSpace;}
        public void setTrailerSize(int trailerSize) {this.trailerSize = trailerSize;}
        public void setAvailableSpace(int availableSpace) {this.availableSpace = availableSpace;}
    }

    // Greedy renovation algorithm
    public static void renovate(List<Room> rooms, RenovationProcess process) {
        // Iterate over rooms
        for (Room room : rooms) {
            // Check if room is too large to fit in current available space
            if (room.getInitial() > process.getAvailableSpace()) {
                // Check if extra kids don't into current trailer size. If so, update trailer size
                if (room.getInitial() - process.getAvailableSpace() > process.getTrailerSize()) {
                    process.setTrailerSize(room.getInitial() - process.getAvailableSpace());
                }
            }
            // Update available space based on current rooms new renovation
            process.setAvailableSpace(process.getAvailableSpace() + room.getChangeInSize());
        }
    }

    public static void main(String[] args) throws Exception {
        // Read input
        Scanner sc = new Scanner(System.in);
        int t = sc.nextInt();
        // Run for each test case
        for (int test = 0; test < t; test++) {
            List<Integer> oldCaps = new ArrayList<>();
            List<Integer> newCaps = new ArrayList<>();

            // Populate lists
            int roomCount = sc.nextInt();
            for (int i = 0; i < roomCount; i++) {
                int initial = sc.nextInt();
                int renovated = sc.nextInt();
                oldCaps.add(initial);
                newCaps.add(renovated);
            }

            // Populate all 3 arrays (negative, zero, and positive change)
            List<Room> posRooms = new ArrayList<>();
            List<Room> eqRooms = new ArrayList<>();
            List<Room> negRooms = new ArrayList<>();
            for (int i = 0; i < roomCount; i++) {
                // Positive room
                if (newCaps.get(i) - oldCaps.get(i) > 0) {
                    posRooms.add(new Room(oldCaps.get(i), newCaps.get(i)));
                }
                // Equal room
                else if (newCaps.get(i) - oldCaps.get(i) == 0) {
                    eqRooms.add(new Room(oldCaps.get(i), newCaps.get(i)));
                }
                // Negative room
                else if (newCaps.get(i) - oldCaps.get(i) < 0) {
                    negRooms.add(new Room(oldCaps.get(i), newCaps.get(i)));
                } else {
                    throw new Exception("~Error adding room~ newCap = " + newCaps.get(i) + " | oldCap = " + oldCaps.get(i));
                }
            }
            // Sort posRooms from smallest to largest
            posRooms.sort(Comparator.comparingInt(Room::getInitial));
            // Sort negRooms from largest to smallest
            negRooms.sort(Comparator.comparingInt(Room::getInitial).reversed());

            // Check each rooms during renovation, making the greedy choice of pos>equal>neg change in size, then pick either smallest or largest
            // If kids in that room cant fit into available space, must put them in the trailer
            RenovationProcess process = new RenovationProcess();

            // Renovate all rooms in the order (pos --> eq --> neg)
            renovate(posRooms, process);
            renovate(eqRooms, process);
            renovate(negRooms, process);

            // Done renovating all rooms, print minimum trailer size needed
            System.out.println(process.getTrailerSize());
        }
        sc.close();
    }
}
