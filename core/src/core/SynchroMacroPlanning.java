package core;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// import org.junit.*;
// import org.junit.runner.Result;

/**
 * Cette classe est le coeur du programme: elle contient toutes les fonctions importantes.
 *
 * <p>Les autres classes (Task,Date,Sector) sont des classes utiles pour mettre en forme
 * l'information de mani�re plus lisible que des tableaux/Listes du Q
 *
 * <p>
 *
 * @author Moaad Fattali
 */
public class SynchroMacroPlanning {

  /** La premi�re ligne de tous les CSV secteur */
  private static final String[] header = {"nom", "debut", "fin", "niveau", "ressources"};
  /** Liste des t�ches du syst�me */
  public static ArrayList<Task> tasks = new ArrayList<Task>();
  /** S�parateur de date utilis� pour la lecture */
  public static String dateSeparator = "/";
  /** S�parateur de valeur CSV utilis� pour la lecture */
  public static String CSVseparator = ";";
  /**
   * Liste des secteurs, mise � jour gr�ce au constructeurs de secteur via la m�thode Main.addSector
   */
  private static List<Sector> listOfSectors = new ArrayList<>();

  /**
   * convertit un tableau de String en int
   *
   * @param strTable
   * @return
   * @throws NumberFormatException
   */
  public static int[] convertToInt(String[] strTable) throws NumberFormatException {
    int n = strTable.length;
    int[] intTable = new int[n];
    for (int i = 0; i < n; i++) {
      intTable[i] = Integer.parseInt(strTable[i]);
    }
    return intTable;
  }

  /**
   * Retourne la liste des fichiers d'un dossier donn�e. Ne dispose pas de conditions sur
   * l'extension du fichier donc le programme peut renvoyer des erreurs si on lui donne autre chose
   * � bouffer que du CSV.
   *
   * @param directoryPath
   * @return
   */
  public static File[] listFiles(String directoryPath) {
    File[] files = null;
    File directoryToScan = new File(directoryPath);
    files = directoryToScan.listFiles();
    return files;
  }

  /**
   * Rajoute une t�che � la liste des t�ches du syst�me si la tache n'existe pas (deux taches sont
   * �gales si elles ont le m�me nom, cf.equalsTo dans la classe Task).
   *
   * <p>Si la t�che existe, modifie les dates de d�but et de fin pour que la dur�e de la t�che
   * finale soit la "r�union" temporelle de toute les dur�e choisies par les secteurs
   *
   * @param sector
   */
  public static void addTask(Task taskToAdd, Sector sector) {
    boolean notFound = true;
    System.out.println();
    for (Task task : tasks) {
      if (task.equals(taskToAdd)) {
        notFound = false;
        System.out.println("T�che " + task.getName() + " trouv�e");
      }
    }
    if (notFound) {
      tasks.add(taskToAdd);
      System.out.println(taskToAdd.toString() + " rajout�e dans la liste des t�ches");
    } else {
      try {
        Task task = fetchTask(taskToAdd.getName());
        task.addSector(sector);
        if (taskToAdd.getStart().compareTo(task.getStart()) < 0) {
          task.setStart(taskToAdd.getStart());
        }
        if (taskToAdd.getFinish().compareTo(task.getFinish()) > 0) {
          task.setFinish(taskToAdd.getFinish());
        }
      } catch (Exception e) {
        System.err.println("Erreur du Programme: t�che non trouv�e alors qu'elle devrait exister");
        e.printStackTrace();
      }
    }
  }

  /**
   * Cherche une t�che dans la liste des t�ches du syst�me, renvoie une erreur TaskNotFoundException
   * si elle n'est pas trouv�e
   *
   * <p>Elle n'est donc cens� �tre utilis�e que si l'on pense que la t�che existe vraiment.
   *
   * @param taskname
   * @return
   * @throws Exception
   */
  public static Task fetchTask(String taskname) throws Exception {
    for (Task task : tasks) {
      if (Task.equalstr(task.getName(), taskname)) {
        return task;
      }
    }
    Exception taskNotFoundException =
        new Exception("T�che \"" + taskname + "\" inexistante dans la liste des t�ches import�e");
    throw taskNotFoundException;
  }

