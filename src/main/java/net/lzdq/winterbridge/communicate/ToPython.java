package net.lzdq.winterbridge.communicate;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ToPython {

    static FileWriter fp;
    static BufferedWriter buffer;
    static int fail_count = 0;
    private static void getpipe(){
        String pipe_path = "/home/ldq/mc/winterbridge/scripts/pipe";
        try {
            fp = new FileWriter(pipe_path);
            buffer = new BufferedWriter(fp);
        } catch (IOException e) {
            //throw new RuntimeException(e);
        }

    }
    static{
        getpipe();
    }

    static Gson gson = new Gson();
    public static boolean send(Object req){
        String jsonContent = gson.toJson(req);
        try {
            buffer.write(jsonContent + "\n");
            buffer.flush();
        } catch (IOException e) {
            //throw new RuntimeException(e);
            getpipe();
            fail_count++;
            Minecraft.getInstance().player.displayClientMessage(Component.literal("IOException" + fail_count), true);
            return false;
        }
        return true;
    }
    public static String send_http(Object req){
        String jsonContent = gson.toJson(req);
        //BridgeMod.LOGGER.info(jsonContent);

        try{
            String url = "http://127.0.0.1:1234/";
            // TODO: add command line configure
            //BridgeMod.LOGGER.info("Line 1");
            HttpClient client = HttpClient.newHttpClient();
            //BridgeMod.LOGGER.info("Line 2");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonContent))
                    .build();
            //BridgeMod.LOGGER.info("Line 3");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //BridgeMod.LOGGER.info("Line 4");
            // Print response
            //System.out.println("Response status code: " + response.statusCode());
            //System.out.println("Response body: " + response.body());
            //BridgeMod.LOGGER.info(String.format("%d", response.statusCode()));
            //BridgeMod.LOGGER.info(response.body());
            return response.body();

        } catch (Exception e) {
            //BridgeMod.LOGGER.info("Exception");
            //e.printStackTrace();
        }
        return "Failed";
    }
}
