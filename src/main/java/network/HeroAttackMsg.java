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
 * 攻击消息，由进行攻击动作的用户发给服务器
 * 只有发送方法，没有解析方法
 * 服务器接收后，将为除攻击发起人外的所有人发送被攻击消息 HeroBeAttackedMsg
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
