import java.util.LinkedList;

public class BoxStack extends LinkedList<Box> {
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

    @Override
    public Box get(int index) {
        // If index is -1 return a box as large as possible to represent floor
        if (index == -1) {
            return new Box(-1, Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
        }
        // If index is one above end of list, return box of 0 size to represent nothing about top os stack
        else if (index == size()) {
            return new Box(-1, 0, 0, 0);
        }
        // Otherwise just do normal .get for LinkedList
        else
            return super.get(index);
    }
}
