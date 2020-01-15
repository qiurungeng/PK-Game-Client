package network;

import networkUtil.CSocketMsgBuilder;
import networkUtil.NetworkIO;
import ui.GamePanel;
import ui.Hero;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;

public class Client {
    private int udpPort_normal;     //客户端普通消息收发端口
    private int udpPort_vital;      //客户端重要消息手法端口
    private int server_udp_normal;  //服务端普通消息转发端口
    private int server_udp_vital;   //服务端重要消息处理端口
    private GamePanel gamePanel;
    private DatagramSocket datagramSocket=null;
    private DatagramSocket datagramSocket_vital=null;

    public Client(GamePanel gamePanel,int udpPort_normal,int udpPort_vital) {
        this.udpPort_normal = udpPort_normal;
        this.udpPort_vital = udpPort_vital;
        this.gamePanel = gamePanel;
        gamePanel.setClient(this);
        try {
            datagramSocket=new DatagramSocket(udpPort_normal);
            datagramSocket_vital=new DatagramSocket(udpPort_vital);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 与服务端建立TCP连接，获取服务端发来的UDP普通消息端口号和重要消息端口号、服务器为客户端英雄分配的ID
     * @param IP
     * @param port
     */
    public void connect(String IP, int port){
        Socket s=null;
        try {
            s = new Socket(IP,port);
            DataOutputStream dos=new DataOutputStream(s.getOutputStream());
            NetworkIO.myWriteInt(dos, udpPort_normal);          //向服务端发送本客户端的UDP端口
            NetworkIO.myWriteInt(dos,udpPort_vital);          //向服务端发送本客户端的UDP端口
            int id = NetworkIO.myReadInt(s);            //获取服务端为新英雄分配的指定ID
            server_udp_normal=NetworkIO.myReadInt(s);   //获取服务端与之通信的UDP1端口
            server_udp_vital=NetworkIO.myReadInt(s);    //获得服务端与之通信的UDP2端口
            gamePanel.getMyHero().setId(id);     //指定新英雄ID
            System.out.println("ClientForC -- My Id:"+gamePanel.getMyHero().getId());
            System.out.println("C Server -- UDP1_PORT:"+server_udp_normal+",UDP2_PORT:"+server_udp_vital);
            System.out.println("connected to server,and server give me an id:"+id);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (s!=null){
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        //向服务端发送新英雄创建成功消息
        HeroCreateMsg heroCreateMsg =new HeroCreateMsg(gamePanel.getMyHero());
        send(heroCreateMsg);

        //启动线程接收服务端发来的UDP Packet
        new Thread(new Client.UDPReceiveThread()).start();
        new Thread(new Client.UDPReceiveThread_Vital()).start();
    }

    /**
     * 退出游戏(关闭窗口)时，向服务端发送退出消息
     */
    public void disconnect(){
        QuitMsg quitMsg=new QuitMsg(gamePanel);
        sendVitalMsg(quitMsg);
    }

    /**
     * 给服务端发普通类型送息，
     */
    public void send(Msg msg){
        msg.send(datagramSocket,"192.168.226.130", server_udp_normal);
    }

    /**
     * 给服务端发送重要类型消息
     * @param msg
     */
    public void sendVitalMsg(Msg msg){
        msg.send(datagramSocket_vital,"192.168.226.130",server_udp_vital);
    }

    //测试攻击消息发送
//    public void sendTestMsg_heroAttack(Hero hero){
//        HeroAttackMsg attackMsg=new HeroAttackMsg(hero);
//        attackMsg.send(datagramSocket_vital,"192.168.226.130",server_udp_vital);
//    }


    /**
     * UDP普通消息处理线程，本地端口：udp_vital,服务器端口：server_udp_vital
     * 接收服务端发来的普通类型消息
     */
    private class UDPReceiveThread implements Runnable{

        byte[] buf=new byte[1024];

        @Override
        public void run() {
            while (datagramSocket!=null){
                DatagramPacket packet=new DatagramPacket(buf,buf.length);
                try {
                    //接收服务端发来的UDP Packet
                    datagramSocket.receive(packet);
                    parse(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void parse(DatagramPacket packet) {
            ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(buf,0,packet.getLength());
            DataInputStream dataInputStream=new DataInputStream(byteArrayInputStream);

            Msg msg=null;
            //根据接收消息类型作相应操作
            try {
                int msgType=dataInputStream.readInt();
                switch (msgType){
                    //新加入玩家通知
                    case Msg.HERO_CREATE_MSG:
                        msg=new HeroCreateMsg(Client.this.gamePanel);    //在一个内部类里去访问封装类的对象
                        msg.parse(dataInputStream);
                        break;
                    //英雄移动通知
                    case Msg.HERO_MOVE_MSG:
                        msg=new HeroMoveMsg(Client.this.gamePanel);
                        msg.parse(dataInputStream);
                        break;
                    //通知新玩家之前已加入的用户
                    case Msg.HERO_ALREADY_EXIST:
                        System.out.println("Parse already exist msg");
                        msg=new HeroAlreadyExistMsg(Client.this.gamePanel);
                        msg.parse(dataInputStream);
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * UDP重要消息处理线程，本地端口：udp_vital,服务器端口：server_udp_vital
     * 接收服务端发来的重要类型消息
     */
    private class UDPReceiveThread_Vital implements Runnable{

        byte[] buf=new byte[1024];

        @Override
        public void run() {
            while (datagramSocket_vital!=null){
                DatagramPacket packet=new DatagramPacket(buf,buf.length);
                try {
                    //接收服务端发来的UDP Packet
                    datagramSocket_vital.receive(packet);
                    parse(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void parse(DatagramPacket packet) {
            ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(buf,0,packet.getLength());
            DataInputStream dataInputStream=new DataInputStream(byteArrayInputStream);
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            DataOutputStream dos=new DataOutputStream(byteArrayOutputStream);
            try{
                byte[] buf=new byte[64];
                int read = dataInputStream.read(buf);
                String resultStr=(new String(buf)).trim();
                String[] split = resultStr.split("\\|");
                int[] result=new int[split.length];

                result[0]=Integer.parseInt(split[0].trim());

                switch (result[0]){
                    //处理被打消息，发送位置到服务端检验是否被打
                    case Msg.HERO_BE_ATTACKED_MSG:
                        result[1]=Integer.parseInt(split[1].trim());
                        int eventId=result[1];
                        CSocketMsgBuilder msgBuilder=new CSocketMsgBuilder();
                        msgBuilder.addInt(Msg.HERO_BE_ATTACKED_MSG);
                        msgBuilder.addInt(eventId);
                        msgBuilder.addInt(gamePanel.getMyHero().getX());
                        msgBuilder.addInt(gamePanel.getMyHero().getY());
                        dos.write(msgBuilder.getSendMsg().getBytes("GBK"));
                        byte[] buffer=byteArrayOutputStream.toByteArray();
                        DatagramPacket feedbackPacket=new DatagramPacket(buffer,
                                buffer.length,new InetSocketAddress("192.168.226.130",server_udp_vital));
                        datagramSocket_vital.send(feedbackPacket);
                        break;
                    //处理英雄被击中消息
                    case Msg.HERO_BE_HURT:
                        int beHurtHeroId=Integer.parseInt(split[1].trim());
                        int bloodReduce=Integer.parseInt(split[2].trim());
                        for (Hero hero : gamePanel.getHeroes()) {
                            if (hero.getId()==beHurtHeroId){
                                //扣血，僵直，被打退
                                hero.setBlood(hero.getBlood()-bloodReduce);
                                hero.beHurt();
                                if (hero.isFaceToRight()){
                                    hero.setX(hero.getX()+10);
                                }else {
                                    hero.setX(hero.getX()-10);
                                }
                                break;
                            }
                        }
                        break;
                    //处理英雄死亡消息
                    case Msg.HERO_DIE_MSG:
                        int dieHeroId=Integer.parseInt(split[1].trim());
//                        System.out.println("处理死亡消息："+dieHeroId+"_______________________________");
                        gamePanel.killHeroById(dieHeroId);
                        break;
                    //处理获胜消息
                    case Msg.HERO_WIN_MSG:
                        int winnerId=Integer.parseInt(split[1].trim());
                        gamePanel.handleWinMsg(winnerId);
                        break;
                    //处理退出消息
                    case Msg.HERO_QUIT_MSG:
                        int quitHeroId=Integer.parseInt(split[1].trim());
                        System.out.println("Hero "+quitHeroId+" has quit.");
                        for (int i = 0; i < gamePanel.getHeroes().size(); i++) {
                            Hero hero=gamePanel.getHeroes().get(i);
                            if (hero.getId()==quitHeroId){
                                gamePanel.getHeroes().remove(i);
                                break;
                            }
                        }
                        break;
                    default:
                        break;
                }

            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }

}
