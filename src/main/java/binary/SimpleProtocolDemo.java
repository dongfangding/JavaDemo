package binary;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>演示简单版使用二进制byte数组来完成一个简单的协议头约定</p >
 *
 * @author Snowball
 * @version 1.0
 * @date 2020/08/04 16:16
 */
public class SimpleProtocolDemo {

    /**
     * 存储协议头 使用10个byte存储，下面描述一下各个byte位的有效含义
     * 0    1个byte位，有效存储值为0和1， 0代表请求， 1代表响应
     * 1~7  7个byte位，不考虑符号位，有效值为0~127，用于代表报文的指令码，指令码决定着报文的处理方式，这是提前定义好的。这也意味这目前这个协议最大支持127个指令开发
     * 8~9  2个byte位，有效值为0~3，代表消息希望的处理方式。 0 代表消息是一次性的，处理失败也无须继续处理  1 代表消息必须处理成功，但不必通知发送方 2 代销消息必须成功且必须通知发送方结果
     *
     */
    private final byte[] head = new byte[10];

    /**
     * 指令码开始的标志位
     */
    private final int cmdBeginIndex = 1;

    /**
     * 指令码结束时的标识位
     */
    private final int cmdEndIndex = 7;

    /**
     * ops开始的标志位
     */
    private final int opsBeginIndex = 8;

    /**
     * ops结束的标志位
     */
    private final int opsEndIndex = 9;


    public static void main(String[] args) {
        SimpleProtocolDemo demo = new SimpleProtocolDemo();
        demo.setType(true);
        demo.setCmd(Cmd.BONG);
        demo.setOps(MsgOps.MUST_SUCCESS_NOTIFY);

        System.out.println("编排指令后的二进制数组为: " + demo.headToString());

        System.out.println("从二进制数组中解析报文原意");
        System.out.println("是否为请求: " + demo.isRequest());
        System.out.println("指令码: " + demo.getCmd());
        System.out.println("消息质量: " + demo.getMsgOps());

        // 这里只用了10个bit就完成了语义的详细解释，但是如果用字符的话，一个指令码不同的命名就占用了好几个字节，再加上存储语义的格式，
        // 可以看到使用byte数组利用二进制位数存储的数字含义可以极大的缩减报文头的字节数
        // 当然最终报文body部分就要看使用方自己决定了

    }

    private String headToString() {
        return Arrays.toString(head);
    }

    /**
     * 设置第0位请求类型
     * @param isRequest
     */
    public void setType(boolean isRequest) {
        head[0] = isRequest ? (byte) 0 : (byte) 1;
    }

    /**
     * 解析协议第0位，是否为请求
     * @return
     */
    public boolean isRequest() {
        return head[0] == 0;
    }

    /**
     * 将指令对应的二进制，先倒序指令二进制，同样倒序放入到存放指令标志位
     * 如100对应的二进制为0110 0100
     * 则从右开始向左依次放入存放指令标识位的最末位，然后逐步前移
     * 也就是最右边的0放入到存放标志位的最后一位
     * 然后往左遍历移动一位，同时标志位也从最后一位往前移动一位
     * 只有这样最后取出来的二进制的值才是正确的
     */
    public void setCmd(Cmd cmd) {
        String cmdBinaryString = cmd.getCmdBinaryString();
        int cmdBinaryLength = cmdBinaryString.length();
        for (int i = cmdEndIndex; i >= cmdBeginIndex; i --) {
            // 如果指令的二进制长度小于填充位的总长度，则不必往前填充了
            // 类似如果指令为3，二进制则为0011，有效位为2位，前面都是0，就没必要遍历了，只要遍历两次即可
            if (cmdEndIndex - i > cmdBinaryLength - 1) {
                break;
            }
            // 倒序指令二进制，然后从标志位最末位往前依次放入
            head[i] = Byte.parseByte(String.valueOf(cmdBinaryString.charAt(cmdBinaryLength - 1 - (cmdEndIndex - i))));
        }
    }

    /**
     * 从报文头中解析出指令码
     * @return
     */
    public Cmd getCmd() {
        byte[] cmdByte = Arrays.copyOfRange(head, cmdBeginIndex, cmdEndIndex + 1);
        return Cmd.getByValue(binaryToDec(cmdByte));
    }

    public void setOps(MsgOps ops) {
        String opsBinaryString = ops.getOpsBinaryString();
        int opsBinaryLength = opsBinaryString.length();
        for (int i = opsEndIndex; i >= opsBeginIndex; i --) {
            if (opsEndIndex - i > opsBinaryLength - 1) {
                break;
            }
            head[i] = Byte.parseByte(String.valueOf(opsBinaryString.charAt(opsBinaryLength - 1 - (opsEndIndex - i))));
        }
    }


    /**
     * 从报文头中解析出指令码
     * @return
     */
    public MsgOps getMsgOps() {
        byte[] cmdByte = Arrays.copyOfRange(head, opsBeginIndex, opsEndIndex + 1);
        return MsgOps.getByValue(binaryToDec(cmdByte));
    }

    enum MsgOps {

        /**
         * 代表消息是一次性的，处理失败也无须继续处理
         */
        MOST_ONCE(0),


        /**
         * 代表消息必须处理成功，但不必通知发送方
         */
        MUST_SUCCESS(1),

        /**
         * 代销消息必须成功且必须通知发送方结果
         */
        MUST_SUCCESS_NOTIFY(2);

        private final static Map<Integer, MsgOps> VALUE_MAP;

        static {
            VALUE_MAP = new HashMap<>(values().length);
            for (MsgOps value : MsgOps.values()) {
                VALUE_MAP.put(value.getOps(), value);
            }
        }

        Integer ops;
        MsgOps(Integer ops) {
            this.ops = ops;
        }

        public Integer getOps() {
            return ops;
        }

        public String getOpsBinaryString() {
            return Integer.toBinaryString(ops);
        }


        public static MsgOps getByValue(Integer value) {
            return VALUE_MAP.get(value);
        }
    }

    enum Cmd {
        /**
         * 指令吗对应的值
         */
        PING(0),

        PONG(1),

        ECHO(2),

        PRINT(3),

        BONG(100)
        ;

        private final static Map<Integer, Cmd> VALUE_MAP;

        static {
            VALUE_MAP = new HashMap<>(values().length);
            for (Cmd value : Cmd.values()) {
                VALUE_MAP.put(value.getCmd(), value);
            }
        }

        Integer cmd;
        Cmd(Integer cmd) {
            this.cmd = cmd;
        }

        public Integer getCmd() {
            return cmd;
        }

        public String getCmdBinaryString() {
            return Integer.toBinaryString(cmd);
        }

        public static Cmd getByValue(Integer value) {
            return VALUE_MAP.get(value);
        }
    }


    /**
     * 二进制数组转换为十进制
     * @param bytes
     * @return
     */
    public int binaryToDec(byte[] bytes) {
        int total = 0;
        int loop = 0;
        for (int i = bytes.length - 1; i >= 0; i --) {
            if (bytes[i] != 0) {
                total += Math.pow(2d, loop);
            }
            loop ++;
        }
        return total;
    }
}
