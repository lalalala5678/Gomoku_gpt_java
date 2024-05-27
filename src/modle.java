import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class modle {
    private static final String API_KEY = "这是连接gpt的api的私钥，我才不告诉你";
    private static final String BASE_URL = "https://api.gptsapi.net";
    private static final String ENDPOINT = "/v1/chat/models";

    public static void main(String[] args) {
        try {
            String response = listModels();
            System.out.println("API Response: " + response);  // 打印API响应以便调试

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray models = jsonResponse.getJSONArray("data");

            System.out.println("Available Models:");
            for (int i = 0; i < models.length(); i++) {
                String modelName = models.getJSONObject(i).getString("id");
                System.out.println(modelName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String listModels() throws Exception {
        URL url = new URL(BASE_URL + ENDPOINT);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }
}
