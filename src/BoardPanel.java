import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardPanel extends JPanel {
    private final Game game;
    private static final int BOARD_SIZE = 15;
    private static final int CELL_SIZE = 50;
    private static final int PADDING = 50;

    public BoardPanel(Game game) {
        this.game = game;
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = (e.getY() - PADDING + CELL_SIZE / 2) / CELL_SIZE;
                int col = (e.getX() - PADDING + CELL_SIZE / 2) / CELL_SIZE;
                if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                    game.handleMove(row, col);
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);
        drawBoard(g);
        drawStones(g);
    }

    private void drawBackground(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        Color color1 = new Color(210, 180, 140);
        Color color2 = new Color(255, 228, 196);
        GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawBoard(Graphics g) {
        g.setColor(Color.BLACK);
        for (int i = 0; i < BOARD_SIZE; i++) {
            g.drawLine(PADDING + i * CELL_SIZE, PADDING, PADDING + i * CELL_SIZE, PADDING + (BOARD_SIZE - 1) * CELL_SIZE);
            g.drawLine(PADDING, PADDING + i * CELL_SIZE, PADDING + (BOARD_SIZE - 1) * CELL_SIZE, PADDING + i * CELL_SIZE);
        }

        // 绘制交叉点圆点
        int[] positions = {3, 7, 11};
        for (int row : positions) {
            for (int col : positions) {
                drawPoint(g, PADDING + col * CELL_SIZE, PADDING + row * CELL_SIZE);
            }
        }
    }

    private void drawPoint(Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(x - 5, y - 5, 10, 10);
    }

    private void drawStones(Graphics g) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (game.getBoard()[row][col] == Game.BLACK) {
                    drawStone(g, col * CELL_SIZE + PADDING, row * CELL_SIZE + PADDING, Color.BLACK);
                } else if (game.getBoard()[row][col] == Game.WHITE) {
                    drawStone(g, col * CELL_SIZE + PADDING, row * CELL_SIZE + PADDING, Color.WHITE);
                }
            }
        }
    }

    private void drawStone(Graphics g, int x, int y, Color color) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int diameter = CELL_SIZE - 10;

        // 绘制棋子的阴影
        g2d.setColor(new Color(50, 50, 50, 100));
        g2d.fillOval(x - diameter / 2 + 3, y - diameter / 2 + 3, diameter, diameter);

        // 绘制棋子本身
        GradientPaint gradient = new GradientPaint(x - diameter / 2, y - diameter / 2, color.brighter(), x + diameter, y + diameter, color.darker());
        g2d.setPaint(gradient);
        g2d.fillOval(x - diameter / 2, y - diameter / 2, diameter, diameter);

        // 绘制棋子的高光
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.drawOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
    }
}
