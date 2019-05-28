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
