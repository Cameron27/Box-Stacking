import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Box class represents a box object with a width, depth, height and id
 *
 * @author Cameron Salisbury (1293897)
 */
public class Box {
    private int id;
    private int width;
    private int height;
    private int depth;

    public Box(int id, int base1, int base2, int height) {
        this.id = id;

        // Width is always the larger of the two base lengths
        this.width = Math.max(base1, base2);
        this.depth = Math.min(base1, base2);

        this.height = height;
    }

    // Creates a list of 3 boxes with all the different rotations of this box
    public List<Box> makeRotations() {
        List<Box> l = new LinkedList<>();

        // Adds each possible rotation, checking that the height is unique each time
        l.add(new Box(id, width, depth, height));
        if (depth != height)
            l.add(new Box(id, width, height, depth));
        if (width != height && width != depth)
            l.add(new Box(id, height, depth, width));

        // Shuffle boxes to remove bias from first rotation
        Collections.shuffle(l);
        return l;
    }

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }


    public int getDepth() {
        return depth;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return width + " " + depth + " " + height;
    }
}
