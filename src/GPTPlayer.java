import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class GPTPlayer {
    private static final String API_KEY=
    private static final String BASE_URL = "https://api.gptsapi.net";
    private static final String ENDPOINT = "/v1/chat/completions";

    public int[] getMove(int[][] board) throws Exception {
        String boardState = boardToString(board);
        System.out.println("Current board state being sent to GPT:\n" + boardState);  // 打印当前棋盘状态

        JSONArray messages = new JSONArray();
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are an AI Gomoku (Five in a Row) player. The board is a 15x15 grid. Each cell is represented by 0 if it is empty, 1 if it contains a black stone, and 2 if it contains a white stone. The board indices start from 0, with rows numbered from bottom to top and columns numbered from left to right. Given the current board state below, return the next move for the white player strictly as a JSON array [row, col] without any additional text. Ensure the move is valid, does not overlap with existing stones, and follows the rules of Gomoku. In Gomoku, a player wins by placing five of their stones in a row, either horizontally, vertically, or diagonally.");

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", "Current board state:\n" + boardState + "\nMake sure the move is valid and does not overlap with existing stones. Only return coordinates in the format [row, col]. The board indices start from 0, with rows numbered from bottom to top.");

        messages.put(systemMessage);
        messages.put(userMessage);

        String response = callChatGPT(messages);
        System.out.println("API Response: " + response);  // 打印API响应以便调试

        JSONObject jsonResponse = new JSONObject(response);
        JSONArray choices = jsonResponse.getJSONArray("choices");
        JSONObject messageObject = choices.getJSONObject(0).getJSONObject("message");
        String moveString = messageObject.getString("content").trim();
        System.out.println("Move String: " + moveString);  // 打印响应内容

        // 提取JSON数组部分
        int[] move = new int[2];
        try {
            JSONArray moveArray = new JSONArray(moveString);
            move[0] = moveArray.getInt(0);
            move[1] = moveArray.getInt(1);
        } catch (Exception e) {
            throw new Exception("Invalid move format: " + moveString);
        }

        return move;
    }

    private String callChatGPT(JSONArray messages) throws Exception {
        URL url = new URL(BASE_URL + ENDPOINT);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
        connection.setDoOutput(true);

        JSONObject request = new JSONObject();
        request.put("model", "claude-3-sonnet-20240229");  // 使用正确的模型名称
        request.put("messages", messages);
        request.put("max_tokens", 50);
        request.put("temperature", 0.7);

        System.out.println("Request Body: " + request.toString());

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = request.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int status = connection.getResponseCode();
        System.out.println("HTTP Response Code: " + status);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                status >= 200 && status < 300 ? connection.getInputStream() : connection.getErrorStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

    private String boardToString(int[][] board) {
        JSONArray moves = new JSONArray();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != 0) {
                    JSONArray move = new JSONArray();
                    move.put(i);
                    move.put(j);
                    move.put(board[i][j]);
                    moves.put(move);
                }
            }
        }
        return moves.toString();
    }

    public static void main(String[] args) {
        try {
            GPTPlayer player = new GPTPlayer();
            int[][] board = new int[15][15]; // 创建一个空的Gomoku棋盘
            board[7][7] = 1; // 模拟一个黑子的落子位置
            board[8][8] = 2; // 模拟一个白子的落子位置
            int[] move = player.getMove(board);
            System.out.println("GPT建议的白子落子位置: " + move[0] + ", " + move[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
