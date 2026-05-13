package dk.sdu.Core;

import dk.sdu.scs.common.services.IWeather;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WeatherRepository {

    public int saveGeoAddress(String address, double latitude, double longitude) throws SQLException {
        String sql = """
                INSERT INTO GeoAddresses (address, latitude, longitude)
                VALUES (?, ?, ?)
                ON CONFLICT (address) DO UPDATE
                    SET latitude = EXCLUDED.latitude, longitude = EXCLUDED.longitude
                RETURNING id
                """;
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, address);
            stmt.setBigDecimal(2, BigDecimal.valueOf(latitude));
            stmt.setBigDecimal(3, BigDecimal.valueOf(longitude));
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt("id");
        }
    }

    public void saveWeatherReading(int addressId, IWeather weather) throws SQLException {
        String sourceQuery = "SELECT id FROM WeatherSources WHERE name = ?";
        String insert = """
                INSERT INTO WeatherReadings (address_id, source_id, temp, humidity, windDirection, windSpeed, cloudCover)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        Connection conn = DatabaseConnection.getInstance().getConnection();
        int sourceId;
        try (PreparedStatement stmt = conn.prepareStatement(sourceQuery)) {
            stmt.setString(1, weather.getName().toLowerCase());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) throw new SQLException("Ukendt kilde: " + weather.getName());
            sourceId = rs.getInt("id");
        }
        try (PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setInt(1, addressId);
            stmt.setInt(2, sourceId);
            stmt.setBigDecimal(3, BigDecimal.valueOf(weather.getTemperature()));
            stmt.setInt(4, (int) weather.getHumidity());
            stmt.setString(5, weather.getWindDirection());
            stmt.setBigDecimal(6, BigDecimal.valueOf(weather.getWindSpeed()));
            stmt.setString(7, weather.getCloudCover());
            stmt.executeUpdate();
        }
    }

    public String getHistory(String address) throws SQLException {
        String sql = """
                SELECT w.created_at, s.name AS source, w.temp, w.humidity, w.windSpeed
                FROM WeatherReadings w
                JOIN WeatherSources s ON s.id = w.source_id
                JOIN GeoAddresses g ON g.id = w.address_id
                WHERE g.address = ?
                ORDER BY w.created_at ASC
                """;
        StringBuilder json = new StringBuilder("[");
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, address);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                json.append("{")
                        .append("\"time\":\"").append(rs.getTimestamp("created_at").toInstant()).append("\",")
                        .append("\"source\":\"").append(rs.getString("source")).append("\",")
                        .append("\"temp\":").append(rs.getBigDecimal("temp")).append(",")
                        .append("\"humidity\":").append(rs.getInt("humidity")).append(",")
                        .append("\"windSpeed\":").append(rs.getBigDecimal("windspeed"))
                        .append("},");
            }
        }
        if (json.charAt(json.length() - 1) == ',') {
            json.deleteCharAt(json.length() - 1);
        }
        json.append("]");
        return json.toString();
    }
}
