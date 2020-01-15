import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.*;

public class Main extends JPanel implements KeyListener {
    int totalWidth = 30; int totalHeight = 30;
    int centerX = totalWidth / 2, centerY = totalHeight / 2;
    int cellSize = 50;
    Timer timer = new Timer(1000 / 10, x -> gameTick());
    Snake snake = new Snake(totalWidth / 2, totalHeight / 2, 5);
    Map<Point2D, CellType> map = new HashMap<>();
    Random random = new Random();
    Main() {
        map.put(snake.body.getFirst(), CellType.Snake);
        for (int i = 0; i < 5; i++)
            placeApple();
    }
    void placeApple() {
        for (int i = 0; i < 1000; i++) {
            int x = random.nextInt(totalWidth);
            int y = random.nextInt(totalHeight);
            var pos = new Point2D(x, y);
            if (map.get(pos) == null) {
                map.put(pos, CellType.Apple);
                return;
            };
        }
    }

    public static void main(String[] args) {
        var window = new JFrame();
        var panel = new Main();
        window.add(panel);
        panel.timer.start();
        window.setVisible(true);
        window.addKeyListener(panel);
    }

    BufferedImage cache;
    @Override
    public void paint(Graphics g) {
        preRender();
        g.drawImage(cache, 0, 0, this);
    }

    public void preRender() {
        var img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        var g = img.getGraphics();
        int width = getWidth() / cellSize;
        int height = getHeight() / cellSize;

        int offsetX = min(totalWidth - width, max(0, snake.body.getFirst().x - width / 2));
        int offsetY = max(0, snake.body.getFirst().y - width / 2);

        int fitX = min(totalWidth, offsetX + width);
        int fitY = min(totalHeight, offsetY + height);

        for (int x = offsetX; x < fitX; x++) {
            for (int y = offsetY; y < fitY; y++) {
                var type = map.getOrDefault(new Point2D(x, y), CellType.Blank);
                if (type == CellType.Blank) {
                    int xs = 255 / 2 * abs(x - centerX) / totalWidth;
                    int ys = 255 / 2 * abs(y - centerY) / totalHeight;
                    var gr = xs + ys;
                    g.setColor(new Color(gr, gr, gr));
                }
                else if (type == CellType.Apple) g.setColor(Color.red);
                else if (type == CellType.Snake) g.setColor(Color.gray);
                g.fillRect((x - offsetX) * cellSize, (y - offsetY) * cellSize, cellSize, cellSize);
            }
        }
        int x = snake.body.getFirst().x, y = snake.body.getFirst().y;
        g.setColor(Color.white);
        g.fillRect((x - offsetX) * cellSize, (y - offsetY) * cellSize, cellSize, cellSize);
        cache = img;
    }

    void move() {
        var newPos = snake.body.getFirst().add(snake.direction);
        snake.body.addFirst(newPos);


        if (map.get(newPos) == CellType.Apple) placeApple();
        else {
            var last = snake.body.removeLast();
            if (!last.equals(snake.body.getLast()))
                map.remove(last);
        }

        if (newPos.x < 0 || newPos.x >= totalWidth || newPos.y < 0 || newPos.y >= totalHeight || map.get(newPos) == CellType.Snake) {
            timer.stop();
            System.out.println("Game over!");
        }
        map.put(newPos, CellType.Snake);
        snake.prevDirection = snake.direction;
    }

    private void gameTick() {
        move();
        repaint();
    }
    public void keyTyped(KeyEvent keyEvent) { }
    public void keyPressed(KeyEvent keyEvent) {
        var direction = snake.direction;
        if (keyEvent.getKeyChar() == 'w') direction = new Point2D(0, -1);
        if (keyEvent.getKeyChar() == 's') direction = new Point2D(0, 1);
        if (keyEvent.getKeyChar() == 'a') direction = new Point2D(-1, 0);
        if (keyEvent.getKeyChar() == 'd') direction = new Point2D(1, 0);

        if (keyEvent.getKeyChar() == 'k') direction = new Point2D(0, -1);
        if (keyEvent.getKeyChar() == 'j') direction = new Point2D(0, 1);
        if (keyEvent.getKeyChar() == 'h') direction = new Point2D(-1, 0);
        if (keyEvent.getKeyChar() == 'l') direction = new Point2D(1, 0);

        snake.setDirection(direction);
    }
    public void keyReleased(KeyEvent keyEvent) { }
}
