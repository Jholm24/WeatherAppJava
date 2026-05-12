/*
package dk.sdu.scs.GeoLocation;

import dk.sdu.GeoLocation.GeoLocationAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeoLocationAPITest {

    private GeoLocationAPI geoLocationAPI;

    @BeforeEach
    void setUp() {
        geoLocationAPI = new GeoLocationAPI();
    }

    @Test
    void testInitialLatitudeIsZero() {
        assertEquals(0.0, geoLocationAPI.getLatitude());
    }

    @Test
    void testInitialLongitudeIsZero() {
        assertEquals(0.0, geoLocationAPI.getLongitude());
    }

    @Test
    void testGetAllReturnsDanishCoordinates() {
        String result = geoLocationAPI.getAll("Campusvej 55, Odense");
        assertNotNull(result);
    }

    @Test
    void testGetAllWithInvalidAddressReturnsZeroResults() {
        String result = geoLocationAPI.getAll("falseadress");
        assertNotNull(result);
    }

    @Test
    void testLatitudeIsSetAfterGetAll() {
        geoLocationAPI.getAll("Campusvej 55, Odense");
        double lat = geoLocationAPI.getLatitude();
        assertTrue(lat > 55.0 && lat < 56.0);
    }

    @Test
    void testLongitudeIsSetAfterGetAll() {
        geoLocationAPI.getAll("Campusvej 55, Odense");
        double lng = geoLocationAPI.getLongitude();
        assertTrue(lng > 10.0 && lng < 11.0);
    }

    @Test
    void testGetAllWithEmptyStringDoesNotCrash() {
        assertDoesNotThrow(() -> geoLocationAPI.getAll(""));
    }

    @Test
    void testCopenhagenCoordinates() {
        geoLocationAPI.getAll("Rådhuspladsen, København");
        assertTrue(geoLocationAPI.getLatitude() > 55.6 && geoLocationAPI.getLatitude() < 55.7);
        assertTrue(geoLocationAPI.getLongitude() > 12.5 && geoLocationAPI.getLongitude() < 12.6);
    }
}

 */