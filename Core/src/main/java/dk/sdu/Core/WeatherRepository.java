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
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        try (Connection conn = DatabaseConnection.getConnection()) {
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
    }
}
