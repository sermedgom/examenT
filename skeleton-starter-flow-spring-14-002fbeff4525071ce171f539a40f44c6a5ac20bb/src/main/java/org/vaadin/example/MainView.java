package org.vaadin.example;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.time.LocalDate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.vaadin.example.MensajeFormatter.formatMensaje;

/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and
 * use @Route annotation to announce it in a URL as a Spring managed
 * bean.
 * Use the @PWA annotation make the application installable on phones,
 * tablets and some desktop browsers.
 * <p>
 * A new instance of this class is created for every new user and every
 * browser tab/window.
 */
@Route
@PWA(name = "Vaadin Application",
        shortName = "Vaadin App",
        description = "This is an example Vaadin application.",
        enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout {

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

    /**
     * Construct a new Vaadin view.
     * <p>
     * Build the initial UI state for the user accessing the application.
     *
     * @param service The message service. Automatically injected Spring managed bean.
     */
    public MainView(@Autowired GreetService service) {

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        String response = getZona();
        //tweet[] zonas = gson.fromJson(response, tweet[].class);
        ArrayList<Tweet> zonas = gson.fromJson(response, new TypeToken<ArrayList<Tweet>>(){}.getType());


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
            String fechaValue = fecha.getValue().toString();; // Obtener la fecha seleccionada

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


            Tweet newprod = new Tweet(newId, nombreValue,mensajeValue,fechaValue);
            postnuevo(newprod);
        });



        option2Cont = new VerticalLayout();
// ...

        Grid<Tweet> grid = new Grid<>();
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        grid.setItems(zonas);
        grid.addColumn(tweet -> formatMensaje(tweet.getMensaje(), tweet.getNombre(), tweet.getFecha()))
                .setHeader("Tweet");
        // Agregar columna de botones para eliminar
        grid.addComponentColumn(tweet -> {
            Button deleteButton = new Button("Borrar", event -> UI.getCurrent().getPage().reload());
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(event -> {
                eliminar(tweet.getId());
                //grid.setItems(getZona());
                Notification.show("Tweet eliminado", 3000, Notification.Position.MIDDLE);
            });
            return deleteButton;
        }).setHeader("");


        grid.addComponentColumn(tweet -> {
            //Tweet tweet = new Tweet();
            Button updateButton = new Button("Modificar");
            updateButton.addClickListener(event -> {
                Dialog dialog = new Dialog();
                dialog.setCloseOnOutsideClick(false);

                TextField nombreField = new TextField("Nombre");
                nombreField.setValue(tweet.getNombre());
                TextField mensajeField = new TextField("Mensaje");
                mensajeField.setValue(tweet.getMensaje());
                TextField fechaField = new TextField("Fecha");
                fechaField.setValue(tweet.getFecha());

                Button acceptButton = new Button("Aceptar", event1 -> {
                    String nombreValue = nombreField.getValue();
                    String mensajeValue = mensajeField.getValue();
                    String fechaValue = fechaField.getValue();

                    tweet.setNombre(nombreValue);
                    tweet.setMensaje(mensajeValue);
                    tweet.setFecha(fechaValue);

                    System.out.println("Antes de llamar");
                    updateTweet(tweet.getId(), tweet);
                    System.out.println("Despues de llamar");

                    UI.getCurrent().getPage().reload(); // Recargar la página

                    dialog.close();
                    Notification.show("Tweet modificado", 3000, Notification.Position.MIDDLE);
                });
                Button cancelButton = new Button("Cancelar", event1 -> dialog.close());

                dialog.add(nombreField, mensajeField, fechaField, acceptButton, cancelButton);
                dialog.open();
            });
            return updateButton;
        }).setHeader("");

        option2Cont.add(grid);


        option2Cont.setVisible(false);

        Tab option1 = new Tab("Form");
        Tab option2 = new Tab("Tabla");


        Tabs tabs = new Tabs(option1, option2);
        tabs.addSelectedChangeListener(event -> {
                    this.hideContainers();
                    Tab selectedTab = event.getSelectedTab();
                    if (selectedTab == option1){
                        option1Cont.setVisible(true);
                    }
                    if (selectedTab == option2){
                        option2Cont.setVisible(true);
                    }
                }
                //           setContent(event.getSelectedTab())
        );
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(false);
        //setContent(tabs.getSelectedTab());

        add(tabs, content,option1Cont,option2Cont );


    }
    private void hideContainers(){
        option1Cont.setVisible(false);
        option2Cont.setVisible(false);

    }

}