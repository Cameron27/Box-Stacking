import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BoxList extends ArrayList<Box> {
    public BoxList() {
        super();
    }

    public BoxList(BoxList list) {
        super(list);
    }

    public void removeId(int id) {
        Iterator<Box> iter = super.iterator();
        while (iter.hasNext()) {
            Box b;
            if ((b = iter.next()).getId() == id) {
                remove(b);
                return;
            }
        }
    }

    // Override iterator to modify how for each works
    @Override
    public Iterator<Box> iterator() {
        return new BoxListIterator();
    }

    // Iterator that returns three boxes for each box in the list, one box for each rotation
    private class BoxListIterator implements Iterator<Box> {
        int startIndex;
        int index;
        List<Box> rotationsInIndex;

        private BoxListIterator() {
            // Sets up random starting index
            if (size() > 0) {
                startIndex = (new Random()).nextInt(size());
                index = startIndex;
            } else {
                startIndex = -1;
                index = -1;
            }
            Box boxInIndex = get(index);
            rotationsInIndex = boxInIndex.makeRotations();
        }

        @Override
        public boolean hasNext() {
            // Indicates list is empty
            if (startIndex == -1) {
                return false;
            }
            // Indicates at last index and out of rotations
            else if ((index + 1) % size() == startIndex && rotationsInIndex.size() == 0) {
                return false;
            } else {
                return true;
            }

        }

        @Override
        public Box next() {
            // If out of rotations
            if (rotationsInIndex.size() == 0) {
                // Go to next index
                index = (index + 1) % size();

                // Check not at end
                if (index >= size()) return null;

                // Get the box and add every rotation to the list
                Box boxInIndex = get(index);
                rotationsInIndex = boxInIndex.makeRotations();
            }

            return rotationsInIndex.remove(0);
        }
    }
}
