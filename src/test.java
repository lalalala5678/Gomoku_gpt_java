import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class test extends JFrame {
    private static final int SIZE = 15; // 棋盘大小
    private static final int GRID_SIZE = 40; // 格子大小
    private int[][] board = new int[SIZE][SIZE]; // 0: 空，1: 玩家，2: AI
    private boolean playerTurn = true;

    // 在此处定义您的API密钥
    private static final String API_KEY =

    public test() {
        setTitle("五子棋");
        setSize(SIZE * GRID_SIZE, SIZE * GRID_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (playerTurn) {
                    int x = e.getX() / GRID_SIZE;
                    int y = e.getY() / GRID_SIZE;
                    if (board[x][y] == 0) {
                        board[x][y] = 1;
                        repaint();
                        playerTurn = false;
                        if (checkWin(1)) {
                            JOptionPane.showMessageDialog(null, "玩家胜利！");
                            resetBoard();
                            return;
                        }
                        makeAIMove();
                    }
                }
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (int i = 0; i < SIZE; i++) {
            g.drawLine(i * GRID_SIZE, 0, i * GRID_SIZE, SIZE * GRID_SIZE);
            g.drawLine(0, i * GRID_SIZE, SIZE * GRID_SIZE, i * GRID_SIZE);
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 1) {
                    g.setColor(Color.BLACK);
                    g.fillOval(i * GRID_SIZE, j * GRID_SIZE, GRID_SIZE, GRID_SIZE);
                } else if (board[i][j] == 2) {
                    g.setColor(Color.RED);
                    g.fillOval(i * GRID_SIZE, j * GRID_SIZE, GRID_SIZE, GRID_SIZE);
                }
            }
        }
    }

    private void makeAIMove() {
        new Thread(() -> {
            try {
                String response = getGPTResponse(board);
                System.out.println("API Response: " + response); // 打印API响应
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.has("x") && jsonResponse.has("y")) {
                    int x = jsonResponse.getInt("x");
                    int y = jsonResponse.getInt("y");
                    board[x][y] = 2;
                    repaint();
                    if (checkWin(2)) {
                        JOptionPane.showMessageDialog(null, "AI胜利！");
                        resetBoard();
                    }
                    playerTurn = true;
                } else {
                    System.out.println("错误：响应中未找到 'x' 和 'y' 键。");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private boolean checkWin(int player) {
        // 实现五子棋的胜利判断逻辑
        // 这里简化为返回false，需要根据实际情况实现
        return false;
    }

    private void resetBoard() {
        board = new int[SIZE][SIZE];
        playerTurn = true;
        repaint();
    }

    private String getGPTResponse(int[][] board) throws Exception {
        // 将棋盘状态转换为API请求的格式
        JSONObject requestJson = new JSONObject();
        JSONArray boardJson = new JSONArray();
        for (int i = 0; i < SIZE; i++) {
            JSONArray row = new JSONArray();
            for (int j = 0; j < SIZE; j++) {
                row.put(board[i][j]);
            }
            boardJson.put(row);
        }
        requestJson.put("board", boardJson);

        URL url = new URL("https://api.gptsapi.net"); // 替换为实际的GPT API端点
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        // 添加授权密钥到请求头
        con.setRequestProperty("Authorization", "Bearer " + API_KEY);
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(requestJson.toString().getBytes());
        os.flush();
        os.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            test game = new test();
            game.setVisible(true);
        });
    }
}
