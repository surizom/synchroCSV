import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Cette classe est le coeur du programme: elle contient toutes les fonctions importantes.
 *
 * <p>Les autres classes (Task,Date,Sector) sont des classes utiles pour mettre en forme
 * l'information de manière plus lisible que des tableaux/Listes du Q
 *
 * <p>
 *
 * @author Moaad Fattali
 */
public class Synchronizer {

  /** La première ligne de tous les CSV secteur */
  private static final String[] FILE_HEADER = {"nom", "debut", "fin", "niveau", "ressources"};
  /** Séparateur de date utilisé pour la lecture */
  private static final String DATE_SEPARATOR = "/";
  /** Séparateur de valeur CSV utilisé pour la lecture */
  private static final String CSV_SEPARATOR = ";";
  /** Charset */
  private static final Charset CSV_CHARSET = Charset.availableCharsets().get("ISO-8859-2");
  /** Ensemble des tâches du système */
  private static Set<Task> tasks = new HashSet<>();
  /**
   * Ensemble des secteurs, mise à jour grâce au constructeurs de secteur via la méthode Main.addSector
   */
  private static Set<Sector> sectors = new HashSet<>();

  /**
   * convertit un tableau de String en int
   *
   * @param strTable
   * @return
   * @throws NumberFormatException
   */
  private static int[] convertToInt(String[] strTable) throws NumberFormatException {
    int n = strTable.length;
    int[] intTable = new int[n];
    for (int i = 0; i < n; i++) {
      intTable[i] = Integer.parseInt(strTable[i]);
    }
    return intTable;
  }

  /**
   * Retourne la liste des fichiers d'un dossier donnée. Ne dispose pas de conditions sur
   * l'extension du fichier donc le programme peut renvoyer des erreurs si on lui donne autre chose
   * à bouffer que du CSV.
   *
   * @param directoryPath
   * @return
   */
  private static File[] listFiles(String directoryPath) {
    return new File(directoryPath).listFiles();
  }

