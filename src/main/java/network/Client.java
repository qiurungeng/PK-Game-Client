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
    private int udpPort_normal;     //�ͻ�����ͨ��Ϣ�շ��˿�
    private int udpPort_vital;      //�ͻ�����Ҫ��Ϣ�ַ��˿�
    private int server_udp_normal;  //�������ͨ��Ϣת���˿�
    private int server_udp_vital;   //�������Ҫ��Ϣ����˿�
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
     * �����˽���TCP���ӣ���ȡ����˷�����UDP��ͨ��Ϣ�˿ںź���Ҫ��Ϣ�˿ںš�������Ϊ�ͻ���Ӣ�۷����ID
     * @param IP
     * @param port
     */
    public void connect(String IP, int port){
        Socket s=null;
        try {
            s = new Socket(IP,port);
            DataOutputStream dos=new DataOutputStream(s.getOutputStream());
            NetworkIO.myWriteInt(dos, udpPort_normal);          //�����˷��ͱ��ͻ��˵�UDP�˿�
            NetworkIO.myWriteInt(dos,udpPort_vital);          //�����˷��ͱ��ͻ��˵�UDP�˿�
            int id = NetworkIO.myReadInt(s);            //��ȡ�����Ϊ��Ӣ�۷����ָ��ID
            server_udp_normal=NetworkIO.myReadInt(s);   //��ȡ�������֮ͨ�ŵ�UDP1�˿�
            server_udp_vital=NetworkIO.myReadInt(s);    //��÷������֮ͨ�ŵ�UDP2�˿�
            gamePanel.getMyHero().setId(id);     //ָ����Ӣ��ID
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

        //�����˷�����Ӣ�۴����ɹ���Ϣ
        HeroCreateMsg heroCreateMsg =new HeroCreateMsg(gamePanel.getMyHero());
        send(heroCreateMsg);

        //�����߳̽��շ���˷�����UDP Packet
        new Thread(new Client.UDPReceiveThread()).start();
        new Thread(new Client.UDPReceiveThread_Vital()).start();
    }

    /**
     * �˳���Ϸ(�رմ���)ʱ�������˷����˳���Ϣ
     */
    public void disconnect(){
        QuitMsg quitMsg=new QuitMsg(gamePanel);
        sendVitalMsg(quitMsg);
    }

    /**
     * ������˷���ͨ������Ϣ��
     */
    public void send(Msg msg){
        msg.send(datagramSocket,"192.168.226.130", server_udp_normal);
    }

    /**
     * ������˷�����Ҫ������Ϣ
     * @param msg
     */
    public void sendVitalMsg(Msg msg){
        msg.send(datagramSocket_vital,"192.168.226.130",server_udp_vital);
    }

    //���Թ�����Ϣ����
//    public void sendTestMsg_heroAttack(Hero hero){
//        HeroAttackMsg attackMsg=new HeroAttackMsg(hero);
//        attackMsg.send(datagramSocket_vital,"192.168.226.130",server_udp_vital);
//    }


    /**
     * UDP��ͨ��Ϣ�����̣߳����ض˿ڣ�udp_vital,�������˿ڣ�server_udp_vital
     * ���շ���˷�������ͨ������Ϣ
     */
    private class UDPReceiveThread implements Runnable{

        byte[] buf=new byte[1024];

        @Override
        public void run() {
            while (datagramSocket!=null){
                DatagramPacket packet=new DatagramPacket(buf,buf.length);
                try {
                    //���շ���˷�����UDP Packet
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
            //���ݽ�����Ϣ��������Ӧ����
            try {
                int msgType=dataInputStream.readInt();
                switch (msgType){
                    //�¼������֪ͨ
                    case Msg.HERO_CREATE_MSG:
                        msg=new HeroCreateMsg(Client.this.gamePanel);    //��һ���ڲ�����ȥ���ʷ�װ��Ķ���
                        msg.parse(dataInputStream);
                        break;
                    //Ӣ���ƶ�֪ͨ
                    case Msg.HERO_MOVE_MSG:
                        msg=new HeroMoveMsg(Client.this.gamePanel);
                        msg.parse(dataInputStream);
                        break;
                    //֪ͨ�����֮ǰ�Ѽ�����û�
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
     * UDP��Ҫ��Ϣ�����̣߳����ض˿ڣ�udp_vital,�������˿ڣ�server_udp_vital
     * ���շ���˷�������Ҫ������Ϣ
     */
    private class UDPReceiveThread_Vital implements Runnable{

        byte[] buf=new byte[1024];

        @Override
        public void run() {
            while (datagramSocket_vital!=null){
                DatagramPacket packet=new DatagramPacket(buf,buf.length);
                try {
                    //���շ���˷�����UDP Packet
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
                    //��������Ϣ������λ�õ�����˼����Ƿ񱻴�
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
                    //����Ӣ�۱�������Ϣ
                    case Msg.HERO_BE_HURT:
                        int beHurtHeroId=Integer.parseInt(split[1].trim());
                        int bloodReduce=Integer.parseInt(split[2].trim());
                        for (Hero hero : gamePanel.getHeroes()) {
                            if (hero.getId()==beHurtHeroId){
                                //��Ѫ����ֱ��������
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
                    //����Ӣ��������Ϣ
                    case Msg.HERO_DIE_MSG:
                        int dieHeroId=Integer.parseInt(split[1].trim());
//                        System.out.println("����������Ϣ��"+dieHeroId+"_______________________________");
                        gamePanel.killHeroById(dieHeroId);
                        break;
                    //�����ʤ��Ϣ
                    case Msg.HERO_WIN_MSG:
                        int winnerId=Integer.parseInt(split[1].trim());
                        gamePanel.handleWinMsg(winnerId);
                        break;
                    //�����˳���Ϣ
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
