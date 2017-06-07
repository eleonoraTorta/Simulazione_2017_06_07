package it.polito.tdp.seriea.db;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;

public class TestSerieADAO {

	public static void main(String[] args) {
		SerieADAO dao = new SerieADAO() ;
		
		List<Season> seasons = dao.listSeasons() ;
		Map <Integer, Season> stagioni = new TreeMap <Integer, Season>();
		for( Season s : seasons){
			stagioni.put(s.getSeason(), s);   
		}
		System.out.println(seasons);
		
		List<Team> teams = dao.listTeams() ;
		System.out.println(teams);
		
		System.out.println(dao.getMatches(2003, stagioni, teams));

		System.out.println(dao.getTeamPerStagione(2003));


	}

}
