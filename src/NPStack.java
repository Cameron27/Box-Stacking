import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("Duplicates")
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
        BoxList unusedBoxes = makeList(file);

        int boxCount = unusedBoxes.size();

        // Check at least one box was found in the file
        if (unusedBoxes.size() == 0) {
            System.err.println("No valid boxes in file \"" + fileName + "\"");
            return new BoxStack();
        }

        // Generate initial stack
        int limit = Math.min((int) Math.ceil(maxConsiderations * 0.05), 10);
        BoxStack bestStack = null;
        BoxList bestUnusedBoxes = null;
        for (int i = 0; i < limit; i++) {
            BoxList candidateUnusedBoxes = new BoxList(unusedBoxes);
            Collections.shuffle(candidateUnusedBoxes);
            BoxStack candidateStack = makeInitialStack(candidateUnusedBoxes);

            if (bestStack == null || candidateStack.height() > bestStack.height()) {
                bestStack = candidateStack;
                bestUnusedBoxes = candidateUnusedBoxes;
            }
        }

        BoxStack stack = bestStack;
        unusedBoxes = bestUnusedBoxes;

        // For the number of considerations allowed
        for (int nthConsider = limit; nthConsider < maxConsiderations; nthConsider++) {
            // Calculate the number of changes
            int numberOfChanges = changesToMake(nthConsider, maxConsiderations, boxCount);

            // Create clones
            BoxStack newStack = new BoxStack(stack);
            BoxList newUnusedBoxes = new BoxList(unusedBoxes);

            // For the number of changes, either insert or replace a box
            for (int i = 0; i < numberOfChanges; i++) {
                float rndFloat = rnd.nextFloat();
                if (rndFloat < 0.4) {
                    swapBox(newStack, newUnusedBoxes);
                } else if (rndFloat < 0.9) {
                    insertBox(newStack, newUnusedBoxes);
                } else {
                    removeBox(stack, unusedBoxes);
                }
            }

            if (newStack.height() >= stack.height()) {
                stack = newStack;
                unusedBoxes = newUnusedBoxes;
            }
        }

        if (!stack.validateStack())
            throw new RuntimeException();

        return stack;
    }

    private static BoxList makeList(BufferedReader file) {
        BoxList output = new BoxList();
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

    private static BoxStack makeInitialStack(BoxList unusedBoxes) {
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

            // If a box was inserted, remove it
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

    // Calculates the number of changes to make based on the total amount of considerations already made, the total
    // number of boxes and some ratio. The changes to make calculated using a quadratic
    private static int changesToMake(int nthConsideration, int maxConsiderations, int boxNumber) {
        double d = (double) (maxConsiderations - nthConsideration + 1) / (double) maxConsiderations;
        double square = d * d;
        return (int) Math.ceil((square * intLog2(boxNumber) * 2));
    }

    private static void swapBox(BoxStack stack, BoxList unusedBoxes) {
        if (unusedBoxes.size() == 0) return;

        int boxIndex = rnd.nextInt(unusedBoxes.size());
        Box boxToSwapIn = unusedBoxes.get(boxIndex);
        List<Box> boxes = boxToSwapIn.makeRotations();

        for (int i = 0; i < stack.size(); i++) {
            Box below = stack.get(i - 1);
            Box above = stack.get(i + 1);

            for (int j = 0; j < boxes.size(); j++) {
                Box box = boxes.get(j);
                if (box.getWidth() < below.getWidth() && box.getDepth() < below.getDepth()) {
                    if (box.getWidth() > above.getWidth() && box.getDepth() > above.getDepth()) {
                        unusedBoxes.set(boxIndex, stack.get(i));
                        stack.set(i, box);
                        return;
                    }
                } else {
                    boxes.remove(j);
                    j--;
                }
            }
        }
    }

    private static void insertBox(BoxStack stack, BoxList unusedBoxes) {
        if (unusedBoxes.size() == 0) return;

        int boxIndex = rnd.nextInt(unusedBoxes.size());
        Box boxToSwapIn = unusedBoxes.get(boxIndex);
        List<Box> boxes = boxToSwapIn.makeRotations();

        for (int i = 0; i <= stack.size(); i++) {
            Box below = stack.get(i - 1);
            Box above = stack.get(i);

            for (int j = 0; j < boxes.size(); j++) {
                Box box = boxes.get(j);
                if (box.getWidth() < below.getWidth() && box.getDepth() < below.getDepth()) {
                    if (box.getWidth() > above.getWidth() && box.getDepth() > above.getDepth()) {
                        stack.add(i, box);
                        unusedBoxes.remove(boxIndex);
                        return;
                    }
                } else {
                    boxes.remove(j);
                    j--;
                }
            }
        }
    }

    private static void removeBox(BoxStack stack, BoxList unusedBoxes) {
        if (stack.size() == 0) return;

        Box removedBox = stack.remove(rnd.nextInt(stack.size()));
        unusedBoxes.add(removedBox);
    }

    private static int intLog2(int i) {
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