  /**
   * Rajoute une tâche à la liste des tâches du système si la tache n'existe pas (deux taches sont
   * égales si elles ont le même nom, cf.equalsTo dans la classe Task).
   *
   * <p>Si la tâche existe, modifie les dates de début et de fin pour que la durée de la tâche
   * finale soit la "réunion" temporelle de toute les durée choisies par les secteurs
   *
   * @param sector
   */
  private static void addTask(Task taskToAdd, Sector sector) {
    boolean notFound = true;
    System.out.println();
    for (Task task : tasks) {
      if (task.equals(taskToAdd)) {
        notFound = false;
        System.out.println("Tâche " + task.getName() + " trouvée");
      }
    }
    if (notFound) {
      tasks.add(taskToAdd);
      System.out.println(taskToAdd.toString() + " rajoutée dans la liste des tâches");
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
        System.err.println("Erreur du Programme: tâche non trouvée alors qu'elle devrait exister");
        e.printStackTrace();
      }
    }
  }

  /**
   * Cherche une tâche dans la liste des tâches du système, renvoie une erreur TaskNotFoundException
   * si elle n'est pas trouvée
   *
   * <p>Elle n'est donc censé être utilisée que si l'on pense que la tâche existe vraiment.
   */
  private static Task fetchTask(final String taskName) {
    // On cherche la premiere tache dont le nom correspond à la tache qu'on cherche
    Optional<Task> taskToFind =
        tasks.stream().filter(task -> task.getName().equals(taskName)).findFirst();
    if (taskToFind.isPresent()) {
      return taskToFind.get();
    }
    throw new RuntimeException(
        "Tâche \"" + taskName + "\" inexistante dans la liste des tâches importée");
  }

  private static String getSectorNameFromFile(File file) {
    return file.getName().substring(0, file.getName().length() - 4);
  }

  /**
   * Crée une tâche via le constructeur Task(String name, int yearS, int monthS, int dayOfMonthS,int
   * yearF, int monthF, int dayOfMonthF, Sector sector)
   *
   * <p>Et ce pour chaque ligne contenant dans sa deuxième colonne "/201" (une date quoi) du CSV
   */
  private static void importTasksFromFile(File file) throws IOException {
    try {
      FileInputStream fis = new FileInputStream(file);
      InputStreamReader isr = new InputStreamReader(fis, CSV_CHARSET);
      CSVReader reader = new CSVReader(isr);
      String[] nextLine;
      Sector sector = new Sector(getSectorNameFromFile(file)); // crée un secteur par fichier
      System.out.println("Importation des tâches du fichier " + sector.getName());
      while ((nextLine = reader.readNext()) != null) {
        for (String line : nextLine) {
          String[] values = line.split(CSV_SEPARATOR);
          if (values.length > 1) { // pour éviter d'avoir une outOfBoundsException
            if (values[1].contains("/201")) { // si c'est une vraie tâche quoi
              /*
               * Là en gros on importe pour de vrai ce qu'il y a dans les CSV secteurs.
               * Values c'est le tableau correspondant à la ligne
               */
              int[] start;
              int[] finish;
              try {
                start = convertToInt(values[1].split(DATE_SEPARATOR)); // ={dd,mm,yy}
                finish = convertToInt(values[2].split(DATE_SEPARATOR)); // ={dd,mm,yy}
              } catch (NumberFormatException e) {
                throw new RuntimeException(
                    "Erreur de date à la tâche: \""
                        + values[0]
                        + "\" du secteur "
                        + sector.getName()
                        + "Veuillez la corriger et relancer le programme");
                // RuntimeException permet en cas de
                // chibrage de remonter à la ligne qui chibre
              }
              Task task =
                  new Task(
                      values[0], start[0], start[1], start[2], finish[0], finish[1], finish[2],
                      sector);
              addTask(task, sector);
              if (values.length
                  > 3) { // ça sert pas à grand chose, mais en gros ça permet de garder les
                // ressources dans le fichier qu'on va exporter plus tard
                task.setResources(values[3]);
              }
            }
          }
        }
      }
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Fichier non trouvé :" + file.getName());
    }
  }

  /**
   * Parcourt un dossier et applique la méthode addTasks à tous ses fichiers .csv
   *
   * @param path
   */
  private static void importTasks(String path) {
    for (File file : listFiles(path)) {
      if (file.getName().endsWith(".csv")) {
        try {
          importTasksFromFile(file);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Parcourt la liste des tâches des secteurs et remplace chaque tâche par l'unique tâche de même
   * nom présente dans la liste des tâche du système.
   *
   * <p>Après l'éxecution de cette fonction, les secteurs ayant la même tâche ont aussi les mêmes
   * dates de début et de fin pour leur tâche
   */
  private static void syncTasks() {

    for (Sector sector : sectors) {
      for (Task task : sector.getListOfTasks()) {
        // TaskSync = tâche présente dans la list de tâches, on prend ses valeurs de début et fin et
        // on l'applique partout
        Task taskSync = fetchTask(task.getName());
        task.setFinish(taskSync.getFinish());
        task.setStart(taskSync.getStart());
      }
    }
  }

  /** Export des CSV secteur dans le répertoire export du projet java */
  private static void exportSectors() {
    for (Sector sector : sectors) {
      System.out.println("Début d'exportation du MacroPlanning " + sector);
      String destinationfile = "export\\" + sector.getName() + ".csv";
      try {
        OutputStream fisw = new FileOutputStream(destinationfile);
        OutputStreamWriter isw = new OutputStreamWriter(fisw, CSV_CHARSET);
        CSVWriter writer = new CSVWriter(isw, ';', '\"', '\\', "\n");
        writer.writeNext(FILE_HEADER);
        writer.writeNext(sector.getHeader());
        for (Task task : sector.getListOfTasks()) {
          writer.writeNext(task.toCSV());
        }
        //				for (Task task: sector.getListOfTasks()) {for (int ind=0;ind<4;ind++)
        // {System.out.print(task.toCSV()[ind]);}System.out.print("\n");}
      } catch (FileNotFoundException e) {
        throw new RuntimeException(
            "fichier d'export non trouvé pour le secteur: " + sector.getName());
      }
    }
  }

  /**
   * Exporte le récapitulatif des tâche qui contient toutes les tâches du système. Un fichier
   * extrêmement utilse pour la vérification et la détection de doublons ou de tâches inexistantes.
   */
  public static void exportTasks() {
    String destinationfile = "Récapitulatif des tâches.csv";
    try {
      OutputStream fisw = new FileOutputStream("export\\" + destinationfile);
      OutputStreamWriter isw = new OutputStreamWriter(fisw, CSV_CHARSET);
      CSVWriter writer = new CSVWriter(isw, ';', '\"', '\\', "\n");
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
    } catch (FileNotFoundException e) {
      throw new RuntimeException("fichier d'export non trouvé pour la synthèse des tâches");
    }
  }

  public static void main(String[] args) {

    JUnitCore junit = new JUnitCore();
    junit.addListener(new TextListener(System.out));

    System.out.println("--Programme de synchronisation des tâches--");

    System.out.println("Fait par Moaad Fattali, respo planning P2020 pour le Raid Centrale Paris");

    System.out.println(
        "Rentrer le chemin du répertoire contenant les planning au format .CSV des secteurs:");

    Scanner scan = new Scanner(System.in);
    String directory = scan.nextLine();

    importTasks(directory);
    int rep;
    List<Integer> reps = new ArrayList<Integer>(Arrays.asList(1, 2));
    do {
      System.out.println("Importation terminée. Exporter les CSV synchronisés?");
      System.out.println("1 - Oui");
      System.out.println("2 - Non");
      rep = scan.nextInt();
    } while (!reps.contains(rep));

    if (rep == 1) {
      syncTasks();
      exportSectors();
      exportTasks();
      System.out.println("Exportation terminée.\n");
      System.err.println(
          "!! Vérifiez soigneusement la présence de doublons, d'incohérences et d'erreurs dans le fichier Récapitulatif des tâches.csv !!");
      System.out.println("Modifiez les CSV de base et recommencez si nécessaire");
    }
    scan.close();
  }

  public static void addSector(Sector sector) {
    sectors.add(sector);
  }
}
