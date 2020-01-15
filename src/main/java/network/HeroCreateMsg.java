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

public class HeroCreateMsg implements Msg {
    int msgType = Msg.HERO_CREATE_MSG;

    Hero hero;
    GamePanel gamePanel;

    public HeroCreateMsg(Hero hero) {
        this.hero = hero;
    }

    public HeroCreateMsg(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    HeroCreateMsg(){}

    public void send(DatagramSocket datagramSocket,String IP,int udpPort) {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        DataOutputStream dos=new DataOutputStream(byteArrayOutputStream);

        try {
            dos.writeInt(msgType);
            dos.writeInt(hero.getType());
            dos.writeInt(hero.getId());
            dos.writeInt(hero.getX());
            dos.writeInt(hero.getY());
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buf=byteArrayOutputStream.toByteArray();
        DatagramPacket packet=new DatagramPacket(buf,buf.length,new InetSocketAddress(IP,udpPort));
        try {
            datagramSocket.send(packet);
            System.out.println("HeroCreateMessage has send");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parse(DataInputStream dis) {

        try {
            int type=dis.readInt(); //英雄类型
            int id=dis.readInt();   //新用户ID
            int x=dis.readInt();
            int y=dis.readInt();

            if (gamePanel.getMyHero().getId()==id){
                return;     //自己的消息不做响应
            }

            //将新用户加入到自己的客户端
            Hero newConnectedHero=null;
            if (type==Hero.TYPE_CXK){
                newConnectedHero=new HeroCaiXuKun(id,x,y);
            }else {
                newConnectedHero=new HeroWuYiFan(id,x,y);
            }
            gamePanel.addHero(newConnectedHero);

            //告诉新用户本客户端的英雄的信息
            HeroAlreadyExistMsg alreadyExistMsg=new HeroAlreadyExistMsg(gamePanel.getMyHero(),id);
            gamePanel.getClient().send(alreadyExistMsg);

            System.out.println("id:"+id+" x:"+x+" :y"+y);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
