import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * NPStack program that takes a file of boxes and uses simulated annealing to find the tallest stack it can
 *
 * @author Cameron Salisbury (1293897)
 */
public class NPStack {
    private static Random rnd = new Random();

    public static void main(String[] args) {
        // Checks for 2 arguments
        if (args.length != 2) {
            System.out.println("Usage: java NPStack2 [file] [maxConsider]");
            return;
        }

        // Checks maxConsider argument
        int considerations;
        try {
            considerations = Integer.parseInt(args[1]);
            if (considerations < 1) {
                System.err.println("Second argument must be a positive number");
                return;
            }
        } catch (NumberFormatException e) {
            System.err.println("Second argument must be a positive number");
            return;
        }

        // Checks path argument
        if (args[0] == null) {
            System.err.println("First argument must not be null");
            return;
        }

        // Runs program
        try {
            BoxStack stack = run(args[0], considerations);
            System.out.println(stack.toString());
        } catch (FileNotFoundException e) {
            System.err.println("File \"" + args[0] + "\" cannot be found");
        }
    }

    static BoxStack run(String fileName, int maxConsiderations) throws FileNotFoundException {
        // Create list of boxes
        BufferedReader file = new BufferedReader(new FileReader(new File(fileName)));
        List<Box> boxes = makeList(file);

        int boxCount = boxes.size();

        // Check at least one box was found in the file
        if (boxes.size() == 0) {
            System.err.println("No valid boxes in file \"" + fileName + "\"");
            return new BoxStack();
        }

        // Calculate number of random stack to consider for starting
        // (5% of total considerations or 10, whichever is smaller)
        int limit = Math.min((int) Math.ceil(maxConsiderations * 0.05), 10);


        BoxStack stack = null;
        List<Box> unusedBoxes = null;
        for (int i = 0; i < limit; i++) {
            // Duplicate box list and randomise order
            List<Box> candidateUnusedBoxes = new ArrayList<>(boxes);
            Collections.shuffle(candidateUnusedBoxes);

            // Generate stack
            BoxStack candidateStack = makeInitialStack(candidateUnusedBoxes);

            // Check if better than current best
            if (stack == null || candidateStack.height() > stack.height()) {
                stack = candidateStack;
                unusedBoxes = candidateUnusedBoxes;
            }
        }

        // Remove number of random starts from max considerations
        maxConsiderations -= limit;

        // For the number of considerations allowed
        for (int nthConsider = 0; nthConsider < maxConsiderations; nthConsider++) {
            // Calculate the number of changes
            int numberOfChanges = changesToMake(nthConsider, maxConsiderations, boxCount);

            // Clone current stack and unused boxes
            BoxStack newStack = new BoxStack(stack);
            List<Box> newUnusedBoxes = new ArrayList<>(unusedBoxes);

            // For the number of changes, either insert, replace or remove a box with weightings
            // 40%, 40% and 10% respectively
            for (int i = 0; i < numberOfChanges; i++) {
                float rndFloat = rnd.nextFloat();
                if (rndFloat < 0.4) {
                    swapBox(newStack, newUnusedBoxes);
                } else if (rndFloat < 0.9) {
                    insertBox(newStack, newUnusedBoxes);
                } else {
                    removeBox(newStack, newUnusedBoxes);
                }
            }

            // If new stack is higher, replace old stack
            if (newStack.height() >= stack.height()) {
                stack = newStack;
                unusedBoxes = newUnusedBoxes;
            }
        }

        // Before finishing, check stack is valid, this should never fail but who knows
        if (!stack.validateStack())
            throw new RuntimeException("Final stack is invalid");

        return stack;
    }

