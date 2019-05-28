import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.fail;

class NPStackTest {

    @Test
    void run() {
        String file = "rand0500.boxes";
        int considerations = 100;

        try {

            long time = System.currentTimeMillis();
            int sum = 0;
            int times = 100;
            for (int i = 0; i < times; i++) {
                sum += NPStack.run(file, considerations);
            }
            System.out.println("Height: " + (sum / times) + " Time: " + (System.currentTimeMillis() - time));

        } catch (FileNotFoundException e) {
            fail("File \"" + file + "\" not found");
        }
//        catch (IOException e) {
//            fail("Error reading from file \"" + file + "\" ");
//        }
    }
}