package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.polito.tdp.seriea.model.Match;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;

public class SerieADAO {
	
	public List<Season> listSeasons(Map <Integer, Season> stagioni) {
		String sql = "SELECT season, description FROM seasons" ;
		
		List<Season> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				Season stagione = new Season(res.getInt("season"), res.getString("description")) ;
				result.add(stagione) ;
				stagioni.put(stagione.getSeason(), stagione);
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Team> listTeams(Map <String, Team> teams) {
		String sql = "SELECT team FROM teams" ;
		
		List<Team> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				Team team = new Team(res.getString("team")) ;
				result.add( team) ;
				teams.put(team.getTeam(), team);
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
		
		public List<Match> getMatches(Season stagione,  Map <String,Team> squadre) {
			String sql = "SELECT * "+
						"FROM matches " +
						"WHERE season = ?" ;
			
			List<Match> result = new ArrayList<>() ;
			
			Connection conn = DBConnect.getConnection() ;
			
			try {
				PreparedStatement st = conn.prepareStatement(sql) ;
				st.setInt(1, stagione.getSeason());
				
				ResultSet res = st.executeQuery() ;
				
				while(res.next()) {
					
					int id = res.getInt("match_id");
					LocalDate data = res.getDate("Date").toLocalDate();
					Team home = squadre.get(res.getString("HomeTeam"));
					Team away = squadre.get(res.getString("AwayTeam"));
					int goalH = res.getInt("FTHG");
					int goalA = res.getInt("FTAG");
					String ris = res.getString("FTR");
					
					Match match = new Match(id, stagione, null,data,home,away,goalH, goalA, ris);
					result.add(match);
				}
					
				
				conn.close();
				return result ;

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null ;
			}
		}
		
		public List<Team> getTeamPerStagione(int idStagione) {
			String sql = "SELECT DISTINCT HomeTeam " +
						"FROM matches " +
						"WHERE season = ?" ;
			
			List<Team> result = new ArrayList<>() ;
			Connection conn = DBConnect.getConnection() ;
			
			try {
				PreparedStatement st = conn.prepareStatement(sql) ;
				st.setInt(1, idStagione);
				ResultSet res = st.executeQuery() ;
				
				while(res.next()) {
					result.add( new Team(res.getString("HomeTeam"))) ;
				}
				
				conn.close();
				return result ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null ;
			}
		}
		
	}