    private static List<Box> makeList(BufferedReader file) {
        List<Box> output = new ArrayList<>();
        int[] index = new int[]{0};

        // Applies a bunch of operations on the lines from the file to create boxes and put them in the box list
        file.lines()
                .map(line -> line.split(" ")) // Split lines by space
                .filter(array -> array.length == 3) // Check there are 3 elements
                .map(array -> { // Try convert those elements to ints
                    try {
                        int x = Integer.parseInt(array[0]);
                        int y = Integer.parseInt(array[1]);
                        int z = Integer.parseInt(array[2]);
                        if (x < 1 || y < 1 || z < 1)
                            return null;
                        return new int[]{x, y, z};
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull) // Remove the elements that were invalid
                .map(array -> { // Create boxes
                    index[0]++;
                    int heightIndex = rnd.nextInt(3);
                    return new Box(index[0], array[(heightIndex + 1) % 3], array[(heightIndex + 2) % 3], array[heightIndex]);
                })
                .forEach(output::add); // Add boxes to box list

        return output;
    }

    private static BoxStack makeInitialStack(List<Box> unusedBoxes) {
        BoxStack output = new BoxStack();

        int index = 0;
        while (index <= output.size()) {
            int boxToRemove = -1;


            // For every box
            for (int i = 0; i < unusedBoxes.size(); i++) {
                Box box = unusedBoxes.get(i);
                Box below = output.get(index - 1);
                Box above = output.get(index);

                // See of that box can be inserted at the current index
                if (box.getWidth() < below.getWidth() && box.getDepth() < below.getDepth() && box.getWidth() > above.getWidth() && box.getDepth() > above.getDepth()) {
                    output.add(index, box);
                    boxToRemove = i;
                    break;
                }
            }

            // If a box was inserted, remove it from list of unused boxes
            if (boxToRemove != -1) {
                unusedBoxes.remove(boxToRemove);
            }
            // If no box was inserted, increase the index
            else {
                index++;
            }
        }

        return output;
    }

    // Calculates the number of changes to make based the square of the proportion of total considerations already made
    // scaled by double the base 2 log of the total considerations to make
    private static int changesToMake(int nthConsideration, int maxConsiderations, int boxNumber) {
        double d = (double) (maxConsiderations - nthConsideration + 1) / (double) maxConsiderations;
        double square = d * d;
        return (int) Math.ceil((square * intLog2(boxNumber) * 2));
    }

    // Swap a random unused box with another box in the stack
    private static void swapBox(BoxStack stack, List<Box> unusedBoxes) {
        // Check there is at least 1 unused box
        if (unusedBoxes.size() == 0) return;

        // Select a random unused box and generate all its rotations
        int boxIndex = rnd.nextInt(unusedBoxes.size());
        Box boxToSwapIn = unusedBoxes.get(boxIndex);
        List<Box> boxes = boxToSwapIn.makeRotations();

        // Going through the stack
        for (int i = 0; i < stack.size(); i++) {
            // Get the box above and below
            Box below = stack.get(i - 1);
            Box above = stack.get(i + 1);

            // For each rotation
            for (int j = 0; j < boxes.size(); j++) {
                Box box = boxes.get(j);

                // Check if the box below is larger
                if (box.getWidth() < below.getWidth() && box.getDepth() < below.getDepth()) {
                    // Check if box above is smaller
                    if (box.getWidth() > above.getWidth() && box.getDepth() > above.getDepth()) {
                        // Swap the boxes in the stack and in the unused box list
                        unusedBoxes.set(boxIndex, stack.get(i));
                        stack.set(i, box);
                        return;
                    }
                }
                // If the box below is not larger, this rotation isn't going to be able to fit up any
                // higher so just remove it
                else {
                    boxes.remove(j);
                    j--;
                }
            }

            // If out of rotations, break
            if (boxes.size() == 0)
                break;
        }
    }

    // Insert a random unused box into the stack
    private static void insertBox(BoxStack stack, List<Box> unusedBoxes) {
        // Check there is at least 1 unused box
        if (unusedBoxes.size() == 0) return;

        // Select a random unused box and generate all its rotations
        int boxIndex = rnd.nextInt(unusedBoxes.size());
        Box boxToSwapIn = unusedBoxes.get(boxIndex);
        List<Box> boxes = boxToSwapIn.makeRotations();

        // Going through the stack
        for (int i = 0; i <= stack.size(); i++) {
            // Get the box above and below
            Box below = stack.get(i - 1);
            Box above = stack.get(i);

            // For each rotation
            for (int j = 0; j < boxes.size(); j++) {
                Box box = boxes.get(j);

                // Check if the box below is larger
                if (box.getWidth() < below.getWidth() && box.getDepth() < below.getDepth()) {
                    // Check if box above is smaller
                    if (box.getWidth() > above.getWidth() && box.getDepth() > above.getDepth()) {
                        // Add the box to the stack and remove it from the unused boxes list
                        stack.add(i, box);
                        unusedBoxes.remove(boxIndex);
                        return;
                    }
                }
                // If the box below is not larger, this rotation isn't going to be able to fit up any
                // higher so just remove it
                else {
                    boxes.remove(j);
                    j--;
                }
            }

            // If out of rotations, break
            if (boxes.size() == 0)
                break;
        }
    }

    // Remove a random box from the stack
    private static void removeBox(BoxStack stack, List<Box> unusedBoxes) {
        if (stack.size() == 0) return;

        Box removedBox = stack.remove(rnd.nextInt(stack.size()));
        unusedBoxes.add(removedBox);
    }

    // Returns base 2 log of number rounded up
    private static int intLog2(int i) {
        // Invert negatives
        if (i < 0)
            i = -i;

        int log = 0;
        while (i != 0) {
            log++;
            i >>= 1;
        }

        return log;
    }
}
