package network;

import java.io.DataInputStream;
import java.net.DatagramSocket;

public interface Msg {
    public static final int HERO_CREATE_MSG=1;
    public static final int HERO_MOVE_MSG=2;
    public static final int HERO_ALREADY_EXIST=3;
    public static final int HERO_ATTACK_MEG=4;
    public static final int HERO_BE_ATTACKED_MSG=5;
    public static final int HERO_BE_HURT=6;
    public static final int HERO_DIE_MSG=7;
    public static final int HERO_WIN_MSG =8;
    public static final int HERO_QUIT_MSG=9;

    public void send(DatagramSocket datagramSocket, String IP, int udpPort);
    public void parse(DataInputStream dis);
}
