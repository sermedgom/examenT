package org.vaadin.example;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.googlecode.gentyref.TypeToken;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
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

    public Tweet postnuevo(Tweet Nueva) {
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
        return gson.fromJson(response.body(), new TypeToken<Tweet>() {
        }.getType());
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

    public MainView(@Autowired GreetService service) {

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        String response = getZona();
        //tweet[] zonas = gson.fromJson(response, tweet[].class);
        ArrayList<Tweet> zonas = gson.fromJson(response, new com.google.gson.reflect.TypeToken<ArrayList<Tweet>>() {
        }.getType());


        option1Cont = new VerticalLayout();
        TextField nombre = new TextField("Nombre");
        TextField mensaje = new TextField("Mensaje");
        DatePicker fecha = new DatePicker("Fecha");
        fecha.setValue(LocalDate.now());
        //fecha.setValue(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        Button submit = new Button("Submit", event -> UI.getCurrent().getPage().reload());

        option1Cont.add(nombre, mensaje, fecha, submit);
        submit.addClickListener(event -> {
            String nombreValue = nombre.getValue();
            String mensajeValue = mensaje.getValue();
            String fechaValue = fecha.getValue().toString();
            ; // Obtener la fecha seleccionada

            //LocalDate fechaValue = LocalDate.parse(dateValue, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // Do something with the values
            // for example, print them
            System.out.println("Nombre: " + nombreValue);
            System.out.println("Mensaje: " + mensajeValue);
            System.out.println("Fecha: " + fechaValue);

            int maxId = 0;
            for (Tweet tweet : zonas) {
                if (tweet.getId() > maxId) {
                    maxId = tweet.getId();
                }
            }

            // Incrementar el valor más alto en 1 para obtener un nuevo id
            int newId = maxId + 1;


            Tweet newprod = new Tweet(newId, nombreValue, mensajeValue, fechaValue);
            postnuevo(newprod);
        });
    }
}
