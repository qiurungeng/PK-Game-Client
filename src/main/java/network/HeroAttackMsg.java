package network;

import networkUtil.CSocketMsgBuilder;
import sun.nio.cs.ext.MS874;
import ui.Hero;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * ������Ϣ���ɽ��й����������û�����������
 * ֻ�з��ͷ�����û�н�������
 * ���������պ󣬽�Ϊ��������������������˷��ͱ�������Ϣ HeroBeAttackedMsg
 */
public class HeroAttackMsg implements Msg {
    int msgType=HERO_ATTACK_MEG;
    Hero hero;

    public HeroAttackMsg(Hero hero) {
        this.hero = hero;
    }

    @Override
    public void parse(DataInputStream dis) {
    }

    public void send(DatagramSocket datagramSocket, String IP, int udpPort){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        DataOutputStream dos=new DataOutputStream(byteArrayOutputStream);
        try {
            CSocketMsgBuilder msgBuilder=new CSocketMsgBuilder();
            msgBuilder.addInt(msgType);
            msgBuilder.addInt(hero.getId());
            msgBuilder.addInt(hero.getX());
            msgBuilder.addInt(hero.getY());
            msgBuilder.addBoolean(hero.isFaceToRight());
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
}
