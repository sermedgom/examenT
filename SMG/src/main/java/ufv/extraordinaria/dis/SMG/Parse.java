package ufv.extraordinaria.dis.SMG;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Parse {

    public ArrayList<Tweet> readJsonFile(String fichero){
        try {
            //lee el fichero que le pasemos y lo carga en un reader
            Reader reader = Files.newBufferedReader(Paths.get(fichero));
            // convierte el array JSON a un arraylist de users
            ArrayList<Tweet> prods = new Gson().fromJson(reader, new TypeToken<ArrayList<Tweet>>() {}.getType());
            // users.forEach(System.out::println);// imprime los users
            reader.close();// close reader
            return prods;

        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>(); //si no ha leido nada, devuelve un array vacio
        }

    }

    public boolean writeJsonFile(String fichero, ArrayList<Tweet> users){
        try{
            Writer writer = Files.newBufferedWriter(Paths.get(fichero));

            writer.write(new Gson().toJson(users));
            writer.close();
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

}