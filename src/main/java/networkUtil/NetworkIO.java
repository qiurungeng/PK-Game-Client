package networkUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Java为Socket封装的函数并不适用于C的socket！！！！
 * 就是说端通过DataInputStream、DataOutputString进行readXX( ),
 * writeXX( )无法与C服务端进行通信，两方接收的都是乱码。
 * 所以想要readInt()/writeInt(),就得Int ←→ String ←→ byte[]
 */
public class NetworkIO {

    /**
     * ASC2码字节数组转Int哈哈哈淦终于绕道解决C与Java的Socket通信传int值大小端不一的问题了浪费老子一天靠哈哈哈蛤蛤嘿嘿嘿
     * @param bytes
     * @return
     */
    private static Integer ASC2ToInt(byte[] bytes){
        String intString=new String(bytes);
        return Integer.valueOf(intString.trim());
    }

    public static int byteArrayToInt(byte[] bytes) {
        int value=0;
        for(int i = 0; i < 4; i++) {
            int shift= (3-i) * 8;
            value +=(bytes[i] & 0xFF) << shift;
        }
        return value;
    }

    public static int myReadInt(DataInputStream dis){
        byte[] buff=new byte[4];
        try {
            int read=dis.read(buff);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ASC2ToInt(buff);
    }
    
    public static int myReadInt(Socket s){
        DataInputStream dis = null;
        try{
            dis=new DataInputStream(s.getInputStream());
        }catch (IOException e){
            e.printStackTrace();
        }
        assert dis != null;
        return myReadInt(dis);
    }

    public static void myWriteInt(DataOutputStream dos, int to_write){
        String write=""+to_write;
        try {
            dos.write(write.getBytes("GBK"));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
