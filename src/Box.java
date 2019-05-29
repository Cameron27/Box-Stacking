import java.util.LinkedList;
import java.util.List;

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

    public List<Box> makeRotations() {
        List<Box> l = new LinkedList<>();
        l.add(new Box(id, width, depth, height));
        l.add(new Box(id, width, height, depth));
        l.add(new Box(id, height, depth, width));
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
