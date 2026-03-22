package dal;

import models.Assignment;
import java.sql.*;
import java.util.*;

public class AssignmentDAO extends DBContext {

    public AssignmentDAO() {
        super();
        try (Statement st = connection.createStatement()) {
            st.execute("ALTER TABLE Assignments ADD is_deleted BIT DEFAULT 0");
        } catch (Exception e) {
            // Field already exists, ignore
        }
    }

    public List<Assignment> getAllAssignments() {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT a.*, c.class_name FROM Assignments a LEFT JOIN Classes c ON a.class_id = c.class_id WHERE ISNULL(a.is_deleted, 0) = 0 ORDER BY a.created_date DESC";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRecord(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assignment err = new Assignment();
            err.setTitle("SQL Error Exception");
            err.setDescription(e.toString() + " \n " + e.getMessage());
            list.add(err);
        }
        return list;
    }

    public List<Assignment> getAssignmentsForTeacher(int classId) {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT a.*, c.class_name FROM Assignments a LEFT JOIN Classes c ON a.class_id = c.class_id WHERE a.class_id = ? AND ISNULL(a.is_deleted, 0) = 0 ORDER BY a.created_date DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRecord(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Assignment> getAssignmentsForStudent(int studentId, int classId) {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT a.*, ISNULL(s.status, N'Chưa nộp') as status, s.file_path, s.score, c.class_name "
                + "FROM Assignments a LEFT JOIN Submissions s "
                + "ON a.assignment_id = s.assignment_id AND s.student_id = ? "
                + "LEFT JOIN Classes c ON a.class_id = c.class_id "
                + "WHERE a.class_id = ? AND ISNULL(a.is_deleted, 0) = 0 "
                + "ORDER BY a.created_date DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, classId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Assignment a = mapRecord(rs);
                a.setStatus(rs.getString("status"));
                Object scoreObj = rs.getObject("score");
                if (scoreObj != null) {
                    a.setScore(rs.getDouble("score"));
                }
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Assignment> searchAllAssignments(String keyword) {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT a.*, c.class_name FROM Assignments a LEFT JOIN Classes c ON a.class_id = c.class_id WHERE (a.title LIKE ? OR a.description LIKE ?) AND ISNULL(a.is_deleted, 0) = 0 ORDER BY a.created_date DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Assignment> searchAssignments(String keyword, int classId) {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT a.*, c.class_name FROM Assignments a LEFT JOIN Classes c ON a.class_id = c.class_id WHERE (a.title LIKE ? OR a.description LIKE ?) AND a.class_id = ? AND ISNULL(a.is_deleted, 0) = 0 ORDER BY a.created_date DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setInt(3, classId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void createAssignment(Assignment a) {
        String sql = "INSERT INTO Assignments(title, description, due_date, created_by, class_id, assignment_file) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, a.getTitle());
            ps.setString(2, a.getDescription());
            ps.setTimestamp(3, new Timestamp(a.getDueDate().getTime()));
            ps.setInt(4, a.getCreatedBy());
            if (a.getClassId() != null) {
                ps.setInt(5, a.getClassId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            if (a.getFilePath() != null && !a.getFilePath().isEmpty()) {
                ps.setString(6, a.getFilePath());
            } else {
                ps.setNull(6, java.sql.Types.NVARCHAR);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Assignment getAssignmentById(int assignmentId) {
        String sql = "SELECT a.*, c.class_name FROM Assignments a LEFT JOIN Classes c ON a.class_id = c.class_id WHERE a.assignment_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRecord(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAssignment(int assignmentId) {
        String sqlUpdate = "UPDATE Assignments SET is_deleted = 1 WHERE assignment_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlUpdate)) {
            ps.setInt(1, assignmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void restoreAssignment(int assignmentId) {
        String sqlUpdate = "UPDATE Assignments SET is_deleted = 0 WHERE assignment_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlUpdate)) {
            ps.setInt(1, assignmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Assignment> getDeletedAssignments(boolean isAdmin, int classId) {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT a.*, c.class_name FROM Assignments a LEFT JOIN Classes c ON a.class_id = c.class_id WHERE a.is_deleted = 1 ";
        if (!isAdmin) {
            sql += "AND a.class_id = ? ";
        }
        sql += "ORDER BY a.created_date DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (!isAdmin) {
                ps.setInt(1, classId);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Assignment mapRecord(ResultSet rs) throws SQLException {
        Assignment a = new Assignment();
        a.setAssignmentId(rs.getInt("assignment_id"));
        a.setTitle(rs.getString("title"));
        a.setDescription(rs.getString("description"));
        a.setDueDate(rs.getTimestamp("due_date"));
        a.setCreatedBy(rs.getInt("created_by"));
        a.setFilePath(rs.getString("assignment_file"));
        a.setClassName(rs.getString("class_name"));
        return a;
    }
}
