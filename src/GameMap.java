import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameMap {
    final int miniMapScale = 3;
    int width, height;
    GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        image = new BufferedImage(width * miniMapScale, height * miniMapScale, BufferedImage.TYPE_INT_ARGB);
        mg = (Graphics2D) image.getGraphics();
        mg.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0f));
    }

    Random random = new Random();
    Map<Point2D, CellType> map = new HashMap<>();
    Graphics2D mg;
    BufferedImage image;

    void clearCell(Point2D position) {
        mg.setColor(new Color(0, 0, 0, 0));
        mg.fillRect(position.x * miniMapScale, position.y * miniMapScale, miniMapScale, miniMapScale);
        map.remove(position);
    }

    void changeCell(Point2D position, CellType cellType) {
        clearCell(position);
        map.put(position, cellType);
        var g = image.getGraphics();
        if (cellType == CellType.Snake) g.setColor(new Color(255, 255, 255, 120));
        else if (cellType == CellType.Apple) g.setColor(new Color(0,255,0,120));
        else if (cellType == CellType.Trap) g.setColor(new Color(255, 0, 0, 120));
        g.fillRect(position.x * miniMapScale, position.y * miniMapScale, miniMapScale, miniMapScale);
    }

    CellType cellAt(Point2D position) { return map.get(position); }
    CellType cellAt(Point2D position, CellType defaultValue) { return map.getOrDefault(position, defaultValue); }

    void placeRandom(CellType cellType) {
        for (int i = 0; i < 1000; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            var pos = new Point2D(x, y);
            if (cellAt(pos) == null) {
                changeCell(pos, cellType);
                break;
            };
        }
    }
}
