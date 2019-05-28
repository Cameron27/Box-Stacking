import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

public class NPStack {
    static Random rnd = new Random();

    public static void main(String[] args) {
        // Checks for 2 arguments
        if (args.length != 2) {
            System.out.println("Usage: java NPStack [file] [maxConsider]");
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
            run(args[0], considerations);
        } catch (FileNotFoundException e) {
            System.err.println("File \"" + args[0] + "\" cannot be found");
        }
//        catch (IOException e) {
//            System.err.println("Error reading from file \"" + args[0] + "\" ");
//        }
    }

    static int run(String fileName, int maxConsiderations) throws FileNotFoundException {
        // Create list of boxes
        BufferedReader file = new BufferedReader(new FileReader(new File(fileName)));
        BoxList unusedBoxes = makeList(file);

        int boxCount = unusedBoxes.size();

        // Check at least one box was found in the file
        if (unusedBoxes.size() == 0) {
            System.err.println("No valid boxes in file \"" + fileName + "\"");
            return 0;
        }

        // Generate initial stack
        BoxStack stack = makeInitialStack(unusedBoxes);

        // For the number of considerations allowed
        for (int nthConsider = 1; nthConsider <= maxConsiderations; nthConsider++) {
            // Calculate the number of changes
            int numberOfChanges = changesToMake(nthConsider, maxConsiderations, boxCount, 10);

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

        return stack.height();
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
            for (Box box : unusedBoxes) {
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
            // noinspection ConstantConditions
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
    private static int changesToMake(int nthConsideration, int maxConsiderations, int boxNumber, int ratio) {
        double d = (double) (maxConsiderations - nthConsideration + 1) / (double) maxConsiderations;
        double square = d * d;
        return (int) Math.ceil((square * boxNumber) / ratio);
    }

    private static void swapBox(BoxStack stack, BoxList unusedBoxes) {
        int index = rnd.nextInt(stack.size());


        // Get list of all boxes that could go in the place
        BoxList possibleSwaps = new BoxList();
        for (Box box : unusedBoxes) {
            Box below = stack.get(index - 1);
            Box above = stack.get(index + 1);

            // See of that box can be inserted at the current index
            if (box.getWidth() < below.getWidth() && box.getDepth() < below.getDepth() && box.getWidth() > above.getWidth() && box.getDepth() > above.getDepth()) {
                possibleSwaps.add(box);
            }
        }

        // If some boxes were found
        if (possibleSwaps.size() > 0) {
            // Pick a random box
            Box boxToSwap = possibleSwaps.get(rnd.nextInt(possibleSwaps.size()));

            // Store old box and replace it
            Box oldBox = stack.get(index);
            stack.set(index, boxToSwap);

            // Makes changes to list of unused boxes
            unusedBoxes.add(oldBox);
            unusedBoxes.removeId(boxToSwap.getId());
        }
    }

    private static void insertBox(BoxStack stack, BoxList unusedBoxes) {
        int index = rnd.nextInt(stack.size());

        // Get list of all boxes that could go in the place
        BoxList possibleInserts = new BoxList();
        for (Box box : unusedBoxes) {
            Box below = stack.get(index - 1);
            Box above = stack.get(index);

            // See of that box can be inserted at the current index
            if (box.getWidth() < below.getWidth() && box.getDepth() < below.getDepth() && box.getWidth() > above.getWidth() && box.getDepth() > above.getDepth()) {
                possibleInserts.add(box);
            }
        }

        // If some boxes were found
        if (possibleInserts.size() > 0) {
            // Pick a random box
            Box boxToInsert = possibleInserts.get(rnd.nextInt(possibleInserts.size()));

            // Insert box
            stack.add(index, boxToInsert);

            // Makes changes to list of unused boxes
            unusedBoxes.removeId(boxToInsert.getId());
        }
    }
}
