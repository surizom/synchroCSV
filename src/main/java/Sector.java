import java.util.ArrayList;
import java.util.List;

public class Sector {
  private String name;
  private List<Task> listOfTasks = new ArrayList<Task>();

  public Sector(String name) {
    this.name = name;
    Synchronizer.addSector(this);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Task> getListOfTasks() {
    return listOfTasks;
  }

  public void addTask(Task task) {
    this.listOfTasks.add(task);
  }

  public String[] getHeader() {
    String[] header = {this.name, null, null, "1"};
    return header;
  }

  public String toString() {
    return this.name + ". Nombre de tâches: " + this.listOfTasks.size();
  }
}
