import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.fail;

class NPStack2Test {

    @Test
    void run() {
        String[] files = new String[]{
                "rand0010.boxes",
                "rand0020.boxes",
                "rand0050.boxes",
                "rand0100.boxes",
                "rand0500.boxes",
                "rand1000.boxes"
        };
        int[] considerations = new int[]{
                25,
                50,
                100,
                500,
                1000,
                5000
        };

        try {

            long time = System.currentTimeMillis();
            int times = 50;
            for (int i = 0; i < files.length; i++) {
                int sum = 0;
                int max = Integer.MIN_VALUE;
                int min = Integer.MAX_VALUE;
                for (int j = 0; j < times; j++) {
                    int height = NPStack2.run(files[i], considerations[i]).height();
                    sum += height;
                    if (height > max)
                        max = height;
                    if (height < min)
                        min = height;
                }
                System.out.println("File: " + files[i] + ", Considerations: " + considerations[i] + ", Simulations: " + times);
                System.out.println("Max Height: " + max + "\nAverage Height: " + (sum / times) + "\nMin Height: " + min + "\nAverage Time (ms): " + (System.currentTimeMillis() - time) / times);
                System.out.println();
            }
        } catch (FileNotFoundException e) {
            fail("File \"" + files[0] + "\" not found");
        }
//        catch (IOException e) {
//            fail("Error reading from file \"" + file + "\" ");
//        }
    }
}