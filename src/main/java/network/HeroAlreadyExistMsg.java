package network;

import ui.GamePanel;
import ui.Hero;
import ui.HeroCaiXuKun;
import ui.HeroWuYiFan;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class HeroAlreadyExistMsg implements Msg{

    int msgType =Msg.HERO_ALREADY_EXIST;
    int targetID;
    Hero hero;
    GamePanel gamePanel;

    public HeroAlreadyExistMsg(Hero hero, int targetID) {
        this.hero = hero;
        this.targetID=targetID;
    }

    public HeroAlreadyExistMsg(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void send(DatagramSocket datagramSocket,String IP,int udpPort) {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        DataOutputStream dos=new DataOutputStream(byteArrayOutputStream);

        try {
            dos.writeInt(msgType);
            dos.writeInt(targetID);
            dos.writeInt(hero.getId());
            dos.writeInt(hero.getX());
            dos.writeInt(hero.getY());
            dos.writeInt(hero.getType());
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

    public void parse(DataInputStream dis) {

        try {
            int targetId=dis.readInt();

            //若正是ID为目标ID的新创建用户，为它添加已存在英雄
            if (gamePanel.getMyHero().getId()==targetId){
                int id=dis.readInt();
                int x=dis.readInt();
                int y=dis.readInt();
                int type=dis.readInt();
                Hero newConnectedHero=null;
                if (type==Hero.TYPE_CXK){
                    newConnectedHero=new HeroCaiXuKun(id,x,y);
                }else {
                    newConnectedHero=new HeroWuYiFan(id,x,y);
                }
                gamePanel.addHero(newConnectedHero);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
