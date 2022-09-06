import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
 
class Main {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    int scannedPosition; // the position of patient zero
    int numberOfBoxes; // the total number of individuals
    double infectionRate;
    double recoveryRate;
    String information = ""; // this string holds the position and status of each person. Note that this string should never be printed outside of debugging purposes and instead treated sort of like a file
    int timestepEndless = 0; // this is the timestep counter for endless mode - Will not affect the code in timestep mode
    int timestepForced;

    System.out.println("Enter the number of boxes. It must be a perfect square");

    // the following loop checks to see if the integer has a perfect square and asssigns it to the numberOfBoxes variable
    while (true){
      numberOfBoxes = scanner.nextInt();

      if (Math.sqrt(numberOfBoxes) == (int)Math.sqrt(numberOfBoxes)){
        break;
      }
      else{
        System.out.println("Not a valid input");
      }
    }

    System.out.println("Enter the number of timesteps. It must be greater than 0");

    // the following loop checks to see if the value of timestepForced is greater than 0
    while (true){
      timestepForced = scanner.nextInt();

      if (timestepForced > 0){
        break;
      }
      else{
        System.out.println("Not a valid input");
      }
    }
 
    System.out.println("Enter the infection rate. It must be bewtween 0 and 1 inclusive");
    
    //the loop below checks to see if the input number is between 0 and 1 inclusive before assigning it to the infectionRate variable
    while(true){
      infectionRate = scanner.nextDouble();

      if (infectionRate >= 0 && infectionRate <= 1){
        break;
      }
      else{
        System.out.println("Not a valid input");
      }
    }

    System.out.println("Enter the recovery rate. It must be between 0 and 1 inclusive");

    //the loop below checks to see if the input number is between 0 and 1 inclusive before assigning it to the recoveryRate variable
    while(true){
      recoveryRate = scanner.nextDouble();
     
      if (recoveryRate >= 0 && recoveryRate <= 1){
        break;
      }
      else{
        System.out.println("Not a valid input");
      }
    }

    // prints the array for the first time. This is the only time it will be printed with the index instead of the status
    for (int i = 1; i <= numberOfBoxes; i++){
      if ( i < 10){
        if ((i % Math.sqrt(numberOfBoxes)) == 0){
          System.out.printf("| 0%d |\n", (i));
          information = information.concat(Integer.toString(i));
          information = information.concat("S");
          information = information.concat("|");
        }
        else {
          System.out.printf("| 0%d ", (i));
          information = information.concat(Integer.toString(i));
          information = information.concat("S");
          information = information.concat("|");
        }
      }
      else{
        if ((i % Math.sqrt(numberOfBoxes)) == 0){
          System.out.printf("| %d |\n", (i));
          information = information.concat(Integer.toString(i));
          information = information.concat("S");
          information = information.concat("|");
        }
        else {
          System.out.printf("| %d ", (i));
          information = information.concat(Integer.toString(i));
          information = information.concat("S");
          information = information.concat("|");
        }
      }
    }

    System.out.printf("Enter the position of the first infected person. The number needs to be between 1 and %d inclusive\n", numberOfBoxes);

    scannedPosition = scanner.nextInt();
 
    scanner.close();

    // updates the value in information to contain the infected person at the specified index
    information = StatusUpdater(information, scannedPosition, 'I');

    // prints grid
    GridPrinter(information, numberOfBoxes);
 
    System.out.println("Time Step: 0\nInfected: 1\nRecovered: 0\nRatio: 1:" + numberOfBoxes);
 
    File file = null; // file variable holds strings
    PrintWriter fileWriter = null; // writes to file


    // writes the string held by the variable information into a file
    try{
      file = new File("t0.txt");

      if (!file.exists()){
        file.createNewFile();
      }

      fileWriter = new PrintWriter(file);

      fileWriter.println(information);
 
      FileStatistics(numberOfBoxes, information, fileWriter, timestepEndless);
 
      fileWriter.close();
    }
    catch(IOException e){
    }
 
