package it.polito.tdp.PremierLeague.model;

public class Arco {
	private Match m1;
	private Match m2;
	private Integer peso;
	public Arco(Match m1, Match m2, Integer peso) {
		super();
		this.m1 = m1;
		this.m2 = m2;
		this.peso = peso;
	}
	public Match getM1() {
		return m1;
	}
	public void setM1(Match m1) {
		this.m1 = m1;
	}
	public Match getM2() {
		return m2;
	}
	public void setM2(Match m2) {
		this.m2 = m2;
	}
	public Integer getPeso() {
		return peso;
	}
	public void setPeso(Integer peso) {
		this.peso = peso;
	}
	
	public String toString() {
		return "("+m1.matchID+")"+" "+m1.teamHomeNAME+" vs "+m1.teamAwayNAME+" - "+"("+m2.matchID+")"+" "+m2.teamHomeNAME+" vs "+m2.teamAwayNAME+" ("+getPeso()+")";
	}
	
}
