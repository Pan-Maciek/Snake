public class Point2D {
    final int x, y;

    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point2D point2D = (Point2D) o;

        if (x != point2D.x) return false;
        return y == point2D.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    public Point2D add(Point2D direction) {
        return new Point2D(x + direction.x, y + direction.y);
    }

    public Point2D inv() {
        return new Point2D(-x, -y);
    }
}