    int exitHandlerR = 0; // holds the number of recovered individuals
    int exitHandlerNull = 0; // holds the number of infected individuals
    int timestepCounter = 1; // counter for timestepForced

    // this loop goes through the grid to see if anyone gets infected or recovers
    while(timestepCounter <= timestepForced){
      for (int i = 1; i <= numberOfBoxes; i++){
        if (GetStatus(i, information).equals("S")){
          information = InfectionChanceCalculator(i, infectionRate, numberOfBoxes, information);
        }
        else if (GetStatus(i, information).equals("I")){
          if (Math.random() <= recoveryRate){
            information = StatusUpdater(information, i, 'R');
          }
        }
      }

      // changes 'i' to 'I'
      for (int i = 0; i <= numberOfBoxes; i++){
        if (GetStatus(i, information).equals("i")){
          information = StatusUpdater(information, i, 'I');
        }
      }
 
      GridPrinter(information, numberOfBoxes);

      timestepEndless++; // only used in endless mode
 
      // creates and writes to a new file each timestep
      try{
        file = new File("t" + Integer.toString(timestepEndless) + ".txt");

        if (!file.exists()){
          file.createNewFile();
        }

        fileWriter = new PrintWriter(file);

        fileWriter.println(information);
 
        FileStatistics(numberOfBoxes, information, fileWriter, timestepEndless);
      
        fileWriter.close();
      }
      catch(IOException e){
      }
 
      for(int i = 1; i <= numberOfBoxes; i++){
        if (GetStatus(i, information).equals("R")){
          exitHandlerR++;
        }
 
        if (GetStatus(i, information).equals("I") || GetStatus(i, information).equals("i")){
          exitHandlerNull++;
        }
      }
 
      System.out.println("Time Step: " + timestepCounter + "\nInfected: " + exitHandlerNull + "\nRecovered: " + exitHandlerR);

      RatioCalculator(exitHandlerNull, numberOfBoxes);
 
      // exits if everyone has the status'R'
      if(exitHandlerR == numberOfBoxes){
        System.exit(0);
        break;
      }
 
      exitHandlerR = 0;
 
      // exits if no one is infected
      if(exitHandlerNull == 0){
        System.exit(0);
        break;
      }

      /*if(exitHandlerNull == numberOfBoxes && infectionRate == 0){
        System.exit(0);
        break;
      }*/
 
      exitHandlerNull = 0;

      timestepCounter++;
 
      // pauses for 3 seconds
      try{
        Thread.sleep(3000);
      }
      catch(InterruptedException e){
      }
    }
  }

  // this method returns a string that holds the position and status of each person
  static String StatusUpdater(String info, int index, char status){
    String newString = "";
    if (index < 10){
      newString = newString.concat(info.substring(0, (info.indexOf(Integer.toString(index)) + 1)));
      newString = newString.concat(Character.toString(status));
      newString = newString.concat(info.substring((info.indexOf(Integer.toString(index)) + 2)));

      return newString;
    }
    else if (index > 9 && index < 100){
      newString = newString.concat(info.substring(0, (info.indexOf(Integer.toString(index)) + 2)));
      newString = newString.concat(Character.toString(status));
      newString = newString.concat(info.substring((info.indexOf(Integer.toString(index)) + 3)));

      return newString;
    }
    else if (index > 99 && index < 1000){
      newString = newString.concat(info.substring(0, index + 3));
      newString = newString.concat(Character.toString(status));
      newString = newString.concat(info.substring(index + 4));

      return newString;
    }

    return "";
  }

  // this method prints the grid that shows the status of each person
  static void GridPrinter(String information, int boxNumber){
    for (int i = 1; i <= boxNumber; i++){
      if ((i % Math.sqrt(boxNumber)) == 0){
        System.out.printf("| %s |\n", GetStatus(i, information));
      }
      else {
        System.out.printf("| %s ", GetStatus(i, information));
      }
    }
  }

  // this method takes an individual's positon, as well as the base infection rate to calculate the chance they get infected, and infects them if they pass the requirements
  static String InfectionChanceCalculator(int index, double infectionRate, int numberOfBoxes, String information){
    int count = 0;
    int idx = 0;

    idx = index - (int)Math.sqrt(numberOfBoxes);

    if (idx > 0) {
      if(GetStatus((index - (int)Math.sqrt(numberOfBoxes)), information).equals("I")){
        count++;
      }
    }

    idx = index - 1;
    if ((idx % (int)Math.sqrt(numberOfBoxes)) != 0) {
      if(GetStatus((index - 1), information).equals("I")){
        if ((index % (int)(Math.sqrt(numberOfBoxes))) != 1){
          count++;
        }
      }
    }

    idx = index + 1;
    if ((index % (int)Math.sqrt(numberOfBoxes)) != 0) { 
      if(GetStatus((index + 1), information).equals("I")){
        if ((index % (int)(Math.sqrt(numberOfBoxes))) != 0){
          count++;
        }
      }
    }

    idx = index + (int)Math.sqrt(numberOfBoxes);
    if ((index <= numberOfBoxes - (int)Math.sqrt(numberOfBoxes)) && (idx <= numberOfBoxes)) {
      if(GetStatus((index + (int)Math.sqrt(numberOfBoxes)), information).equals("I")){
        count++;
      }
    }

    infectionRate = infectionRate * count;

    if (infectionRate > 1){
      infectionRate = 1;
    }
    else if (infectionRate < 0){
      infectionRate = 0;
    }

    if (Math.random() <= infectionRate){
      return StatusUpdater(information, index, 'i');
    }

    return information;
  }

  static String GetStatus(int index, String information){
    if (index < 10){
      return information.substring((information.indexOf((Integer.toString(index))) + 1), (information.indexOf((Integer.toString(index))) + 2));
    }
    else if (index > 9 && index < 100){
      return information.substring((information.indexOf((Integer.toString(index))) + 2), (information.indexOf((Integer.toString(index))) + 3));
    }

    return "";
  }
 
  // this method writes all of the statistics to a file 
  static void FileStatistics(int numberOfBoxes, String information, PrintWriter fileWriter, int step){
    int statisticCounter = 0;
 
    for(int i = 1; i <= numberOfBoxes; i++){
      if (GetStatus(i, information).equals("S")){
        statisticCounter++;
      }
    }

    fileWriter.println("Time Step: " + step);
 
    fileWriter.println("Susceptible: " + Integer.toString(statisticCounter));
 
    statisticCounter = 0;
 
    for(int i = 1; i <= numberOfBoxes; i++){
      if (GetStatus(i, information).equals("I") || GetStatus(i, information).equals("i")){
        statisticCounter++;
      }
    }
 
    fileWriter.println("Infected: " + Integer.toString(statisticCounter));
 
    statisticCounter = 0;
 
    for(int i = 1; i <= numberOfBoxes; i++){
      if (GetStatus(i, information).equals("R")){
        statisticCounter++;
      }
    }
 
    fileWriter.println("Recovered: " + Integer.toString(statisticCounter));
 
    statisticCounter = 0;
  }

  // this method calculates the ratio between the number of infected and the total number of people
  static void RatioCalculator (int numberOfInfected, int numberOfBoxes){
    int max = 0;

    for (int i = 1; i <= numberOfInfected && i <= numberOfBoxes; i++){
      if(((numberOfInfected % i) == 0) && ((numberOfBoxes % i) == 0)){
        max = i;
      }
    }

    if (max != 0){
      System.out.println("Ratio: " + numberOfInfected / max + ":" + numberOfBoxes / max);
    }
    else{
      System.out.println("Ratio: " + numberOfInfected + ":" + numberOfBoxes);
    }
  }
}