package it.polito.tdp.seriea.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	
	private SerieADAO dao;
	private List <Season> stagioni;
	private List <Team> squadre;
	private Map <Integer, Season> mappaStagioni ;
	private Map <String, Team> mappaTeams ;
	private List <Match> matches;
	private SimpleDirectedWeightedGraph <Team, DefaultWeightedEdge> grafo ;
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
		this.grafo = new SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		// Ottengo i matches di questa stagione dal database
		matches = dao.getMatches(stagione, mappaTeams);
		
		// Carico i vertici
		Graphs.addAllVertices(grafo, dao.getTeamPerStagione(stagione.getSeason(), mappaTeams)) ;
		// alternativamente posso iterare sui match, il meodo addVertex assicura di non aggiungere duplicati al grafo (non sono necessari controlli)
		// for (Match m : matches) {
		// 	 graph.addVertex(m.getHomeTeam());
		// 	 graph.addVertex(m.getAwayTeam());
		
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


	// METODO 1 : itero sui vertici
	public List<Team> getClassifica(Season stagione) {
		this.creaGrafo(stagione);
		
		// azzero i punteggi
		for (Team t : grafo.vertexSet()){
				t.azzeraPunti();
		}
		
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
		// alternativamente, anziche implementare con comparable un metodo  -(this - altro)
		// posso usare reverseOrder() che inverte l'ordinamente naturale
		// Collections.sort(classifiche, Comparator.reverseOrder());
		
		return classifiche;	

	}
	
	// METODO 2 : itero sugli archi
	public List<Team> getClassifica2() {
		// azzero i punteggi
		for (Team t : grafo.vertexSet())
			t.azzeraPunti();

		// considero ogni partita
		for (DefaultWeightedEdge e : grafo.edgeSet()) {
			Team home = grafo.getEdgeSource(e);
			Team away = grafo.getEdgeTarget(e);
			switch ((int) grafo.getEdgeWeight(e)) {
			case +1:
//				home.setPunti(home.getPunteggio() + 3);
				break;
			case -1:
//				away.setPunti(away.getPunteggio() + 3);
				break;
			case 0:
//				home.setPunti(home.getPunteggio() + 1);
//				away.setPunti(away.getPunteggio() + 1);
				break;
			}
		}

		List<Team> classifica = new ArrayList<Team>(grafo.vertexSet());
		Collections.sort(classifica, new Comparator<Team>() {

			@Override
			public int compare(Team o1, Team o2) {
				return -(o1.getPunteggio() - o2.getPunteggio());
			}
		});

		return classifica;
	}

	public List <Team> getSequenza(Season stagione){
		
		List <Team> parziale = new ArrayList <Team>();
		List <Team> best = new ArrayList <Team>();
		this.usedEdges = new HashSet<>() ;
		
		
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
			// oppure this.best = new ArrayList<>(parziale)
			// ma NON this.best = parziale
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
