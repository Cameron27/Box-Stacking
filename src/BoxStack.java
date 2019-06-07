import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * Box class is an ArrayList that represents a stack of boxes
 *
 * @author Cameron Salisbury (1293897)
 */
public class BoxStack extends ArrayList<Box> {
    public BoxStack() {
        super();
    }

    public BoxStack(BoxStack stack) {
        super(stack);
    }

    // Gets the height of the stack
    public int height() {
        int sum = 0;

        // Adds height of every box to sum
        for (Box b : this)
            sum += b.getHeight();

        return sum;
    }

    // Checks that a stack is valid
    public boolean validateStack() {
        // Empty stacks are valid
        if (size() == 0) return true;

        // Gets the bottom box
        Box current = get(0);
        Set<Integer> set = new HashSet<>();
        set.add(get(0).getId());

        // For each box
        for (int i = 1; i < size(); i++) {
            Box box = get(i);

            // If it is larger then the box below it, fail
            if (box.getWidth() > current.getWidth() || box.getDepth() > current.getDepth())
                return false;
            // If the box id has already been seen, fail
            if (set.contains(box.getId()))
                return false;
                // Otherwise record the box id
            else
                set.add(box.getId());

        }

        return true;
    }

    // Gets a box at a specific index, extends ArrayList.get by having -1 return the floor and
    // size() return the a 0x0x0 box
    @Override
    public Box get(int index) {
        // If index is -1 return a box as large as possible to represent floor
        if (index == -1) {
            return new Box(-1, Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
        }
        // If index is one above end of list, return box of 0 size to represent nothing about top of stack
        else if (index == size()) {
            return new Box(-1, 0, 0, 0);
        }
        // Otherwise just do normal .get for LinkedList
        else
            return super.get(index);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        int sum = 0;

        // For each box
        for (Box box : this) {
            // Add the height
            sum += box.getHeight();

            // Add line of box dimensions and current height
            result.append(String.format("%s %s\n", box.toString(), sum));
        }

        return result.toString();
    }
}
