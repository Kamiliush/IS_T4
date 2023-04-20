package org.example;

import java.sql.*;
import java.util.List;

public class MysqlDatabaseConnector {
    private final String DB_URL = "jdbc:mysql://localhost:3308/laptops?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String DB_USERNAME = "root";
    private final String DB_PASSWORD = "root";

    private void saveToDatabase( List<LaptopEntity> laptopsList) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            for (LaptopEntity laptop : laptopsList) {
                String query = "INSERT INTO laptops (manufacturer, screen_diagonal, screen_resolution, screen_finish, " +
                        "touch_screen_present, CPU, core_count, CPU_frequency, RAM, disk_capacity, disk_type, GPU, " +
                        "GPU_memory, OS, optical_drive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, laptop.getManufacturer());
                preparedStatement.setString(2, laptop.getScreenDiagonal());
                preparedStatement.setString(3, laptop.getScreenResolution());
                preparedStatement.setString(4, laptop.getScreenFinish());
                preparedStatement.setString(5, laptop.getTouchScreenPresent());
                preparedStatement.setString(6, laptop.getCPU());
                preparedStatement.setString(7, laptop.getCoreCount());
                preparedStatement.setString(8, laptop.getCPUFrequency());
                preparedStatement.setString(9, laptop.getRAM());
                preparedStatement.setString(10, laptop.getDiskCapacity());
                preparedStatement.setString(11, laptop.getDiskType());
                preparedStatement.setString(12, laptop.getGPU());
                preparedStatement.setString(13, laptop.getGPUMemory());
                preparedStatement.setString(14, laptop.getOS());
                preparedStatement.setString(15, laptop.getOpticalDrive());

                preparedStatement.executeUpdate();
            }

            connection.close();
        } catch (Exception e) {
            /*
            JOptionPane.showMessageDialog(null, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();*/
        }
    }
}
