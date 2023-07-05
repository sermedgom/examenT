package ufv.extraordinaria.dis.SMG;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class Controller {
    private static final String DATA_FILE_PATH = "data/archivo.json";

    @GetMapping("/data")
    public ArrayList<Tweet> users(){
        Parse reader = new Parse();
        ArrayList<Tweet> prod = reader.readJsonFile(DATA_FILE_PATH);
        return prod;
    }
    @PostMapping(path = "/data",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArrayList<Tweet>> crear(@RequestBody Tweet newtweet){
        Info prodhandle = new Info();
        ArrayList<Tweet> lst = prodhandle.addprod(newtweet);
        return new ResponseEntity<>(lst, HttpStatus.CREATED);
    }

    @PutMapping("/data/{id}")
    public ResponseEntity<ArrayList<Tweet>> actualizar(@PathVariable("id") int id,
                                                       @RequestBody Tweet tweetActualizado)
    {
        System.out.println("nuevo tweet:" + tweetActualizado.getMensaje());
        Info prodhandle = new Info();
        ArrayList<Tweet> lst = prodhandle.modificar(id, tweetActualizado);

        if (lst != null) {
            return new ResponseEntity<>(lst, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }




    @DeleteMapping("/data/{id}")
    public ResponseEntity<ArrayList<Tweet>> eliminar(@PathVariable int id) {
        System.out.println(id);
        Info prodhandle = new Info();
        ArrayList<Tweet> lst = prodhandle.deleteprod(id);
        if (lst != null) {
            return new ResponseEntity<>(lst, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