  /**
   * Cr�e une t�che via le constructeur Task(String name, int yearS, int monthS, int dayOfMonthS,int
   * yearF, int monthF, int dayOfMonthF, Sector sector)
   *
   * <p>Et ce pour chaque ligne contenant dans sa deuxi�me colonne "/201" (une date quoi) du CSV
   *
   * @param file
   * @throws IOException
   */
  public static void importTasksFromFile(File file) throws IOException {
    try (FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.ISO_8859_1);
        CSVReader reader = new CSVReader(isr)) {
      String[] nextLine;
      String sectorName =
          file.getName()
              .substring(
                  0,
                  file.getName().length()
                      - 4); // nom du secteur = nom du fichier moins le string correspondant � l'extension .csv (4chars du coup)
      Sector sector = new Sector(sectorName); // cr�e un secteur par fichier
      System.out.println("Importation des t�ches du fichier " + sector.getName());
      while ((nextLine = reader.readNext()) != null) {
        for (String line : nextLine) {
          String[] values = line.split(CSVseparator);
          if (values.length > 1) { // pour �viter d'avoir une outOfBoundsException
            if (values[1].contains("/201")) { // si c'est une vraie t�che quoi
              /*
               * L� en gros on importe pour de vrai ce qu'il y a dans les CSV secteurs.
               * Values c'est le tableau correspondant � la ligne
               */
              String[] starts = values[1].split(dateSeparator); // ="dd/mm/yy"
              String[] finishs = values[2].split(dateSeparator); // ="dd/mm/yy"
              int[] start = new int[starts.length];
              int[] finish = new int[finishs.length];
              try {
                start = convertToInt(starts); // ={dd,mm,yy}
                finish = convertToInt(finishs); // ={dd,mm,yy}
              } catch (NumberFormatException e) {
                System.err.println(
                    "Erreur de date � la t�che: \""
                        + values[0]
                        + "\""); // ce bloc try catch n'�tait pas obligatoire mais permet en cas de
                // chibrage de remonter � la ligne qui chibre
                e.printStackTrace();
              }
              Task task =
                  new Task(
                      values[0], start[0], start[1], start[2], finish[0], finish[1], finish[2],
                      sector);
              SynchroMacroPlanning.addTask(task, sector);
              if (values.length
                  > 3) { // �a sert pas � grand chose, mais en gros �a permet de garder les
                // ressources dans le fichier qu'on va emporter plus tard
                task.setResources(values[3]);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Parcourt un dossier et applique la m�thode addTasks � tous ses fichiers
   *
   * @param path
   */
  public static void importTasks(String path) {
    for (File file : listFiles(path)) {
      try {
        importTasksFromFile(file);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Parcourt la liste des t�ches des secteurs et remplace chaque t�che par l'unique t�che de m�me
   * nom pr�sente dans la liste des t�che du syst�me.
   *
   * <p>Apr�s l'�xecution de cette fonction, les secteurs ayant la m�me t�che ont aussi les m�mes
   * dates de d�but et de fin pour leur t�che
   */
  public static void syncTasks() {

    for (Sector sector : listOfSectors) {
      for (Task task : sector.getListOfTasks()) {
        try {
          Task taskSync = fetchTask(task.getName());
          task.setFinish(taskSync.getFinish());
          task.setStart(taskSync.getStart());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  /** Export des CSV secteur dans le r�pertoire du projet java */
  public static void exportSectors() {
    for (Sector sector : listOfSectors) {
      System.out.println("D�but d'exportation du MacroPlanning " + sector);
      // String destinationfile=file.getAbsolutePath().split(".")[0]+"sync.csv";
      String destinationfile = sector.getName() + ".csv";
      try (OutputStream fisw = new FileOutputStream(destinationfile);
          OutputStreamWriter isw = new OutputStreamWriter(fisw, StandardCharsets.ISO_8859_1);
          CSVWriter writer = new CSVWriter(isw, ';', '\"', '\\', "\n")) {
        writer.writeNext(header);
        writer.writeNext(sector.getHeader());
        for (Task task : sector.getListOfTasks()) {
          writer.writeNext(task.toCSV());
        }
        //				for (Task task: sector.getListOfTasks()) {for (int ind=0;ind<4;ind++)
        // {System.out.print(task.toCSV()[ind]);}System.out.print("\n");}
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Exporte le r�capitulatif des t�che qui contient toutes les t�ches du syst�me. Un fichier
   * extr�mement utilse pour la v�rification et la d�tection de doublons ou de t�ches inexistantes.
   */
  public static void exportTasks() {
    String destinationfile = "R�capitulatif des t�ches.csv";
    try (OutputStream fisw = new FileOutputStream(destinationfile);
        OutputStreamWriter isw = new OutputStreamWriter(fisw, StandardCharsets.ISO_8859_1);
        CSVWriter writer = new CSVWriter(isw, ';', '\"', '\\', "\n")) {
      String[] headerT = {"nom", "debut", "fin", "niveau", "ressources", "secteurs"};
      writer.writeNext(headerT);
      for (Task task : tasks) {
        String sectors = "";
        for (Sector sector : task.getListOfSectors()) {
          sectors = sectors + sector.getName() + ", ";
        }
        String[] line = new String[task.toCSV().length + 1];
        for (int i = 0; i < task.toCSV().length; i++) {
          line[i] = task.toCSV()[i];
        }
        line[task.toCSV().length] = sectors;
        writer.writeNext(line);
      }
      //				for (Task task: sector.getListOfTasks()) {for (int ind=0;ind<4;ind++)
      // {System.out.print(task.toCSV()[ind]);}System.out.print("\n");}
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * M�thode Main
   *
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {

    JUnitCore junit = new JUnitCore();
    junit.addListener(new TextListener(System.out));
    // Result result = junit.run(SynchroMacroPlanning.class);

    System.out.println("--Programme de synchronisation des t�ches--");

    System.out.println("Fait par Moaad Fattali, respo planning P2020 pour le Raid Centrale Paris");

    System.out.println(
        "Rentrer le chemin du r�pertoire contenant les planning au format .CSV des secteurs:");

    Scanner scan = new Scanner(System.in);
    String directory = scan.nextLine();

    importTasks(directory);
    int rep = 0;
    List<Integer> reps = new ArrayList<>();
    reps.add(1);
    reps.add(2);
    do {
      System.out.println("Importation termin�e. Exporter les CSV synchronis�s?");
      System.out.println("1 - Oui");
      System.out.println("2 - Non");
      rep = scan.nextInt();
    } while (!reps.contains(rep));

    if (rep == 1) {
      syncTasks();
      exportSectors();
      exportTasks();
      System.out.println("Exportation termin�e.\n");
      System.err.println(
          "!! V�rifiez soigneusement la pr�sence de doublons, d'incoh�rences et d'erreurs dans le fichier R�capitulatif des t�ches.csv !!");
      System.out.println("Modifiez les CSV de base et recommencez si n�cessaire");
    }

    scan.close();

    //		File file = new File("MP/Macroplanning Com 2018 V1.csv");
    //		Main.addTasksFromFilev2(file);
  }

  public static void addSector(Sector sector) {
    SynchroMacroPlanning.listOfSectors.add(sector);
  }
}
