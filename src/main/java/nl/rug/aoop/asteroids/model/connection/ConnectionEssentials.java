package nl.rug.aoop.asteroids.model.connection;

import java.net.InetAddress;

/**
 * holds a {@link InetAddress} and a port. Both of which are essential to connect
 * to either server or client.
 * @param ipAddress the ip address to connect to
 * @param port the port to connect through
 * **/
public record ConnectionEssentials(InetAddress ipAddress, int port) {
}
