package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.ClassRoom;

public class ClassDAO extends DBContext {

    public List<ClassRoom> getAllClasses() {
        List<ClassRoom> list = new ArrayList<>();
        String sql = "SELECT * FROM Classes";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ClassRoom(
                        rs.getInt("class_id"), 
                        rs.getString("class_name"), 
                        rs.getInt("teacher_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
