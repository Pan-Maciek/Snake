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
    int totalWidth = 300, totalHeight = 300;
    int cellSize = 50;
    int miniMapScale = 1;

    int centerX = totalWidth / 2, centerY = totalHeight / 2;

    Timer timer = new Timer(1000 / 10, x -> onTick());

    Snake snake = new Snake(centerX, centerY, 5);
    Map<Point2D, CellType> map = new HashMap<>();

    Random random = new Random();

    // rendering
    BufferedImage mapImage, miniMapImage;
    Graphics2D mg;
    Main() {
        miniMapImage = new BufferedImage(totalWidth * miniMapScale, totalHeight * miniMapScale, BufferedImage.TYPE_INT_ARGB);
        mg = (Graphics2D) miniMapImage.getGraphics();
        mg.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0f));
        map.put(snake.body.getFirst(), CellType.Snake);
        for (int i = 0; i < sqrt(totalWidth * totalHeight); i++)
            place(CellType.Apple);
        for (int i = 0; i < sqrt(totalWidth * totalHeight); i++)
            place(CellType.Trap);
    }

    void place(CellType type) {
        for (int i = 0; i < 1000; i++) {
            int x = random.nextInt(totalWidth);
            int y = random.nextInt(totalHeight);
            var pos = new Point2D(x, y);
            if (map.get(pos) == null) {
                updateCell(pos, type);
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

    @Override
    public void paint(Graphics g) {
        preRender();
        g.drawImage(mapImage, 0, 0, this);
        g.setColor(Color.white);
        int x = getWidth() - totalWidth * miniMapScale - 5;
        int y = getHeight() - totalHeight * miniMapScale- 5;
        g.drawRect(x - 1, y - 1, totalWidth * miniMapScale + 2, totalHeight * miniMapScale + 2);
        g.drawImage(miniMapImage, x, y, this);
    }

    void updateCell(Point2D position, CellType type) {
        clearCell(position);
        map.put(position, type);
        var g = miniMapImage.getGraphics();
        if (type == CellType.Snake) g.setColor(new Color(255, 255, 255, 120));
        else if (type == CellType.Apple) g.setColor(new Color(0,255,0,120));
        else if (type == CellType.Trap) g.setColor(new Color(255, 0, 0, 120));
        g.fillRect(position.x * miniMapScale, position.y * miniMapScale, miniMapScale, miniMapScale);
    }

    void clearCell(Point2D position) {
        var g = miniMapImage.getGraphics();
        mg.setColor(new Color(0, 0, 0, 0));
        mg.fillRect(position.x * miniMapScale, position.y * miniMapScale, miniMapScale, miniMapScale);
        map.remove(position);
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
                var type = map.getOrDefault(new Point2D(x, y), CellType.Blank);
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
        mapImage = img;
    }

    void move() {
        var newPos = snake.body.getFirst().add(snake.direction);
        snake.body.addFirst(newPos);

        if (map.get(newPos) == CellType.Apple) place(CellType.Apple);
        else {
            var last = snake.body.removeLast();
            if (!last.equals(snake.body.getLast()))
                clearCell(last);
        }

        if (newPos.x < 0 || newPos.x >= totalWidth ||
            newPos.y < 0 || newPos.y >= totalHeight ||
            map.get(newPos) == CellType.Snake || map.get(newPos) == CellType.Trap) {
            timer.stop();
            System.out.println("Game over!");
        }
        updateCell(newPos, CellType.Snake);
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
