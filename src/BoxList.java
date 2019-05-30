import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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

    public Iterator<Box> rotationsIterator() {
        return rotationsIterator(0);
    }

    // Returns an iterator that iterates through all rotations
    public Iterator<Box> rotationsIterator(int i) {
        return new RotationsIterator(super.listIterator(i), super.listIterator());
    }

    // Iterator that returns three boxes for each box in the list, one box for each rotation
    private class RotationsIterator implements Iterator<Box> {
        ListIterator<Box> iter;
        ListIterator<Box> zeroIter;
        ListIterator<Box> oldIter;
        List<Box> rotationsInIndex;
        int startIndex;

        private RotationsIterator(ListIterator<Box> startIter, ListIterator<Box> zeroIter) {
            this.iter = startIter;
            rotationsInIndex = new ArrayList<>();
            startIndex = iter.nextIndex();

            if (startIndex != 0)
                this.zeroIter = zeroIter;
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext() || rotationsInIndex.size() != 0;
        }

        @Override
        public Box next() {
            // If out of rotations
            if (rotationsInIndex.size() == 0) {
                Box box = iter.next();

                if (zeroIter != null && !iter.hasNext()) {
                    oldIter = iter;
                    iter = zeroIter;
                    zeroIter = null;
                } else if (zeroIter == null && iter.nextIndex() == startIndex) {
                    iter = oldIter;
                }

                if (box != null)
                    rotationsInIndex = box.makeRotations();
                else
                    return null;
            }

            return rotationsInIndex.remove(0);
        }
    }
}
