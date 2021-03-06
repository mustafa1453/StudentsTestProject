package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import beans.CommentsBean;
import beans.UserBean;
import config.ConnectionManager;

public class CommentsDAO {

	/**
	 * Return commets which are related to some practical or lecture
	 */
	public static ArrayList<Object[]> findByOwner(int ownerId, String type) {
		String query = "SELECT * FROM comments c "
				+ "INNER JOIN users u ON u.id = c.author "
				+ "WHERE owner_id = ? AND owner_type = ? "
				+ "ORDER BY date";
		ArrayList<Object[]> comments = new ArrayList<>();
		
		ConnectionManager conM = new ConnectionManager();
		Connection con = conM.getConnection();
		
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setInt(1, ownerId);
			stmt.setString(2, type);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				CommentsBean comment = new CommentsBean();
				comment.setCid(rs.getInt("cid"));
				comment.setAuthor(rs.getInt("author"));
				comment.setOwnerId(rs.getInt("owner_id"));
				comment.setDate(rs.getLong("date"));
				comment.setTitle(rs.getString("title"));
				comment.setBody(rs.getString("body"));
				comment.setOwnerType(rs.getString("owner_type"));
				
				UserBean author = new UserBean();
				author.setId(rs.getInt("id"));
				author.setAvatar(rs.getString("avatar_name"));
				author.setFirstName(rs.getString("first_name"));
				author.setLastName(rs.getString("last_name"));
				author.setUserName(rs.getString("user_name"));

				Object[] pack = {author, comment};
				comments.add(pack);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return comments;
	}

	/**
	 * Check if exists comment with provided cid and uid
	 */
	public static boolean checkByCidAuthor(int cid, int uid) {
		String query = "SELECT cid FROM comments c "
				+ "INNER JOIN users u ON u.id = c.author "
				+ "WHERE cid = ? AND author = ? "
				+ "ORDER BY date";
		
		ConnectionManager conM = new ConnectionManager();
		Connection con = conM.getConnection();

		boolean result = false;
		
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setInt(1, cid);
			stmt.setInt(2, uid);
			ResultSet rs = stmt.executeQuery();

			result = rs.next();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return result;
	}

	/**
	 * Insert new comment to db.
	 */
	public static boolean insert (CommentsBean comment) {
		String query = "INSERT INTO comments " 
					+ "(author, owner_id, date, title, body, owner_type) "
					+ "VALUES (?, ?, ?, ?, ?, ?)";

		ConnectionManager conM = new ConnectionManager();
		Connection con = conM.getConnection();
		int rowsAffected = 0;
		
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setInt(1, comment.getAuthor());
			stmt.setInt(2, comment.getOwnerId());
			stmt.setLong(3, comment.getDate());
			stmt.setString(4, comment.getTitle());
			stmt.setString(5, comment.getBody());
			stmt.setString(6, comment.getOwnerType());

			rowsAffected = stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return rowsAffected > 0;
	}

	/**
	 * Delete comment from db.
	 */
	public static boolean delete (int cid) {
		String query = "DELETE FROM comments WHERE cid = ?";
		
		ConnectionManager conM = new ConnectionManager();
		Connection con = conM.getConnection();
		int rowsAffected = 0;
		
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setInt(1, cid);
			rowsAffected = stmt.executeUpdate();
		
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return rowsAffected > 0;
	}

	/**
	 * Edit comment.
	 */
	public static boolean update(CommentsBean bean) {
		String query = "UPDATE comments SET title=?, body=? WHERE cid = ?";

		ConnectionManager conM = new ConnectionManager();
		Connection con = conM.getConnection();
		int rowsAffected = 0;
		try (PreparedStatement updateStmt = con.prepareStatement(query)) {
			updateStmt.setString(1, bean.getTitle());
			updateStmt.setString(2, bean.getBody());
			updateStmt.setInt(3, bean.getCid());

			rowsAffected = updateStmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return rowsAffected > 0;
	}

}
