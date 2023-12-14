package pl.coderslab;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.File;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    private static final String[] optionsArray = {"add", "remove", "list", "exit"};
    private static String[][] tasks;

    public static void printOptions() {
        System.out.println(ConsoleColors.BLUE + "Please select an option:" + ConsoleColors.RESET);
        for (int i = 0; i < optionsArray.length; i++) {
            System.out.println(optionsArray[i]);
        }
    }

    public static void validateDate(String dateString) throws WrongFieldContentException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateString);
            if (!dateString.equals(sdf.format(date))) {
                throw new WrongFieldContentException("Incorrect task date");
            }
        } catch (ParseException ex) {
            throw new WrongFieldContentException("Incorrect task date format");
        }
    }

    public static void validateCriticality(String criticalityString) throws WrongFieldContentException {
        if (!(StringUtils.equalsIgnoreCase("true", criticalityString) ||
                StringUtils.equalsIgnoreCase("false", criticalityString))) {
            throw new WrongFieldContentException("Criticality field must be either true or false");
        }
    }

    public static void loadFile(String fileName) throws FileNotFoundException, WrongFieldContentException, WrongNumberOfFieldsException {
        tasks = new String[0][3];

        File inputFile = new File(fileName);

        Scanner inputFileScanner = new Scanner(inputFile);

        int lineCount = 0;

        while (inputFileScanner.hasNextLine()) {
            String line;
            String[] lineSplit;
            int valueCnt;

            tasks = Arrays.copyOf(tasks, lineCount + 1);

            line = inputFileScanner.nextLine();

            lineSplit = line.split(",");

            tasks[lineCount] = new String[lineSplit.length];

            if (lineSplit.length != 3) {
                throw new WrongNumberOfFieldsException("Wrong number of fields. Required number of fields is 3");
            }

            for (valueCnt = 0; valueCnt < lineSplit.length; valueCnt++) {
                String currentValue = StringUtils.trim(lineSplit[valueCnt]);
                tasks[lineCount][valueCnt] = currentValue;

                switch (valueCnt) {
                    case 0:  // no validation for task name
                        break;
                    case 1:  // validation of date format
                        validateDate(currentValue);
                        break;
                    case 2:  // validation of true / false
                        validateCriticality(currentValue);
                        break;
                }
            }

            lineCount++;
        }
    }

    public static void listTasks() {

        for (int i = 0; i < tasks.length; i++) {
            System.out.println(i + ": " + tasks[i][0] + " " + tasks[i][1] + " " + tasks[i][2]);
        }
    }

    public static void addTask() {
        String taskInput;
        String dateInput;
        String importanceFlagInput;
        boolean repeatEntry;

        Scanner scan = new Scanner(System.in);
        System.out.println("Please add task description");
        taskInput = StringUtils.trim(scan.nextLine());

        do {
            repeatEntry = false;
            System.out.println("Please add task due date");
            dateInput = StringUtils.trim(scan.nextLine());

            try {
                validateDate(dateInput);
            } catch (WrongFieldContentException ex) {
                System.out.println(ConsoleColors.RED_BOLD + "Wrong date format! Right date format is yyyy-mm-dd." + ConsoleColors.RESET);
                repeatEntry = true;
            }
        } while (repeatEntry);

        do {
            repeatEntry = false;
            System.out.println("Is your task important: true / false");
            importanceFlagInput = StringUtils.trim(StringUtils.lowerCase(scan.nextLine()));

            try {
                validateCriticality(importanceFlagInput);
            } catch (WrongFieldContentException ex) {
                System.out.println(ConsoleColors.RED_BOLD + "Wrong value! Right value is either true or false." + ConsoleColors.RESET);
                repeatEntry = true;
            }
        } while (repeatEntry);

        tasks = Arrays.copyOf(tasks, tasks.length + 1);
        tasks[tasks.length - 1] = new String[3];
        tasks[tasks.length - 1][0] = taskInput;
        tasks[tasks.length - 1][1] = dateInput;
        tasks[tasks.length - 1][2] = importanceFlagInput;
    }

    public static void removeTask() {
        int taskToRemoveNumber;

        Scanner scan = new Scanner(System.in);
        System.out.println("Please select task number to remove");
        try {
            taskToRemoveNumber = Integer.parseInt(StringUtils.trim(scan.nextLine()));
            if (taskToRemoveNumber >= 0 && taskToRemoveNumber < tasks.length) {
                String[][] array1 = Arrays.copyOfRange(tasks, 0, taskToRemoveNumber);
                String[][] array2 = Arrays.copyOfRange(tasks, taskToRemoveNumber + 1, tasks.length);
                tasks = ArrayUtils.addAll(array1, array2);
            } else {
                System.out.println(ConsoleColors.RED_BOLD + "Wrong task number! Choose task from the list." + ConsoleColors.RESET);
            }

        } catch (NumberFormatException ex) {
            System.out.println(ConsoleColors.RED_BOLD + "Wrong format! Number expected." + ConsoleColors.RESET);
        }
    }

    public static void writeToFile(String fileName) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(fileName);
        for (int i = 0; i < tasks.length; i++) {
            printWriter.println(tasks[i][0] + ", " + tasks[i][1] + ", " + tasks[i][2]);
        }
        printWriter.close();
    }

    public static void main(String[] args) throws FileNotFoundException, WrongFieldContentException, WrongNumberOfFieldsException {
        loadFile("tasks.csv");

        System.out.println(Arrays.deepToString(tasks));

        String userInstruction;

        do {
            printOptions();
            Scanner scan = new Scanner(System.in);
            userInstruction = StringUtils.trim(StringUtils.lowerCase(scan.nextLine()));
            switch (userInstruction) {
                case "add":
                    addTask();
                    break;
                case "remove":
                    removeTask();
                    break;
                case "list":
                    listTasks();
                    break;
                case "exit":
                    System.out.println(ConsoleColors.RED_BOLD + "Bye, bye." + ConsoleColors.RESET);
                    break;
                default:
                    System.out.println(ConsoleColors.RED_BOLD + "No such option" + ConsoleColors.RESET);
            }
        } while (!userInstruction.equals("exit"));
        writeToFile("tasks.csv");
    }
}