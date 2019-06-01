import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("Duplicates")
public class NPStack2 {
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
        BoxStack stack = makeInitialStack(unusedBoxes);

        // For the number of considerations allowed
        for (int nthConsider = 1; nthConsider <= maxConsiderations; nthConsider++) {
            // Calculate the number of changes
            int numberOfChanges = changesToMake(nthConsider, maxConsiderations, boxCount);

            // Create clones
            BoxStack newStack = new BoxStack(stack);
            BoxList newUnusedBoxes = new BoxList(unusedBoxes);

            // For the number of changes, either insert or replace a box
            for (int i = 0; i < numberOfChanges; i++) {
                if (rnd.nextFloat() < 0.5) {
                    swapBox(newStack, newUnusedBoxes);
                } else {
                    insertBox(newStack, newUnusedBoxes);
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

        Collections.shuffle(output);
        return output;
    }

    private static BoxStack makeInitialStack(BoxList unusedBoxes) {
        BoxStack output = new BoxStack();

        int index = 0;
        while (index <= output.size()) {
            Box boxToRemove = null;

            // For every box
            Iterator<Box> iter = unusedBoxes.rotationsIterator(rnd.nextInt(unusedBoxes.size()));
            while (iter.hasNext()) {
                Box box = iter.next();
                Box below = output.get(index - 1);
                Box above = output.get(index);

                // See of that box can be inserted at the current index
                if (box.getWidth() < below.getWidth() && box.getDepth() < below.getDepth() && box.getWidth() > above.getWidth() && box.getDepth() > above.getDepth()) {
                    output.add(index, box);
                    boxToRemove = box;
                    break;
                }
            }

            // If a box was inserted, remove it
            if (boxToRemove != null) {
                unusedBoxes.removeId(boxToRemove.getId());
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
        int index = rnd.nextInt(stack.size());
        int limit = (int) Math.ceil(unusedBoxes.size() * 0.2) * 3;
        int count = 0;

        Iterator<Box> iter = unusedBoxes.rotationsIterator(rnd.nextInt(unusedBoxes.size()));
        while (iter.hasNext()) {
            Box box = iter.next();
            if (count > limit) return;

            Box below = stack.get(index - 1);
            Box above = stack.get(index + 1);

            // See of that box can be inserted at the current index
            if (box.getWidth() < below.getWidth() && box.getDepth() < below.getDepth() && box.getWidth() > above.getWidth() && box.getDepth() > above.getDepth()) {
                // Store old box and replace it
                Box oldBox = stack.get(index);
                stack.set(index, box);

                // Makes changes to list of unused boxes
                unusedBoxes.add(oldBox);
                unusedBoxes.removeId(box.getId());
                return;
            }
            count++;
        }
    }

    private static void insertBox(BoxStack stack, BoxList unusedBoxes) {
        int index = rnd.nextInt(stack.size());
        int limit = (int) Math.ceil(unusedBoxes.size() * 0.1) * 3;
        int count = 0;

        Iterator<Box> iter = unusedBoxes.rotationsIterator(rnd.nextInt(unusedBoxes.size()));
        while (iter.hasNext()) {
            Box box = iter.next();
            if (count > limit) return;

            Box below = stack.get(index - 1);
            Box above = stack.get(index);

            // See of that box can be inserted at the current index
            if (box.getWidth() < below.getWidth() && box.getDepth() < below.getDepth() && box.getWidth() > above.getWidth() && box.getDepth() > above.getDepth()) {
                // Store old box and replace it
                stack.add(index, box);

                // Makes changes to list of unused boxes
                unusedBoxes.removeId(box.getId());
                return;
            }
            count++;
        }
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
