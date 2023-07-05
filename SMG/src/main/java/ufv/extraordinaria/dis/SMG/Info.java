package ufv.extraordinaria.dis.SMG;

import java.util.ArrayList;

public class Info {

    ArrayList<Tweet> addprod (Tweet produ){
        Parse reader = new Parse();
        ArrayList<Tweet> userlist = reader.readJsonFile("data/archivo.json");
        userlist.add(produ);
        reader.writeJsonFile("data/archivo.json",userlist);
        return userlist;
    }

    public ArrayList<Tweet> data;

    ArrayList<Tweet> deleteprod(int id) {

        System.out.println("despues->"+ id);
        Parse reader = new Parse();
        ArrayList<Tweet> userlist = reader.readJsonFile("data/archivo.json"); // Obtener la lista de tweets desde el archivo JSON
        int index = -1;

        // Buscar el tweet con el id proporcionado
        for (int i = 0; i < userlist.size(); i++) {
            if (userlist.get(i).getId() == id) {

                System.out.println("Encontrado");
                index = i;
                break;
            }
        }

        if (index != -1) {
            userlist.remove(index); // Eliminar el tweet de la lista
            System.out.println("user" + userlist);
            reader.writeJsonFile("data/archivo.json",userlist);
            System.out.println("Borrado");
            return userlist; // Devolver la lista actualizada de tweets
        } else {
            return null; // No se encontró el tweet con el id proporcionado
        }
    }

    ArrayList<Tweet> modificar(int id, Tweet tweetModificado) {
        Parse reader = new Parse();
        ArrayList<Tweet> tweetList = reader.readJsonFile("data/archivo.json"); // Obtener la lista de tweets desde el archivo JSON

        int index = -1;

        // Buscar el tweet con el id proporcionado
        for (int i = 0; i < tweetList.size(); i++) {
            if (tweetList.get(i).getId() == tweetModificado.getId()) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            tweetList.set(index, tweetModificado); // Reemplazar el tweet en la lista
            reader.writeJsonFile("data/archivo.json", tweetList); // Escribir la lista actualizada en el archivo JSON
            return tweetList; // Devolver la lista actualizada de tweets
        } else {
            return null; // No se encontró el tweet con el id proporcionado
        }
    }

}

