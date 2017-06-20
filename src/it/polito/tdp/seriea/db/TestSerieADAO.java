package it.polito.tdp.seriea.db;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;

public class TestSerieADAO {

	public static void main(String[] args) {
		SerieADAO dao = new SerieADAO() ;
	
		Map <Integer, Season> stagioni = new TreeMap <Integer, Season>();
		List<Season> seasons = dao.listSeasons(stagioni) ;
		System.out.println(seasons);
		
		Map <String, Team> squadre = new TreeMap <String, Team>();
		List<Team> teams = dao.listTeams(squadre) ;
		System.out.println(teams);
		
		System.out.println(dao.getMatches( stagioni.get(2003),squadre));

		System.out.println(dao.getTeamPerStagione(2003, squadre));

	}

}
