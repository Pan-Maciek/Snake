import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import  static java.awt.event.KeyEvent.*;

import static java.lang.Math.*;

public class Main extends JPanel implements KeyListener {
    // config
    int totalWidth = 100, totalHeight = 100;
    int cellSize = 50;

    int centerX = totalWidth / 2, centerY = totalHeight / 2;

    Timer timer = new Timer(1000 / 10, x -> onTick());

    Snake snake = new Snake(centerX, centerY, 5);
    GameMap map = new GameMap(totalWidth, totalHeight);

    Random random = new Random();

    // rendering
    BufferedImage mapImage;
    Main() {
        map.changeCell(snake.body.getFirst(), CellType.Snake);

        for (int i = 0; i < sqrt(totalWidth * totalHeight); i++)
            map.placeRandom(CellType.Apple);
        for (int i = 0; i < sqrt(totalWidth * totalHeight); i++)
            map.placeRandom(CellType.Trap);
    }

    public static void main(String[] args) {
        var window = new JFrame();
        var panel = new Main();
        window.add(panel);
        panel.timer.start();
        window.setVisible(true);
        window.addKeyListener(panel);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        preRender();
        g.drawImage(mapImage, 0, 0, this);
        g.setColor(Color.white);
    }

    public void preRender() {
        var img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        var g = img.getGraphics();
        int width = getWidth() / cellSize;
        int height = getHeight() / cellSize;

        int offsetX = max(0, min(totalWidth - width, snake.body.getFirst().x - width / 2));
        int offsetY = max(0, min(totalHeight - height, snake.body.getFirst().y - height / 2));

        int fitX = min(totalWidth, offsetX + width + 1);
        int fitY = min(totalHeight, offsetY + height + 1);

        int drawOffsetX = max((getWidth() - totalWidth * cellSize) / 2, 0);
        int drawOffsetY = max((getHeight() - totalHeight * cellSize) / 2, 0);

        for (int x = offsetX; x < fitX; x++) {
            for (int y = offsetY; y < fitY; y++) {
                var type = map.cellAt(new Point2D(x, y), CellType.Blank);
                if (type == CellType.Blank) {
                    if ((x + y) % 2 == 0) g.setColor(new Color(18, 18, 18));
                    else g.setColor(new Color(19, 19, 19));
                }
                else if (type == CellType.Apple) g.setColor(new Color(0, 255, 0));
                else if (type == CellType.Trap) g.setColor(new Color(255, 0, 0));
                else if (type == CellType.Snake) g.setColor(new Color(40, 40, 40));
                g.fillRect((x - offsetX) * cellSize + drawOffsetX, (y - offsetY) * cellSize + drawOffsetY, cellSize, cellSize);
            }
        }
        int x = snake.body.getFirst().x, y = snake.body.getFirst().y;
        g.setColor(Color.white);
        g.fillRect((x - offsetX) * cellSize + drawOffsetX, (y - offsetY) * cellSize + drawOffsetY, cellSize, cellSize);
        var renderMiniMap = drawOffsetX + drawOffsetY == 0;

        if (renderMiniMap) {
            x = getWidth() - totalWidth * map.miniMapScale - 5;
            y = getHeight() - totalHeight * map.miniMapScale- 5;
            g.drawRect(x - 1, y - 1, totalWidth * map.miniMapScale + 2, totalHeight * map.miniMapScale + 2);
            g.drawImage(map.image, x, y, this);
        }

        mapImage = img;
    }

    void move() {
        var newPos = snake.body.getFirst().add(snake.direction);
        snake.body.addFirst(newPos);

        if (map.cellAt(newPos) == CellType.Apple) map.placeRandom(CellType.Apple);
        else {
            var last = snake.body.removeLast();
            if (!last.equals(snake.body.getLast()))
                map.clearCell(last);
        }

        if (newPos.x < 0 || newPos.x >= totalWidth ||
            newPos.y < 0 || newPos.y >= totalHeight ||
            map.cellAt(newPos) == CellType.Snake || map.cellAt(newPos) == CellType.Trap) {
            timer.stop();
            System.out.println("Game over!");
        }
        map.changeCell(newPos, CellType.Snake);
        snake.prevDirection = snake.direction;
    }

    void onTick() {
        update();
        repaint();
    }

    private void update() { move(); }

    public void keyPressed(KeyEvent keyEvent) {
        var e = keyEvent.getKeyCode();
        if (e == VK_W || e == VK_K || e == VK_UP) snake.setDirection(0, -1);
        if (e == VK_S || e == VK_J || e == VK_DOWN) snake.setDirection(0, 1);
        if (e == VK_A || e == VK_H || e == VK_LEFT) snake.setDirection(-1, 0);
        if (e == VK_D || e == VK_L || e == VK_RIGHT) snake.setDirection(1, 0);
    }

    public void keyReleased(KeyEvent keyEvent) { }
    public void keyTyped(KeyEvent keyEvent) { }
}
