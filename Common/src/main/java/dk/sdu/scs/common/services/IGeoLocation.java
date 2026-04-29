package dk.sdu.cbse.common.services;

public interface IGeoLocation {

    double getLatitude();

    double getLongitude();

    String getAll(String address);
}
