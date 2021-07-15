package com.movie.dugansamplecode;


import java.io.IOException;
import java.net.URISyntaxException;

public class DuganSampleCodeApplication {

    public static void main(String[] args) throws IOException, URISyntaxException {
        if(args.length == 2 && args[0].equals("storage"))
        {
            CsvController.loadMovies(args[1]);
        }
        else if (args.length == 4 && args[0].equals("query")){
            CsvController.getQuery(args[1],args[2],args[3]);
        }
        else{
            System.out.println("Invalid Arguments");
        }

    }

}
