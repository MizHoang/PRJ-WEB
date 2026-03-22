package dal;
import models.Submission;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubmissionDAO extends DBContext {

    public boolean submitAssignment(int assignmentId, int studentId, String filePath) {
        String check = "SELECT 1 FROM Submissions WHERE assignment_id = ? AND student_id = ?";

        try (PreparedStatement psCheck = connection.prepareStatement(check)) {
            psCheck.setInt(1, assignmentId);
            psCheck.setInt(2, studentId);

            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                // ĐÃ TỒN TẠI → UPDATE (NỘP LẠI)
                String update = "UPDATE Submissions SET file_path = ?, submit_date = GETDATE(), status = N'Đã nộp' WHERE assignment_id = ? AND student_id = ?";
                try (PreparedStatement ps = connection.prepareStatement(update)) {
                    ps.setString(1, filePath);
                    ps.setInt(2, assignmentId);
                    ps.setInt(3, studentId);
                    return ps.executeUpdate() > 0;
                }
            } else {
                // CHƯA CÓ → INSERT
                String insert = "INSERT INTO Submissions(assignment_id, student_id, file_path, submit_date, status) VALUES(?,?,?,GETDATE(),N'Đã nộp')";
                try (PreparedStatement ps = connection.prepareStatement(insert)) {
                    ps.setInt(1, assignmentId);
                    ps.setInt(2, studentId);
                    ps.setString(3, filePath);
                    return ps.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Submission> getSubmissionsByAssignment(int assignmentId) {
        List<Submission> list = new ArrayList<>();
        String sql = "SELECT u.user_id, u.full_name, " +
                     "s.submission_id, s.file_path, s.submit_date, s.status, s.score, a.due_date " +
                     "FROM Users u " +
                     "INNER JOIN Assignments a ON u.class_id = a.class_id " +
                     "LEFT JOIN Submissions s ON s.student_id = u.user_id AND s.assignment_id = a.assignment_id " +
                     "WHERE a.assignment_id = ? AND u.role = 'student'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            ResultSet rs = ps.executeQuery();
            java.util.Date now = new java.util.Date();
            while (rs.next()) {
                Submission sub = new Submission();
                int submitId = rs.getInt("submission_id");
                sub.setSubmissionId(submitId);
                sub.setAssignmentId(assignmentId);
                sub.setStudentId(rs.getInt("user_id"));
                sub.setStudentName(rs.getString("full_name"));
                
                Timestamp dueDate = rs.getTimestamp("due_date");
                boolean isPastDue = dueDate != null && now.after(dueDate);
                
                if (submitId > 0) {
                    sub.setFilePath(rs.getString("file_path"));
                    sub.setSubmitDate(rs.getTimestamp("submit_date"));
                    sub.setStatus(rs.getString("status"));
                    Object sc = rs.getObject("score");
                    if (sc != null) sub.setScore(rs.getDouble("score"));
                } else {
                    sub.setStatus(isPastDue ? "Quá hạn" : "Chưa làm");
                    if (isPastDue) {
                        sub.setScore(0.0);
                    }
                }
                list.add(sub);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean gradeStudent(int assignmentId, int studentId, int submissionId, double score) {
        if (submissionId > 0) {
            String sql = "UPDATE Submissions SET score = ? WHERE submission_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setDouble(1, score);
                ps.setInt(2, submissionId);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) { e.printStackTrace(); }
        } else {
            String sql = "INSERT INTO Submissions(assignment_id, student_id, score, status, submit_date) VALUES(?,?,?, N'Đã chấm (Quá hạn)', GETDATE())";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, assignmentId);
                ps.setInt(2, studentId);
                ps.setDouble(3, score);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }
}