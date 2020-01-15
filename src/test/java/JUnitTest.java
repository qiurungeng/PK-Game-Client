import static org.junit.Assert.*;

import networkUtil.CSocketMsgBuilder;
import org.junit.Test;


public class JUnitTest {
    @Test
    public void testStringBuilder(){
        CSocketMsgBuilder msgBuilder=new CSocketMsgBuilder();
        msgBuilder.addInt(4);
        msgBuilder.addInt(100);
        msgBuilder.addInt(512);
        msgBuilder.addInt(220);
        msgBuilder.addBoolean(true);
        System.out.println(msgBuilder.getSendMsg());
    }

}
