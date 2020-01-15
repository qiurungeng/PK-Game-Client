package networkUtil;

public class CSocketMsgBuilder {

    private String sendMsg;

    public CSocketMsgBuilder() {
        sendMsg="";
    }

    public void addInt(int i){
        if (!sendMsg.equals("")){
            sendMsg+="|";
        }
        sendMsg+=i;
    }

    public void add(Object o){
        if (!sendMsg.equals("")){
            sendMsg+="|";
        }
        sendMsg+=o.toString();
    }

    public void addBoolean(boolean b){
        if (!sendMsg.equals("")){
            sendMsg+="|";
        }
        if (b){
            sendMsg+="1";
        }else sendMsg+="0";
    }

    public String getSendMsg(){
        return sendMsg;
    };
}
