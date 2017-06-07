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
	
	public List<Season> listSeasons() {
		String sql = "SELECT season, description FROM seasons" ;
		
		List<Season> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				result.add( new Season(res.getInt("season"), res.getString("description"))) ;
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Team> listTeams() {
		String sql = "SELECT team FROM teams" ;
		
		List<Team> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				result.add( new Team(res.getString("team"))) ;
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
		
		public List<Match> getMatches(int idStagione, Map<Integer, Season> stagioni, List<Team> squadre) {
			String sql = "SELECT * "+
						"FROM matches " +
						"WHERE season = ?" ;
			
			List<Match> result = new ArrayList<>() ;
			
			Connection conn = DBConnect.getConnection() ;
			
			try {
				PreparedStatement st = conn.prepareStatement(sql) ;
				st.setInt(1, idStagione);
				
				ResultSet res = st.executeQuery() ;
				Team home = null;
				Team away = null;
				
				while(res.next()) {
					
					int id = res.getInt("match_id");
					Season stagione = stagioni.get(res.getInt("Season"));
					LocalDate data = res.getDate("Date").toLocalDate();
					for( Team t : squadre){
						if( t.getTeam().equals(res.getString("HomeTeam"))){
							home = t;
						}
						if( t.getTeam().equals(res.getString("AwayTeam"))){
							away = t;
						}
					}
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


