package com.example.gomokuapi.controllers;

import com.example.gomokuapi.models.Cell;
import com.example.gomokuapi.models.Logic;
import javafx.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping(LogicController.BASE_URI)
public class LogicController {

        public static final String BASE_URI = "gomoku/v1/logic";
        Logic logicInstance = null;


        //  gomoku/v1/logic/nextmove
        @CrossOrigin(origins = "http://localhost:8081")
        @RequestMapping(value = "/nextmove", method = RequestMethod.GET)
        public ResponseEntity<?> IntelligentMove(){
            if (logicInstance == null){
                return new ResponseEntity<>("Initialize new game first",HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(logicInstance.IntelligentMove(),HttpStatus.OK);
        }


        // gomoku/v1/logic/newgame/100
        @CrossOrigin(origins = "http://localhost:8081")
        @RequestMapping(value = "/newgame/{size}", method = RequestMethod.GET)
        public ResponseEntity<String> newGame(@PathVariable("size") Integer size){
            this.logicInstance = new Logic(size,size);
            return new ResponseEntity<>("New game created",HttpStatus.OK);
        }

        // gomoku/v1/logic/setcell/20/20/1
        @CrossOrigin(origins = "http://localhost:8081")
        @RequestMapping(value = "/setcell/{x}/{y}/{value}", method = RequestMethod.GET)
            public ResponseEntity<String> newGame(
                    @PathVariable("x") Integer x,
                    @PathVariable("y") Integer y,
                    @PathVariable("value") Integer value

        ){
            if (logicInstance == null){
                return new ResponseEntity<>("Initialize new game first",HttpStatus.FORBIDDEN);
            }

            if (this.logicInstance.setCell(x,y,value))
                return new ResponseEntity<>("cell set",HttpStatus.OK);
            else
                return new ResponseEntity<>("cant set cell",HttpStatus.BAD_REQUEST);
        }

}
