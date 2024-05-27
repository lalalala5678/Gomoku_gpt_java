import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Game {
    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;

    private final int[][] board = new int[15][15];
    private int currentPlayer = BLACK;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final GPTPlayer gptPlayer = new GPTPlayer();
    private BoardPanel boardPanel;

    public Game(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
    }

    public void setBoardPanel(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
    }

    public int[][] getBoard() {
        return board;
    }

    public void start() {
        JOptionPane.showMessageDialog(null, "Game started. You are BLACK.");
    }

    public void handleMove(int row, int col) {
        if (board[row][col] == EMPTY && currentPlayer == BLACK) {
            board[row][col] = BLACK;
            currentPlayer = WHITE;
            boardPanel.repaint();
            checkWin();
            executor.submit(this::gptMove);
        }
    }

    private void gptMove() {
        try {
            int[] move = gptPlayer.getMove(board);
            board[move[0]][move[1]] = WHITE;
            currentPlayer = BLACK;
            SwingUtilities.invokeLater(() -> {
                boardPanel.repaint();
                JOptionPane.showMessageDialog(null, "GPT moved to (" + (move[0] + 1) + ", " + (move[1] + 1) + ").");
                checkWin();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkWin() {
        if (checkWinCondition(BLACK)) {
            JOptionPane.showMessageDialog(null, "BLACK wins!");
            resetGame();
        } else if (checkWinCondition(WHITE)) {
            JOptionPane.showMessageDialog(null, "WHITE wins!");
            resetGame();
        }
    }

    private boolean checkWinCondition(int player) {
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                if (checkDirection(row, col, 1, 0, player) || // 水平
                        checkDirection(row, col, 0, 1, player) || // 垂直
                        checkDirection(row, col, 1, 1, player) || // 正斜
                        checkDirection(row, col, 1, -1, player)) { // 反斜
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkDirection(int row, int col, int dRow, int dCol, int player) {
        int count = 0;
        for (int i = 0; i < 5; i++) {
            int r = row + i * dRow;
            int c = col + i * dCol;
            if (r >= 0 && r < 15 && c >= 0 && c < 15 && board[r][c] == player) {
                count++;
            } else {
                break;
            }
        }
        return count == 5;
    }

    private void resetGame() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = EMPTY;
            }
        }
        currentPlayer = BLACK;
        boardPanel.repaint();
        start();
    }
}
