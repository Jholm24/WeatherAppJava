package dk.sdu.scs.common.services;

public interface IGeoLocation {

    double getLatitude();

    double getLongitude();

    String getAll(String address);
}
