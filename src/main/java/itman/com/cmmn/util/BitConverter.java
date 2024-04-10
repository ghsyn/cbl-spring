package itman.com.cmmn.util;

/**
 * @Comment: c#의 BitConverter와 같은 기능을 할 수 있도록 험수 구현. 현재는 사용중인 함수만 구현되었음 필요시 추가
 * @author: ybwhyb
 * @since: 2024-01-20
 * */
public class BitConverter {

    /**
     * @comment: byte[] to float 변환
     * @param: byte[]
     * @param: int
     * @return: float
     * */
    public static float toSingle(byte[] bytes, int offset) {
        return Float.intBitsToFloat(toInt32(bytes, offset));
    }

    /**
     * @comment: byte[] to short로 변환(16bit)
     * @param: byte[]
     * @param: int
     * @return: short
     * */
    public static short toInt16(byte[] bytes, int offset) {
        return (short) ((bytes[offset] & 0xFF) | ((bytes[offset + 1] & 0xFF) << 8));
    }

    /**
     * @comment: byte[] to int(32bit)
     * @param: byte[]
     * @param: int
     * @return: int
     * */
    public static int toInt32(byte[] bytes, int offset) {
        return (bytes[offset] & 0xFF) | ((bytes[offset + 1] & 0xFF) << 8) | ((bytes[offset + 2] & 0xFF) << 16) | ((bytes[offset + 3] & 0xFF) << 24);
    }
}
