package dal;

import models.Assignment;
import java.sql.*;
import java.util.*;

public class AssignmentDAO extends DBContext {

    public List<Assignment> getAllAssignments() {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT * FROM Assignments ORDER BY created_date DESC";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Assignment a = new Assignment();
                a.setAssignmentId(rs.getInt("assignment_id"));
                a.setTitle(rs.getString("title"));
                a.setDescription(rs.getString("description"));
                a.setDueDate(rs.getTimestamp("due_date"));
                a.setCreatedBy(rs.getInt("created_by"));
                a.setStatus(rs.getString("status"));
                a.setFilePath(rs.getString("file_path"));
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

 public List<Assignment> getAssignmentsForTeacher() {
    List<Assignment> list = new ArrayList<>();

    String sql = "SELECT * FROM Assignments ORDER BY created_date DESC";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Assignment a = new Assignment();
            a.setAssignmentId(rs.getInt("assignment_id"));
            a.setTitle(rs.getString("title"));
            a.setDescription(rs.getString("description"));
            a.setDueDate(rs.getTimestamp("due_date"));
            list.add(a);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

    public List<Assignment> getAssignmentsForStudent(int studentId) {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT a.*, ISNULL(s.status, N'Chưa nộp') as status, s.file_path "
                + "FROM Assignments a LEFT JOIN Submissions s "
                + "ON a.assignment_id = s.assignment_id AND s.student_id = ? "
                + "ORDER BY a.created_date DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Assignment a = new Assignment();
                a.setAssignmentId(rs.getInt("assignment_id"));
                a.setTitle(rs.getString("title"));
                a.setDescription(rs.getString("description"));
                a.setDueDate(rs.getTimestamp("due_date"));
                a.setStatus(rs.getString("status"));
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Assignment> searchAssignments(String keyword) {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT * FROM Assignments WHERE title LIKE ? OR description LIKE ? ORDER BY created_date DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Assignment a = new Assignment();
                a.setAssignmentId(rs.getInt("assignment_id"));
                a.setTitle(rs.getString("title"));
                a.setDescription(rs.getString("description"));
                a.setDueDate(rs.getTimestamp("due_date"));
                a.setCreatedBy(rs.getInt("created_by"));
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void createAssignment(Assignment a) {
        String sql = "INSERT INTO Assignments(title, description, due_date, created_by) VALUES(?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, a.getTitle());
            ps.setString(2, a.getDescription());
            ps.setTimestamp(3, new Timestamp(a.getDueDate().getTime()));
            ps.setInt(4, a.getCreatedBy());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
