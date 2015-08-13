package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import beans.GroupBean;
import config.ConnectionManager;

public class GroupsDAO {
	
	public static ArrayList<GroupBean> findAll() {
		String query = "SELECT * FROM groups";
		ArrayList<GroupBean> groups = new ArrayList<>();
		
		ConnectionManager conM = new ConnectionManager();
		Connection con = conM.getConnection();
		
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				String groupName = rs.getString("groupName");
				GroupBean group = new GroupBean(id, groupName);
				groups.add(group);
			}
        } catch (SQLException e) {
	        System.out.println(e.getMessage());
        }
		return groups;
	}
	
	@SuppressWarnings("finally")
	public static ArrayList<String> findGroups(String groupName) {
		String query = "SELECT DISTINCT groupName FROM groups WHERE groupName LIKE ?";
		
		ConnectionManager conM = new ConnectionManager();
		Connection con = conM.getConnection();
		ResultSet rs = null;

		ArrayList<String> groups = null;

		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setString(1, "%" + groupName.trim() + "%");
			rs = stmt.executeQuery();

			groups = new ArrayList<>();

			while (rs.next()) {
				groups.add(rs.getString("groupName"));
			}
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		finally {
			return groups;
		}
	}
	
	public static ArrayList<String> findAssignedGroups(String groupName, int subjectId) {
		String query = "SELECT DISTINCT groupName FROM groups g INNER JOIN stgrelations s ON g.id = s.groupId"
				+ " WHERE s.subjectId = ? AND g.groupName LIKE ?";
		
		ConnectionManager conM = new ConnectionManager();
		Connection con = conM.getConnection();
		ResultSet rs = null;
		ArrayList<String> groups = null;
		
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setInt(1, subjectId);
			stmt.setString(2, "%" + groupName.trim() + "%");
			
			rs = stmt.executeQuery();
			
			groups = new ArrayList<>();

			while (rs.next()) {
				groups.add(rs.getString("groupName"));
			}
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
        }
		return groups;
	}
	
	@SuppressWarnings("finally")
    public static GroupBean find(int id) {
		String query = "SELECT * FROM groups WHERE id = ?";
		
		ConnectionManager conM = new ConnectionManager();
		Connection con = conM.getConnection();
		
		GroupBean group = null;
		
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				String groupName = rs.getString("groupName");
				group = new GroupBean(id, groupName);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
        } finally {
        	return group;
        }
	}
	
	@SuppressWarnings("finally")
    public static GroupBean find(String groupName) {
		
		String query = "SELECT * FROM groups WHERE groupName = ?";
		
		ConnectionManager conM = new ConnectionManager();
		Connection con = conM.getConnection();
		
		GroupBean group = null;
		
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setString(1, groupName);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				int id = rs.getInt("id");
				group = new GroupBean(id, groupName);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
        } finally {
        	return group;
        }
	}
	
	public static String groupValidate (String groupName) {
		String query = "SELECT * FROM groups WHERE groupName = ?";
		
		ConnectionManager conM = new ConnectionManager();
		Connection con = conM.getConnection();
		ResultSet rs = null;
		String errorMessage = null;
		
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setString(1, groupName);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				errorMessage = "This group is already exist!";
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
        }
		return errorMessage;
	}
	
	@SuppressWarnings("finally")
    public static boolean insert (String groupName) {
		String query = "INSERT INTO groups " 
					+ "(groupName) "
					+ "VALUES (?)";
		
		ConnectionManager conM = new ConnectionManager();
		Connection con = conM.getConnection();
		int rowsAffected = 0;
		
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setString(1, groupName);
			rowsAffected = stmt.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
        } finally {
			return rowsAffected > 0;
		}
	}
	
	@SuppressWarnings("finally")
    public static boolean delete (int id) {
		String query = "DELETE FROM groups WHERE id = ?";
		
		ConnectionManager conM = new ConnectionManager();
		Connection con = conM.getConnection();
		int rowsAffected = 0;
		
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setInt(1, id);
			rowsAffected = stmt.executeUpdate();
		
		} catch (SQLException e) {
			System.out.println(e.getMessage());
        } finally {
			return rowsAffected > 0;
		}
	}
	
	@SuppressWarnings("finally")
    public static boolean update (GroupBean group) {
		String query = "UPDATE groups SET groupName = ? WHERE id = ?";
		
		ConnectionManager conM = new ConnectionManager();
		Connection con = conM.getConnection();
		int rowsAffected = 0;
		
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setString(1, group.getGroupName());
			stmt.setInt(2, group.getId());
			rowsAffected = stmt.executeUpdate();
		
		} catch (SQLException e) {
			System.out.println(e.getMessage());
        } finally {
			return rowsAffected > 0;
		}
	}
	
	public static HashMap<Integer, String> getGroupsMap () {
		String query = "SELECT * FROM groups";
		
		HashMap<Integer, String> groupsMap = null;
		
		ConnectionManager conM = new ConnectionManager();
		Connection con = conM.getConnection();
		ResultSet rs = null;
		
		try (Statement stmnt = con.createStatement()) {
	        rs = stmnt.executeQuery(query);
	        groupsMap = new HashMap<>();
	        
	        while (rs.next()) {
	        	int groupId = rs.getInt("id");
	        	String groupName = rs.getString("groupName");
	        	groupsMap.put(groupId, groupName);
	        }
		} catch (SQLException e) {
        	System.out.println(e.getMessage());
        }
		
		return groupsMap;
	}
	
	public static boolean shareSubject(int userId, int subjectId, String groups) {
		String[] groupNames = groups.split(",");
		
		if (!groupNames[0].equals("")) {
			ArrayList<Integer> groupsId = findGroupsId(groupNames);
			deleteRelations(userId, subjectId);
			String insertQuery = "INSERT INTO stgrelations "
					+ "(teacherId, subjectId, groupId) VALUES";
			
			ConnectionManager conM = new ConnectionManager();
			Connection con = conM.getConnection();
			int rowsAffected = 0;
				
			for (int i = 0; i < groupsId.size(); i++) {
				insertQuery += " (?,?,?)";
				if (i != groupsId.size() - 1) {
					insertQuery += ",";
				}
			}
				
				try (PreparedStatement stmt = con.prepareStatement(insertQuery)) {
					int i = 0;
					for (int groupId : groupsId) {
						stmt.setInt(i + 1, userId);
						stmt.setInt(i + 2, subjectId);
						stmt.setInt(i + 3, groupId);
						i += 3;
					}
					rowsAffected = stmt.executeUpdate();
				} catch (SQLException e) {
			        System.out.println(e.getMessage());
		        }
				return rowsAffected > 0;
		}
		return false;
	}
	
	@SuppressWarnings("finally")
    public static ArrayList<Integer> findGroupsId(String[] groupsName) {
		String query = "SELECT id FROM groups WHERE groupName IN (";

		ArrayList<Integer> groupIds = new ArrayList<>();
		int length = groupsName.length;
		
		for (int i = 0; i < length; i++) {
			query += "?,";
		}
		query = query.substring(0, query.length() - 1) + ")";
		
		ConnectionManager conM = new ConnectionManager();
		Connection con = conM.getConnection();
		ResultSet rs = null;	
			
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			for (int i = 0; i < length; i++) {
				stmt.setString(i + 1, groupsName[i]);
			}
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				groupIds.add(rs.getInt("id"));
			}
			
		} catch (SQLException e) {
	        System.out.println(e.getMessage());
        } finally {
        	return groupIds;
        }
	}
	
	@SuppressWarnings("finally")
    public static boolean deleteRelations(int userId, int subjectId) {
		String selectQuery = "DELETE FROM stgrelations WHERE teacherId=? AND subjectId=?";
		
		ConnectionManager conM = new ConnectionManager();
		Connection con = conM.getConnection();
		int rowsAffected = 0;
		
		try (PreparedStatement stmt = con.prepareStatement(selectQuery)) {
			stmt.setInt(1, userId);
			stmt.setInt(2, subjectId);
			
			rowsAffected = stmt.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			return rowsAffected > 0;
		}
		
	}

	/**
	 * Get all groups attached to teacherId and showed relations between groups as String and subjectId as Integer in HashMap.
	 * 
	 * @param teacherId
	 * @return
	 */
	public static HashMap<String, String> getGroupsByTeacher(int teacherId) {
		String selectQuery = "SELECT subjectName, GROUP_CONCAT(groupName) as groupsStr "
				+ "FROM stgrelations r "
				+ "INNER JOIN subjects s ON s.id = r.subjectId "
				+ "INNER JOIN groups g ON g.id = r.groupId "
				+ "WHERE teacherId=? "
				+ "GROUP BY subjectId ";

		ConnectionManager conM = new ConnectionManager();
		Connection con = conM.getConnection();
		ResultSet rs = null;

		HashMap<String, String> groups = new HashMap<>();

		try (PreparedStatement stmt = con.prepareStatement(selectQuery)) {
			stmt.setInt(1, teacherId);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				String subjectName = rs.getString("subjectName");
				String groupsStr = rs.getString("groupsStr");
				groups.put(subjectName, groupsStr);
			}
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} 
		
		return groups;
	}
	
	@SuppressWarnings("finally")
    public static boolean appendToReady(int testId, int groupId) {
		
		ConnectionManager conM = new ConnectionManager();
		Connection con = conM.getConnection();
		ResultSet rs = null;
		int rowsAffected = 0;
		
		ArrayList<Integer> studentsId = new ArrayList<>();
		
		String selectQuery = "SELECT id FROM users WHERE role = 0 AND groupId = ?";
		
		try (PreparedStatement stmt = con.prepareStatement(selectQuery)) {
			stmt.setInt(1, groupId);
			
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				studentsId.add(rs.getInt("id"));
			}
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
        }
		
		String insertQuery = "INSERT INTO ready_students (testId, studentId, groupId) "
				+ "VALUES ";
		
		int listSize = studentsId.size();
		
		for (int i = 0; i < listSize; i++) {
			insertQuery += "(?, ?, ?)";
			if (i != listSize - 1) {
				insertQuery += ", ";
			}
		}
		
		try (PreparedStatement stmt = con.prepareStatement(insertQuery)) {
			int k = 1;
			for (int i = 0; i < listSize; i++) {
				stmt.setInt(k, testId);
				stmt.setInt(k + 1, studentsId.get(i));
				stmt.setInt(k + 2, groupId);
				k += 3;
			}
			rowsAffected = stmt.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			return rowsAffected > 0;
		}
	}
	
	
}
