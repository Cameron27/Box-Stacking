import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BoxListTest {

    @Test
    void iterator() {
        BoxList list = new BoxList();
        list.add(new Box(0, 1, 2, 3));
        list.add(new Box(1, 4, 5, 6));
        list.add(new Box(2, 7, 8, 9));

        for (Box b : list) {
            assertNotNull(b);
            System.out.println(b.toString());
        }

        BoxList newList = new BoxList(list);
        newList.add(new Box(3, 10, 11, 12));
        assertEquals(3, list.size());
        assertEquals(4, newList.size());
    }
}