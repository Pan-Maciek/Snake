import java.util.Deque;
import java.util.LinkedList;

public class Snake {
    Snake(int x, int y, int length) {
        for (int i = 0; i < length; i++) {
            body.addFirst(new Point2D(x, y));
        }
    }
    Deque<Point2D> body = new LinkedList<>();
    Point2D direction = new Point2D(-1, 0);
}