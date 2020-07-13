package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Arco;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Match> listAllMatches(){
		String sql = "SELECT m.MatchID, m.TeamHomeID, m.TeamAwayID, m.teamHomeFormation, m.teamAwayFormation, m.resultOfTeamHome, m.date, t1.Name, t2.Name   "
				+ "FROM Matches m, Teams t1, Teams t2 "
				+ "WHERE m.TeamHomeID = t1.TeamID AND m.TeamAwayID = t2.TeamID";
		List<Match> result = new ArrayList<Match>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				
				Match match = new Match(res.getInt("m.MatchID"), res.getInt("m.TeamHomeID"), res.getInt("m.TeamAwayID"), res.getInt("m.teamHomeFormation"), 
							res.getInt("m.teamAwayFormation"),res.getInt("m.resultOfTeamHome"), res.getTimestamp("m.date").toLocalDateTime(), res.getString("t1.Name"),res.getString("t2.Name"));
				
				
				result.add(match);

			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Integer> getMesi(){
		String sql="SELECT DISTINCT (MONTH(DATE)) AS mese FROM matches ORDER BY mese";
		List<Integer> mesi = new ArrayList<Integer>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				mesi.add(res.getInt("mese"));

			}
			conn.close();
			return mesi;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Match> getVertici(Integer mese,Map<Integer,Match> idMap){
		String sql="SELECT m.MatchID,m.TeamHomeID,m.TeamAwayID,m.TeamHomeFormation,m.TeamAwayFormation,m.ResultOfTeamHome,t.Name,t2.Name,m.Date "
				+ "FROM teams t,teams t2,matches m "
				+ "WHERE t.TeamID=m.TeamHomeID "
				+ "AND t2.TeamID=m.TeamAwayID "
				+ "AND t.TeamID <> t2.TeamID "
				+ "AND MONTH(DATE)= ? ";
		List<Match> result = new ArrayList<Match>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, mese);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				if(!idMap.containsKey(res.getInt("m.MatchID"))) {
				
					Match match = new Match(res.getInt("m.MatchID"), res.getInt("m.TeamHomeID"), res.getInt("m.TeamAwayID"), res.getInt("m.TeamHomeFormation"), 
								res.getInt("m.TeamAwayFormation"),res.getInt("m.ResultOfTeamHome"), res.getTimestamp("m.Date").toLocalDateTime(), res.getString("t.Name"),res.getString("t2.Name"));
					idMap.put(match.getMatchID(),match);
					result.add(match);
				}
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Arco> getArchi(Integer min,Map<Integer,Match>idMap,Integer mese){
		String sql="SELECT m1.MatchID AS v1,m2.MatchID AS v2,COUNT(DISTINCT(a1.PlayerID)) AS peso "
				+ "FROM matches m1, matches m2, actions a1, actions a2 "
				+ "WHERE m1.MatchID > m2.MatchID "
				+ "AND m1.MatchID = a1.MatchID "
				+ "AND m2.MatchID = a2.MatchID "
				+ "AND a1.PlayerID = a2.PlayerID "
				+ "AND a1.TimePlayed >= ? "
				+ "AND a2.TimePlayed >= ? "
				+ "AND MONTH(m1.Date)= ? "
				+ "AND MONTH(m2.Date)= ? "
				+ "GROUP BY m1.MatchID,m2.MatchID";
		List<Arco> archi = new ArrayList<Arco>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, min);
			st.setInt(2, min);
			st.setInt(3, mese);
			st.setInt(4, mese);
			
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				if(idMap.containsKey(res.getInt("v1")) && idMap.containsKey(res.getInt("v2"))) {
				
					Match m1= idMap.get(res.getInt("v1"));
					Match m2= idMap.get(res.getInt("v2"));
					Integer peso= res.getInt("peso");
					Arco a= new Arco(m1,m2,peso);
					archi.add(a);
				}
			}
			conn.close();
			return archi;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
