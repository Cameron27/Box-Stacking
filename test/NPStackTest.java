import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.fail;

class NPStackTest {

    @Test
    void run() {
        String[] files = new String[]{
                "rand0010.boxes",
                "rand0050.boxes",
                "rand0100.boxes",
                "rand0500.boxes",
                //"rand1000.boxes"
        };
        int[] considerations = new int[]{
                50,
                100,
                500,
                1000,
                //5000
        };

        try {

            long time = System.currentTimeMillis();
            int sum = 0;
            int times = 50;
            for (int i = 0; i < files.length; i++) {
                for (int j = 0; j < times; j++) {
                    sum += NPStack.run(files[i], considerations[i]);
                }
                System.out.println("Height: " + (sum / times) + " Time: " + (System.currentTimeMillis() - time) / times + " File: " + files[i] + " Considerations: " + considerations[i]);
            }
        } catch (FileNotFoundException e) {
            fail("File \"" + files[0] + "\" not found");
        }
//        catch (IOException e) {
//            fail("Error reading from file \"" + file + "\" ");
//        }
    }
}