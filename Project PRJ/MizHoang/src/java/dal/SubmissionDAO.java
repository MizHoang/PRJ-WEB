package dal;
import java.sql.*;

public class SubmissionDAO extends DBContext {

    public boolean submitAssignment(int assignmentId, int studentId, String filePath) {

        String check = "SELECT 1 FROM Submissions WHERE assignment_id = ? AND student_id = ?";

        try (
            PreparedStatement psCheck = connection.prepareStatement(check)
        ) {
            psCheck.setInt(1, assignmentId);
            psCheck.setInt(2, studentId);

            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                //  ĐÃ TỒN TẠI → UPDATE (NỘP LẠI)
                String update = "UPDATE Submissions SET file_path = ?, submit_date = GETDATE(), status = N'Đã nộp' WHERE assignment_id = ? AND student_id = ?";
                
                try (PreparedStatement ps = connection.prepareStatement(update)) {
                    ps.setString(1, filePath);
                    ps.setInt(2, assignmentId);
                    ps.setInt(3, studentId);
                    return ps.executeUpdate() > 0;
                }

            } else {
                // ✅ CHƯA CÓ → INSERT
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
}