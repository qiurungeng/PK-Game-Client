package network;

import networkUtil.CSocketMsgBuilder;
import ui.GamePanel;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class QuitMsg implements Msg{
    private int msgType=Msg.HERO_QUIT_MSG;
    private GamePanel gamePanel;

    public QuitMsg(GamePanel gamePanel){
        this.gamePanel=gamePanel;
    }

    @Override
    public void send(DatagramSocket datagramSocket, String IP, int udpPort) {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        DataOutputStream dos=new DataOutputStream(byteArrayOutputStream);
        try {
            CSocketMsgBuilder msgBuilder=new CSocketMsgBuilder();
            msgBuilder.addInt(msgType);
            msgBuilder.addInt(0);
            dos.write(msgBuilder.getSendMsg().getBytes("GBK"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buf=byteArrayOutputStream.toByteArray();
        DatagramPacket packet=new DatagramPacket(buf,buf.length,new InetSocketAddress(IP,udpPort));
        try {
            datagramSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void parse(DataInputStream dis) {

    }
}
