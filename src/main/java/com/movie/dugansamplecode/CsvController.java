package com.movie.dugansamplecode;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CsvController {
    public static final String TEMP_STORAGE_FILE = "src/main/resources/tempFile.csv";
    public static final String STORAGE_CSV = "src/main/resources/storage.csv";
    public static final String[] TABLE_CSV = {"STB","TITLE","PROVIDER","DATE","REV","VIEW_TIME"};

    public static void loadMovies(String filePath) throws IOException {

        //attempts to read the file provided
        Scanner myReader = null;
        try {
            myReader = new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred. Check if File path is correct");
            e.printStackTrace();
        }
        assert myReader != null;
        myReader.nextLine();

        //creates a temp file to load new data
        //sets the column names initially
        CSVWriter csvWriter = new CSVWriter(
                new FileWriter(TEMP_STORAGE_FILE, true),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.RFC4180_LINE_END);

        csvWriter.writeNext(TABLE_CSV);
        csvWriter.close();


        //reads though all lines in the file provided
        while (myReader.hasNextLine()) {
            String newLine = myReader.nextLine();

            csvWriter = new CSVWriter(
                    new FileWriter(TEMP_STORAGE_FILE, true),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.RFC4180_LINE_END);

            //replaces | with csv standard
            String[] newLineCells = newLine.split("\\|");

            Reader reader = Files.newBufferedReader(Paths.get(STORAGE_CSV));

            //loops through the csv file and tries to find an existing primary key STB, TITLE and DATE.
            //if found it writes the new line instead of the old one. If not found it writes the new line instead of the old
            CSVReader csvReader = new CSVReader(reader);
            String[] oldCsvLine;
            boolean found = false;
            while ((oldCsvLine = csvReader.readNext()) != null && !found) {
                if(oldCsvLine[0].equals(newLineCells[0]) && oldCsvLine[1].equals(newLineCells[1]) && oldCsvLine[3].equals(newLineCells[3])){
                    csvWriter.writeNext(newLineCells);
                    found = true;
                }

            }

            if (!found){
                csvWriter.writeNext(newLineCells);
            }
            csvWriter.close();
            reader.close();
            csvReader.close();
        }
        myReader.close();

        //replaces old storage file with the new one
        new File(TEMP_STORAGE_FILE).renameTo(new File(STORAGE_CSV));

        System.out.println("File Loaded");

    }

    public static void getQuery(String select, String order, String filter) throws IOException {


        Reader reader = Files.newBufferedReader(Paths.get(STORAGE_CSV));
        CSVReader csvReader = new CSVReader(reader);

        List<String[]> outputQuery = csvReader.readAll();

        csvReader.close();

        //filters values by select provided
        if(select.length() > 3){
            outputQuery = selectStatment(select, outputQuery);
        }

        //filters values by filter provided
        if(filter.length() > 3){
            filterStatement(filter, outputQuery);

        }

        //rearrange the file by order provided
        if(order.length() > 3){
            outputQuery = orderStatement(order, outputQuery);
        }

        //once done we check if we didnt filter everything is not
        //if there is data to print I print the results.
        if(outputQuery.size() > 1){
            for(String[]row : outputQuery.subList(1,outputQuery.size()))
            {
                System.out.println(Arrays.toString(row).replace("[", "").replace("]", ""));
            }
        }
        else{
            System.out.println("No Results Returned");
        }

    }

    private static List<String[]> orderStatement(String order, List<String[]> query) {

        //grabs data provided
        List<String> orderArray = Arrays.asList(order.replace(" ", "").replace("-o", "").split(","));

        //gets the column names
        List<String> columnName = Arrays.asList(query.get(0));

        //sorts though the list and rearranges it.
        for (int column = orderArray.size(); column-- > 0; ) {
            int columnPosition = columnName.indexOf(orderArray.get(column));
            query = sortbyColumn(query, columnPosition);
        }
        return query;
    }

    private static void filterStatement(String filter, List<String[]> query) {
        //grabs data provided
        List<String> filterArray = Arrays.asList(filter.replace(" ", "").replace("-f", "").split(","));
        //gets the column names
        List<String> columnName = Arrays.asList(query.get(0));

        //Breaks up the data provided by the column name and the data we need to match
        for(String i : filterArray){
            String[] breakUpFilter = i.split("=");
            int filterPosition = columnName.indexOf(breakUpFilter[0]);
            int j = 1;
            while (j < query.size()){
                //If we dont find the results we are looking for we delete it
                if(!query.get(j)[filterPosition].equals(breakUpFilter[1])){
                    query.remove(j);
                }
                else{
                    j++;
                }
            }
        }
    }

    private static List<String[]> selectStatment(String select, List<String[]> query) {
        //grabs data provided
        List<String> selectArray = Arrays.asList(select.replace(" ", "").replace("-s", "").split(","));

        //gets the column names
        List<String> columnName = Arrays.asList(query.get(0));
        List<String[]> newQuery = new ArrayList<>();

        //We create a new query so we can sort in the order the user provides.
        //we grab the old query data and put it into the new one in the correct order
        for(int column = 0; column < selectArray.size(); column++){
            int columnPosition = columnName.indexOf(selectArray.get(column));

            for(int row = 0; row < query.size(); row++){
                //If this is the first pass through we need to create the arrays in the query results
                if(newQuery.size() - 1 < row){
                    newQuery.add(new String[selectArray.size()]);
                }
                newQuery.get(row)[column] = query.get(row)[columnPosition];
            }
        }

        query = newQuery;
        return query;
    }

    //this allows is to sort through each row and rearrange them.
    public static List<String[]> sortbyColumn(List<String[]> query, int col)
    {
        Collections.sort(query.subList(1, query.size()),
                new Comparator<String[]>() {
                    public int compare(final String[] row1,
                                       final String[] row2) {

                        if (row1[col].compareTo(row2[col]) >= 0)
                            return 1;
                        else
                            return -1;
                    }
                });
        return query;

    }

}
