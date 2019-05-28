import java.util.Iterator;
import java.util.LinkedList;

public class BoxList extends LinkedList<Box> {
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
        int index = -1;
        BoxList rotationsInIndex = new BoxList();

        @Override
        public boolean hasNext() {
            // Check either the index is not at then end or there is a rotation still at the current index
            return index + 1 < size() || rotationsInIndex.size() != 0;

        }

        @Override
        public Box next() {
            // If out of rotations
            if (rotationsInIndex.size() == 0) {
                // Go to next index
                index++;

                // Check not at end
                if (index >= size()) return null;

                // Get the box and add every rotation to the list
                Box boxInIndex = get(index);
                rotationsInIndex = new BoxList();
                rotationsInIndex.add(new Box(boxInIndex.getId(), boxInIndex.getWidth(), boxInIndex.getDepth(), boxInIndex.getHeight()));
                rotationsInIndex.add(new Box(boxInIndex.getId(), boxInIndex.getWidth(), boxInIndex.getHeight(), boxInIndex.getDepth()));
                rotationsInIndex.add(new Box(boxInIndex.getId(), boxInIndex.getHeight(), boxInIndex.getDepth(), boxInIndex.getWidth()));
            }

            return rotationsInIndex.remove();
        }
    }
}
