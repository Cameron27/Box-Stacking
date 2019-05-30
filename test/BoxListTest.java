import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BoxListTest {

    @Test
    void iterator() {
        BoxList list = new BoxList();
        list.add(new Box(0, 1, 2, 3));
        list.add(new Box(1, 4, 5, 6));
        list.add(new Box(2, 7, 8, 9));

        Iterator<Box> iter = list.rotationsIterator(2);
        while (iter.hasNext()) {
            Box b = iter.next();
            assertNotNull(b);
            System.out.println(b.toString());
        }
    }
}