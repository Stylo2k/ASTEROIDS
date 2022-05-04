package nl.rug.aoop.asteroids.model.connection;

import lombok.extern.java.Log;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * handles all the traffic going to or out of the server and client
 * */
@Log
public abstract class TrafficHandler {
    public static final int MAX_SIZE = 2024;

    /**
     * sends a {@link MultiPlayerGamePackage} from the client to the server
     * @param mp the multiplayer package to send
     * @param s the socket to send through
     * @param ce the {@link ConnectionEssentials} used to communicate with the other side
     * */
    public void sendMultiPlayerGamePackage(MultiPlayerGamePackage mp, DatagramSocket s, ConnectionEssentials ce) throws IOException {
        turnIntoBytesAndSend(mp, s, ce);
    }

    /**
     * receives a {@link MultiPlayerGamePackage} from the server
     * @param s the socket to send through
     * */
    public MultiPlayerGamePackage receiveMultiPlayerGamePackage(DatagramSocket s) throws IOException, ClassNotFoundException {
        DatagramPacket packet = receive(s);
        ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
        MultiPlayerGamePackage mp = (MultiPlayerGamePackage) iStream.readObject();
        iStream.close();
        return mp;
    }

    /**
     * sends a {@link SinglePlayerPackage} to the other side
     * @param sp the single player package to send
     * @param s the socket to send through
     * @param ce the {@link ConnectionEssentials} used to communicate with the other side
     * */
    public void sendSinglePlayerGamePackage(SinglePlayerPackage sp, DatagramSocket s, ConnectionEssentials ce) throws IOException {
        turnIntoBytesAndSend(sp, s, ce);
    }

    /**
     * turns an object calls into bytes. Make sure that that class implements {@link Serializable}
     * @param sp class to send over
     * @param s the socket to send through
     * @param ce the {@link ConnectionEssentials} used to communicate with the other side
     * */
    private void turnIntoBytesAndSend(Object sp, DatagramSocket s, ConnectionEssentials ce) throws IOException {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutput oo = new ObjectOutputStream(bStream);
        oo.writeObject(sp);
        oo.close();
        byte[] data = bStream.toByteArray();
        s.send(new DatagramPacket(data, data.length, ce.ipAddress(), ce.port()));
    }

    /**
     * receives a single {@link SinglePlayerPackage}
     * @param s the socket to send data through
     * */
    public SinglePlayerPackage receiveSinglePlayerGamePackage(DatagramSocket s) throws IOException, ClassNotFoundException {
        DatagramPacket packet = receive(s);
        ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
        SinglePlayerPackage mp = (SinglePlayerPackage) iStream.readObject();
        iStream.close();
        return mp;
    }

    /**
     * sends an initial packet to grant connection
     * @param s the socket to send through
     * @param ce the {@link ConnectionEssentials} used to communicate with the other side
     * */
    public void sendInitPacket (DatagramSocket s, ConnectionEssentials ce) throws IOException {
        DatagramPacket packet = new DatagramPacket(new byte[1], 1, ce.ipAddress(), ce.port());
        s.send(packet);
    }

    /**
     * receives a packet from the socket given
     * @param s the socket to send through
     * */
    public DatagramPacket receive(DatagramSocket s) throws IOException {
        byte[] data = new byte[MAX_SIZE];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        s.receive(packet);
        return packet;
    }

    /**
     * notifies server or client that the other has left
     * @param s the socket to send through
     * @param ce the {@link ConnectionEssentials} used to communicate with the other side
     * */
    public void sendQuit (DatagramSocket s, ConnectionEssentials ce) throws IOException {
        DatagramPacket packet = new DatagramPacket(new byte[1], 1, ce.ipAddress(), ce.port());
        s.send(packet);
    }

    /**
     * receives a quit action from either the server or the client
     * @param s the socket to send through
     * */
    public void receiveQuit(DatagramSocket s) throws IOException {
        byte[] data = new byte[1];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        s.receive(packet);
    }

}
