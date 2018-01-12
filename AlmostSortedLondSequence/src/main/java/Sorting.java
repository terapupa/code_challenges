import java.util.Iterator;
import java.util.LinkedList;

public class Sorting {

    private static void sortAlmostSorted(Iterator<Integer> sequence, int k) {
        LinkedList<Integer> ll = new LinkedList<>();
        while (sequence.hasNext()) {
            while (ll.size() < k) {
                ll.add(sequence.next());
            }
            ll.sort((x1, x2) -> {
                if (x1 > x2) return 1;
                if (x1 < x2) return -1;
                return 0;
            });
            System.out.println(ll.poll());
            ll.addLast(sequence.next());
        }
        ll.sort((x1, x2) -> {
            if (x1 > x2) return 1;
            if (x1 < x2) return -1;
            return 0;
        });
        while (!ll.isEmpty()) {
            System.out.println(ll.poll());
        }
    }

}
