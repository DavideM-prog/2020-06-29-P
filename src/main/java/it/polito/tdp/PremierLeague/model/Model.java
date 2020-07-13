package it.polito.tdp.PremierLeague.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	private PremierLeagueDAO dao;
	private Graph <Match,DefaultWeightedEdge> grafo;
	private Map<Integer,Match> idMap;
	private List<Arco> archi;
	
	public Model(){
		this.dao=new PremierLeagueDAO();
	}
	
	public List<Integer> getMesi(){
		return this.dao.getMesi();
	}
	
	public void creaGrafo(Integer min,Integer mese) {
		this.grafo=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.idMap=new HashMap<Integer,Match>();
		
		// aggiungo i vertici
		List<Match> vertici=this.dao.getVertici(mese, idMap);
		Graphs.addAllVertices(this.grafo, vertici);
		
		//aggiungo gli archi
		this.archi=this.dao.getArchi(min, idMap, mese);
		for(Arco a: archi) {
			if(this.grafo.containsVertex(a.getM1()) && this.grafo.containsVertex(a.getM2())) {
				Graphs.addEdge(this.grafo, a.getM1(), a.getM2(), a.getPeso());
			}
		}
		
	}

	public int numeroVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int numeroArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Arco> getConnesioneMax(){
		List <Arco> archiConnMax=new ArrayList<Arco>();
		Integer connMax=-1;
		Arco supporto =null;
		for(Arco a : this.archi) {
			if(a.getPeso()>connMax) {
				connMax=a.getPeso();
				supporto = new Arco(a.getM1(),a.getM2(),a.getPeso());
			}
		}
		for(Arco a : this.archi) {
			if(a.getPeso()==connMax) {
				supporto= new Arco(a.getM1(),a.getM2(),a.getPeso());
				archiConnMax.add(supporto);
			}
		}
		return archiConnMax;
	}
}
