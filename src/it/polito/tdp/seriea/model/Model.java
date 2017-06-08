package it.polito.tdp.seriea.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	private Map <Integer, Season> mappaStagioni ;
	private Map <String, Team> mappaTeams ;
	private List <Match> matches;
	private DirectedWeightedMultigraph <Team, DefaultWeightedEdge> grafo ;
	private Set<DefaultWeightedEdge> usedEdges ;

	
	public Model(){
		dao  = new SerieADAO();
		mappaStagioni = new TreeMap <Integer, Season>();
		mappaTeams = new TreeMap <String, Team>();
		stagioni = dao.listSeasons(mappaStagioni);
		squadre = dao.listTeams(mappaTeams);
	}
	
	public List <Season> getStagioni(){
		if( stagioni == null){
			stagioni = dao.listSeasons(mappaStagioni);
		}
		return stagioni;
	}
	
	public List <Team> getTeams(){
		if( squadre == null){
			squadre = dao.listTeams(mappaTeams);
		}
		return squadre;
	}

	
	public void creaGrafo(Season stagione) {
		
		// Inizializzo il grafo
		this.grafo = new DirectedWeightedMultigraph<Team, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		// Ottengo i matches di questa stagione dal database
		matches = dao.getMatches(stagione, mappaTeams);
		
		// Carico i vertici
		Graphs.addAllVertices(grafo, dao.getTeamPerStagione(stagione.getSeason())) ;
		
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


	public List<Team> getClassifica(Season stagione) {
		this.creaGrafo(stagione);
		
		for(Team t : grafo.vertexSet()){
			
			// considero partite in casa (archi uscenti)
			for( DefaultWeightedEdge arco: grafo.outgoingEdgesOf(t)){
				
				
				// partita vinta
				if( grafo.getEdgeWeight(arco) == 1){
					t.addPunteggio(3);
				}
				// partita persa
				if( grafo.getEdgeWeight(arco) == -1){
					t.addPunteggio(0);
				}
				// pareggio
				if( grafo.getEdgeWeight(arco) == 0){
					t.addPunteggio(1);
				}	
			}
			
			// considero partite fuori casa
			for( DefaultWeightedEdge arco: grafo.incomingEdgesOf(t)){
				// partita vinta
				if( grafo.getEdgeWeight(arco) == -1){
					t.addPunteggio(3);
				}
				// partita persa
				if( grafo.getEdgeWeight(arco) == 1){
					t.addPunteggio(0);
				}
				// pareggio
				if( grafo.getEdgeWeight(arco) == 0){
					t.addPunteggio(1);
				}	
			}
		}
		List<Team> classifiche = new ArrayList<Team>(grafo.vertexSet());
		Collections.sort(classifiche);
		return classifiche;	

	}

	public List <Team> getSequenza(Season stagione){
		
		List <Team> parziale = new ArrayList <Team>();
		List <Team> best = new ArrayList <Team>();
		this.usedEdges = new HashSet<>() ;;
		
		/***ATTENZIONE***/
		/**
		 * Elimina dei vertici dal grafo per renderlo
		 * gestibile dalla ricorsione.
		 * Nella soluzione "vera" questa istruzione va rimossa
		 * (però l'algoritmo non termina in tempi umani).
		 */
		this.riduciGrafo(8);
		
		for(Team initial : grafo.vertexSet()) {
			parziale.add(initial) ;
			this.recursive(1, initial, parziale, best) ;
			parziale.remove(initial) ;
		}

		return best;

	}
	

	public void recursive(int step, Team iniziale, List<Team> parziale , List <Team> best){
		
		if(parziale.size() > best.size()){
			best.clear();
			best.addAll(parziale);	
		}
		
		for(DefaultWeightedEdge arco : grafo.outgoingEdgesOf(iniziale)){
			Team perdente = grafo.getEdgeTarget(arco);
			if(grafo.getEdgeWeight(arco) == 1.0 && !this.usedEdges.contains(arco)){
				parziale.add(perdente);
				usedEdges.add(arco);
				recursive ( step++, perdente,parziale, best );
				usedEdges.remove(perdente);
				parziale.remove(parziale.size() -1);  
				// Attenzione: parziale.remove(perdente) non funziona perché perdente può comparire più di una volta
			}	
		}
			
	}
	
	
	/**
	 * cancella dei vertici dal grafo in modo che la sua dimensione
	 * sia solamente pari a {@code dim} vertici
	 * @param dim
	 */
	private void riduciGrafo(int dim) {
		HashSet<Team> togliere = new HashSet<Team>() ;
		
		Iterator<Team> iter = grafo.vertexSet().iterator() ;
		for(int i=0; i<grafo.vertexSet().size()-dim; i++) {
			togliere.add(iter.next()) ;
		}
		grafo.removeAllVertices(togliere) ;
		System.err.println("Attenzione: cancello dei vertici dal grafo");
		System.err.println("Vertici rimasti: "+grafo.vertexSet().size()+"\n");
	}

}
