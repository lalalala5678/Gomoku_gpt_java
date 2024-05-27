import javax.swing.*;

public class Gomoku {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gomoku");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 800);

            Game game = new Game(null);  // 初始化时先传入null
            BoardPanel boardPanel = new BoardPanel(game);  // 创建 BoardPanel 实例
            game.setBoardPanel(boardPanel);  // 将 BoardPanel 设置到 Game 实例中

            frame.add(boardPanel);
            frame.setVisible(true);

            game.start();
        });
    }
}
