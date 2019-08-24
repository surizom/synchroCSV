public class Date implements Comparable<Date> {

  private final static String SEPARATOR = "/";

  private int day;
  private int month;
  private int year;

  Date(int day, int month, int year) {
    super();
    this.day = day;
    this.month = month;
    this.year = year;
  }

  public int getDay() {
    return day;
  }

  public void setDay(int day) {
    this.day = day;
  }

  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public int approxNumberOfDays() {
    return this.day + 30 * this.month + 365 * this.year;
  }

  public int compareTo(Date arg0) {
    return this.approxNumberOfDays() - arg0.approxNumberOfDays();
  }

  @Override
  public String toString() {
    return this.day + SEPARATOR + this.month + SEPARATOR + this.year;
  }
}
