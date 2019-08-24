package core;

import java.util.ArrayList;

//import java.util.*;

public class Task {
	private Date start;
	private Date finish;
	private String name;
	private ArrayList<Sector> listOfSectors = new ArrayList<Sector>();
	private String resources;

	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getFinish() {
		return finish;
	}
	public void setFinish(Date finish) {
		this.finish = finish;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getResources() {
		return resources;
	}
	public void setResources(String resources) {
		this.resources = resources;
	}
	public ArrayList<Sector> getListOfSectors() {
		return listOfSectors;
	}
	public void addSector(Sector sector) {
		listOfSectors.add(sector);
	}

	public Task(int yearS, int monthS, int dayOfMonthS,int yearF, int monthF, int dayOfMonthF) {
		this.start=new Date(yearS, monthS, dayOfMonthS);
		this.finish=new Date(yearF, monthF, dayOfMonthF);
	}
	public Task(String name, int yearS, int monthS, int dayOfMonthS,int yearF, int monthF, int dayOfMonthF) {
		this.start=new Date(yearS, monthS, dayOfMonthS);
		this.finish=new Date(yearF, monthF, dayOfMonthF);
		this.name=name;
	}
	public Task(String name, int yearS, int monthS, int dayOfMonthS,int yearF, int monthF, int dayOfMonthF, Sector sector) {
		this.start=new Date(yearS, monthS, dayOfMonthS);
		this.finish=new Date(yearF, monthF, dayOfMonthF);
		this.name=name;
		sector.addTask(this);
		this.addSector(sector);
	}

	public Task(Date start, Date finish) {
		this.start=start;
		this.finish=finish;
	}

	public Task(String name) {
		this.name=name;
	}


	public String toString() {
		return "Nom: "+name+". Début: "+start+". Fin: "+finish;
	}

    /**
     * 
     */
	public static boolean equalstr(String a, String b) {
		String lowerA = a.toLowerCase();
		String lowerB = b.toLowerCase();
		
		String[] aTable = lowerA.split(" ");
		String[] bTable = lowerB.split(" ");
		
		lowerA=lowerB=null;
		for (int i = 0; i < bTable.length; i++) {
			lowerB=lowerB+bTable[i];
			}
		for (int i = 0; i < aTable.length; i++) {
			lowerA=lowerA+aTable[i];
			}
		return lowerA.equals(lowerB);
	}

	/**
	 * On considère que deux tâches sont égales si elles ont même nom (sans prendre en compte la casse ni les espaces)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!equalstr(name,other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * Renvoie la ligne correspondant à la tâche telle qu'elle doit figurer sur le CSV secteur
	 * Attention le programme ne prend en compte que des tâches de niveau 2 pour l'instant (de toute façon les tâches de niveau 3 ça sert à rien)
	 * @return
	 */
	public String[] toCSV() {
		String[] res= {this.name,this.start.toString(),this.finish.toString(),"2",this.getResources()};
		return res;
	}

}
