package it.polito.tdp.seriea.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgrapht.Graphs;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	
	private SerieADAO dao;
	private List <Season> stagioni;
	private List <Team> squadre;
	private Map <Integer, Season> mappaStagioni = new TreeMap <Integer, Season>();
	private List <Match> matches;
	private DirectedWeightedMultigraph <Team, DefaultWeightedEdge> grafo ;
	
	public Model(){
		dao  = new SerieADAO();
	}
	
	public List <Season> getStagioni(){
		if( stagioni == null){
			stagioni = dao.listSeasons();
			
			getStagioni();
			for( Season s : stagioni){
				mappaStagioni.put(s.getSeason(), s);   
			}
		}
		return stagioni;
	}
	
	public List <Team> getTeams(){
		if( squadre == null){
			squadre = dao.listTeams();
		}
		return squadre;
	}

	public List<Team> getClassifica(Season stagione) {
		int idStagione = stagione.getSeason();
		this.creaGrafo(idStagione);
		
		for(Team t : grafo.vertexSet()){
			
			// considero partite in casa (archi uscenti)
			for( DefaultWeightedEdge arco: grafo.outgoingEdgesOf(t)){
				
				
				// partita vinta
				if( grafo.getEdgeWeight(arco) == 1.0){
					t.addPunteggio(3);
				}
				// partita persa
				if( grafo.getEdgeWeight(arco) == -1.0){
					t.addPunteggio(0);
				}
				// pareggio
				if( grafo.getEdgeWeight(arco) == 0.0){
					t.addPunteggio(1);
				}	
			}
			
			// considero partite fuori casa
			for( DefaultWeightedEdge arco: grafo.incomingEdgesOf(t)){
				// partita vinta
				if( grafo.getEdgeWeight(arco) == -1.0){
					t.addPunteggio(3);
				}
				// partita persa
				if( grafo.getEdgeWeight(arco) == 1.0){
					t.addPunteggio(0);
				}
				// pareggio
				if( grafo.getEdgeWeight(arco) == 0.0){
					t.addPunteggio(1);
				}	
			}
		}
		List<Team> classifiche = new ArrayList<Team>(grafo.vertexSet());
		Collections.sort(classifiche);
		return classifiche;	

	}

	private void creaGrafo(int idStagione) {
		
		// Inizializzo il grafo
		this.grafo = new DirectedWeightedMultigraph<Team, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		// Ottengo i matches di questa stagione dal database
		this.getStagioni();
		matches = dao.getMatches(idStagione, mappaStagioni, this.getTeams());
		
		// Carico i vertici
		Graphs.addAllVertices(grafo, dao.getTeamPerStagione(idStagione)) ;
		
		// Aggiungo gli archi
		for( Match m : matches){
			// creo arco
			DefaultWeightedEdge arco = grafo.addEdge(m.getHomeTeam(), m.getAwayTeam()) ;
			// imposto il peso
			if(m.getFthg() > m.getFtag()){
				grafo.setEdgeWeight(arco, 1 );
			}
			if(m.getFthg() < m.getFtag()){
				grafo.setEdgeWeight(arco, -1 );
			}
			if(m.getFthg() == m.getFtag()){
				grafo.setEdgeWeight(arco, 0 );
			}
		}	
	}

}
