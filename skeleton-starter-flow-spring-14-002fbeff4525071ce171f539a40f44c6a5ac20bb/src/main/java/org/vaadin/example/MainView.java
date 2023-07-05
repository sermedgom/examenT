package org.vaadin.example;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.googlecode.gentyref.TypeToken;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class MainView {

    public ArrayList<Tweet> data;

    VerticalLayout option1Cont;
    VerticalLayout option2Cont;
    HttpRequest request;
    HttpClient client = HttpClient.newBuilder().build();
    HttpResponse<String> response;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private String getZona() {
        try {
            String resource = "http://localhost:8090/data";
            //System.out.println(resource);
            request = HttpRequest
                    .newBuilder(new URI(resource))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            //System.out.println(response.body());
        } catch (URISyntaxException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response.body();
    }
    public Tweet postnuevo(Tweet Nueva){
        try {
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8090/data"))
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(gson.toJson(Nueva)))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return gson.fromJson(response.body(), new TypeToken<Tweet>(){}.getType());
    }

    public boolean eliminar(int id) {
        try {
            String resource = "http://localhost:8090/data/" + id;
            request = HttpRequest.newBuilder(new URI(resource))
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == HttpStatus.OK.value();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateTweet(int id, Tweet tweet) {
        try {
            System.out.println("ID:" + id);
            System.out.println(tweet.getMensaje());
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8090/data/" + id))
                    .header("Content-Type", "application/json")
                    .method("PUT", HttpRequest.BodyPublishers.ofString(gson.toJson(tweet)))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


}
