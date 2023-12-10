package pl.coderslab;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    public static String[][] loadFile(String fileName) throws FileNotFoundException, WrongFieldContentException, WrongNumberOfFieldsException {
        String[][] resultArray = new String[0][3];

        File inputFile = new File(fileName);

        Scanner inputFileScanner = new Scanner(inputFile);

        int lineCount = 0;

        while (inputFileScanner.hasNextLine()) {
            String line;
            String[] lineSplit;
            int valueCnt;

            resultArray = Arrays.copyOf(resultArray, lineCount + 1);

            line = inputFileScanner.nextLine();

            lineSplit = line.split(",");

            resultArray[lineCount] = new String[lineSplit.length];

            if (lineSplit.length != 3) {
                throw new WrongNumberOfFieldsException("Wrong number of fields. Required number of fields is 3");
            }

            for (valueCnt = 0; valueCnt < lineSplit.length; valueCnt++) {
                String currentValue = StringUtils.trim(lineSplit[valueCnt]);
                resultArray[lineCount][valueCnt] = currentValue;

                switch (valueCnt) {
                    case 0:  // no validation for task name
                        break;
                    case 1:  // validation of date format
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = sdf.parse(currentValue);
                            if (!currentValue.equals(sdf.format(date))) {
                                throw new WrongFieldContentException("Incorrect task date");
                            }
                        }
                        catch (ParseException ex) {
                            throw new WrongFieldContentException("Incorrect task date format");
                        }
                        break;
                    case 2:  // validation of true / false
                        if (!(StringUtils.equalsIgnoreCase("true", currentValue) ||
                                StringUtils.equalsIgnoreCase("false", currentValue))) {
                            throw new WrongFieldContentException("Criticality field must be either true or false");
                        }
                        break;
                }
            }

            lineCount++;
        }

        return resultArray;
    }

    public static void main(String[] args) throws FileNotFoundException, WrongFieldContentException, WrongNumberOfFieldsException {
        String[][] fileContent = loadFile("tasks.csv");

        System.out.println(Arrays.deepToString(fileContent));
    }
}