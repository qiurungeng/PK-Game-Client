package network;

import ui.GamePanel;
import ui.Hero;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class HeroMoveMsg implements Msg {
    int msgType=Msg.HERO_MOVE_MSG;
    int id;
    HeroAction action;
    GamePanel gamePanel;

    public HeroMoveMsg(GamePanel gamePanel) {
        this.gamePanel=gamePanel;
    }

    public HeroMoveMsg(int id, GamePanel gamePanel, HeroAction heroAction){
        this.id=id;
        this.gamePanel=gamePanel;
        this.action=heroAction;
    }

    @Override
    public void send(DatagramSocket datagramSocket, String IP, int udpPort) {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        DataOutputStream dos=new DataOutputStream(byteArrayOutputStream);

        try {
            dos.writeInt(msgType);
            dos.writeInt(id);

            dos.writeInt(action.x);
            dos.writeInt(action.y);
            dos.writeBoolean(action.up);
            dos.writeBoolean(action.down);
            dos.writeBoolean(action.left);
            dos.writeBoolean(action.right);
            dos.writeBoolean(action.attack);
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

        try {
            int id=dis.readInt();
//@@@@@@@@@@@@@            System.out.println(id);
            if (gamePanel.getMyHero().getId()==id){
                return;     //自己的消息不做响应
            }
            int x=dis.readInt();
            int y=dis.readInt();
            boolean up=dis.readBoolean();
            boolean down=dis.readBoolean();
            boolean left=dis.readBoolean();
            boolean right=dis.readBoolean();
            boolean attack=dis.readBoolean();

            boolean exist=false;
            for (Hero hero : gamePanel.getHeroes()) {
                if (hero.getId()==id){
                    hero.setX(x);
                    hero.setY(y);
                    hero.setUp(up);
                    hero.setDown(down);
                    hero.setLeft(left);
                    hero.setRight(right);
                    hero.setAttack(attack);
//                    System.out.println("ID:"+hero.getId()+"---up:"+hero.isUp()+",down:"+hero.isDown()+
//                            ",left:"+hero.isLeft()+",right:"+hero.isRight());
                    exist=true;
                    break;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
