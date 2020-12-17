package net.lepexnetwork.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.dv8tion.jda.api.entities.Role;


public class MySQLManager {
	private static String host = "localhost";

	private static String port = "3306";

	private static String database = "database";

	private static String username = "username";

	private static String password = "password";

	private static Connection con;

	public MySQLManager() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
		if(!isConnect()) {
			connect();
			System.out.println("MySQL have connected");
		}
	}

	public void connect() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	public void disconnect() {
		if (isConnect().booleanValue())
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}  
	}

	public Boolean isConnect() {
		return Boolean.valueOf(!(con == null));
	}

	public boolean isNewServer(String guildID) {
		boolean isNewServer = true;
		if(!isConnect()) {
			connect();
		}
		try {
			String sql = "SELECT guildID FROM server WHERE guildID like ?";
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, guildID);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next())
				isNewServer = false; 
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return isNewServer;
	}

	public String getPrefix(String guildID) {
		String prefix = "";
		if(!isConnect()) {
			connect();
		}
		String sql = "SELECT prefix FROM server WHERE guildID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, guildID);
			statement.executeQuery();
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next())
				prefix = resultSet.getString("prefix"); 
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return prefix;
	}

	public void setPrefix(String guildID, String prefix) {
		if(!isConnect()) {
			connect();
		}
		String sql = "UPDATE server SET prefix = ? WHERE guildID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, prefix);
			statement.setString(2, guildID);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	public void setErrlogChannelID(String guildID, String channelID) {
		if(!isConnect()) {
			connect();
		}
		String sql = "UPDATE server SET logChannelID = ? WHERE guildID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, channelID);
			statement.setString(2, guildID);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	public String getErrlogChannelID(String guildID) {
		String errlogChannelID = "";
		if(!isConnect()) {
			connect();
		}
		String sql = "SELECT logChannelID FROM server WHERE guildID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, guildID);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next())
				errlogChannelID = resultSet.getString("logChannelID"); 
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return errlogChannelID;
	}

	public void setWorking(String guildID, int onOff) {
		if(!isConnect()) {
			connect();
		}
		String sql = "UPDATE server SET isWorking = ? WHERE guildID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setInt(1, onOff);
			statement.setString(2, guildID);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	public boolean isWorking(String guildID) {
		boolean isWorking = false;
		if(!isConnect()) {
			connect();
		}
		String sql = "SELECT isWorking FROM server WHERE guildID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, guildID);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next() && 
					resultSet.getInt("isWorking") != 0)
				isWorking = true; 
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return isWorking;
	}

	public void setAllowChannel(String guildID, String channelID) {
		if(!isConnect()) {
			connect();
		}
		String sql = "UPDATE server SET allowChannelID = ? WHERE guildID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, channelID);
			statement.setString(2, guildID);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	public String getAllowChannel(String guildID) {
		String allowChannelID = "";
		if(!isConnect()) {
			connect();
		}
		String sql = "SELECT allowChannelID FROM server WHERE guildID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, guildID);
			statement.executeQuery();
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next())
				allowChannelID = resultSet.getString("allowChannelID"); 
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return allowChannelID;
	}

	public void setDefRole(String guildID, String roleID) {
		if(!isConnect()) {
			connect();
		}
		String sql = "UPDATE server SET defRole = ? WHERE guildID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, roleID);
			statement.setString(2, guildID);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	public String getDefRole(String guildID) {
		String defRoleID = "";
		connect();
		String sql = "SELECT defRole FROM server WHERE guildID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, guildID);
			statement.executeQuery();
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next())
				defRoleID = resultSet.getString("allowChannelID"); 
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect();
		} 
		return defRoleID;
	}*/

	public void newServer(String serverID, String serverName) {
		if(!isConnect()) {
			connect();
		}
		String sql = "INSERT INTO server (guildID, serverName) VALUES (?,?)";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, serverID);
			statement.setString(2, serverName);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	public int getDisplay(String guildID) {
		int display = 0;
		if(!isConnect()) {
			connect();
		}
		String sql = "SELECT display FROM server WHERE guildID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, guildID);
			statement.executeQuery();
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next())
				display = resultSet.getInt("display"); 
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return display;
	}

	public void setDisplay(String guildID, String display) {
		if(!isConnect()) {
			connect();
		}
		String sql = "UPDATE server SET display = ? WHERE guildID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, display);
			statement.setString(2, guildID);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	public Boolean isNewNicknamedata(String guildId, String memberID) {
		boolean isNew = true;
		if(!isConnect()) {
			connect();
		}
		String sql = "SELECT nickname FROM usernickname WHERE guildID = ? and memberID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, guildId);
			statement.setString(2, memberID);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next())
				isNew = false;
		} catch (SQLException e) {
			e.printStackTrace();
		}  
		return isNew;
	}

	public void addNicknamedata(String serverID, String memberID, String nickname) {
		if(!isConnect()) {
			connect();
		}
		String sql = "INSERT INTO usernickname (guildID, memberID, nickname) VALUES (?,?,?)";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, serverID);
			statement.setString(2, memberID);
			statement.setString(3, nickname);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}  
	}

	public void delNicknamedata(String guildID, String memberID) {
		if(!isConnect()) {
			connect();
		}
		String sql = "DELETE FROM usernickname WHERE guildID = ? and memberID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, guildID);
			statement.setString(2, memberID);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}


	public void updateNicknamedata(String guildID, String memberID, String splittedContentRaw) {
		if(!isConnect()) {
			connect();
		}
		String sql = "UPDATE usernickname SET nickname = ? WHERE guildID = ? and memberID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, splittedContentRaw);
			statement.setString(2, guildID);
			statement.setString(3, memberID);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	public String getNicknamedata(String guildid, String memberid) {
		String nickName = "";
		if(!isConnect()) {
			connect();
		}
		String sql = "SELECT nickname FROM usernickname WHERE guildID = ? and memberid = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, guildid);
			statement.setString(2, memberid);
			statement.executeQuery();
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next())
				nickName = resultSet.getString("nickname"); 
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return nickName;
	}

	public void setCustom(String guildID, String display) {
		if(!isConnect()) {
			connect();
		}
		String sql = "UPDATE server SET custompattern = ? WHERE guildID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, display);
			statement.setString(2, guildID);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	public String getCustomPattern(String guildid) {
		String nickName = "";
		if(!isConnect()) {
			connect();
		}

		String sql = "SELECT custompattern FROM server WHERE guildID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, guildid);
			statement.executeQuery();
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next())
				nickName = resultSet.getString("custompattern"); 
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return nickName;
	}

	public void delServer(String guildID) {
		if(!isConnect()) {
			connect();
		}
		String sql = "DELETE FROM server WHERE guildID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, guildID);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}


	public int getCount(String countingColumn, String table, String useColumn, String serachData) {
		if(!isConnect()) {
			connect();
		}
		String sql = "SELECT COUNT(" + countingColumn +") AS number FROM " + table + " WHERE " + useColumn + " = ?";	

		int intResult = 0;
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, serachData);
			statement.executeQuery();
			ResultSet resultSet = statement.executeQuery();

			String result;
			if (resultSet.next()) {
				result = resultSet.getString("number");
				intResult = Integer.parseInt(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return intResult;
	}

	public String [] getIgnoreGroup(String guildID) {
		if(!isConnect()) {
			connect();
		}
		String roleID [] = null;
		String sql = "SELECT servergroupID FROM ignoreServergroup WHERE guildID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, guildID);
			statement.executeQuery();
			ResultSet resultSet = statement.executeQuery();
			int i = 0;
			roleID = new String[getCount("servergroupID", "ignoreServergroup", "guildID", guildID)];
			while (resultSet.next()) {

				roleID[i] = resultSet.getString("servergroupID");
				i++;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return roleID;
	}

	public void addIgnoreRole(String guildID, String servergroupID) {
		if(!isConnect()) {
			connect();
		}
		String sql = "INSERT INTO ignoreServergroup (guildID, servergroupID) VALUES (?,?)";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, guildID);
			statement.setString(2, servergroupID);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	public void removeIgnoreRole(String guildID, String servergroupID) {
		if(!isConnect()) {
			connect();
		}
		String sql = "DELETE FROM ignoreServergroup WHERE guildID = ? and servergroupID = ?";
		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, guildID);
			statement.setString(2, servergroupID);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

}
