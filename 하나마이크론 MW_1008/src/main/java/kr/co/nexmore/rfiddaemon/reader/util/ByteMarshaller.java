package kr.co.nexmore.rfiddaemon.reader.util;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import kr.co.nexmore.netty.lib.annotation.Segment;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author
 * @version 1.0
 * @see <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일          수정자        수정내용
 *  -----------   ------------    ---------------------------
 *   2017-11-13         최초 생성
 * </pre>
 */
public class ByteMarshaller {

    public static void marshal(ByteBuf byteBuf, Object obj) {
        marshalInternal(byteBuf, obj);
    }

    public static void marshal(ByteBuf byteBuf, Object obj, String... excludedFields) {
        marshalInternal(byteBuf, obj, excludedFields);
    }

    public static void marshalInternal(ByteBuf out, Object obj, String... excludedFields) {
        try {
            List<Field> fields = fields(obj.getClass());

            for (Field field : fields) {
                Segment segment = field.getAnnotation(Segment.class);
                if (segment == null) {
                    continue;
                }

                if (ArrayUtils.contains(excludedFields, field.getName())) {
                    continue;
                }

/*                if ("search".equals(command)) {
                    if(field.getName().equals("readerMac") || field.getName().equals("readerIp")) {
                        continue;
                    }
                }*/

                Segment.Type type = segment.type();

                if (Segment.Type.INT.equals(type)) {
                    out.writeInt((Integer) FieldUtils.readField(obj, field.getName(), true));
                } else if (Segment.Type.CHAR.equals(type)) {
                    String value = Objects.toString(FieldUtils.readField(obj, field.getName(), true), "");
                    writeString(out, value, segment.size(), segment.isString());
                } else if (Segment.Type.SHORT.equals(type)) {
//                    out.writeShort((Short) FieldUtils.readField(obj, field.getName(), true));
                    out.writeShort((Integer) FieldUtils.readField(obj, field.getName(), true));
                } else if (Segment.Type.BYTE.equals(type)) {
                    out.writeByte((Byte) FieldUtils.readField(obj, field.getName(), true));
                } else if (Segment.Type.HEX.equals(type)) {
                    String value = Objects.toString(FieldUtils.readField(obj, field.getName(), true), "");
                    writeHexString(out, value);
                } else if (Segment.Type.IP.equals(type)) {
                    String value = Objects.toString(FieldUtils.readField(obj, field.getName(), true), "");
//                    writeIp(out, value);
                } else {
                    writeHexString(out, obj.toString());
                }

            }
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

/*    public static void unmarshal(ByteBuf out, Object obj){
        try{
            List<Field> fields = fields(obj.getClass());

            for ( Field field : fields ){
                Segment segment = field.getAnnotation(Segment.class);
                if ( segment == null ){
                    continue;
                }

                Segment.Type type = segment.type();

                if ( Segment.Type.INT.equals(type) ){
                        out.writeInt((Integer) FieldUtils.readField(obj, field.getName(), true));
                } else if ( Segment.Type.CHAR.equals(type) ){
                    String value = Objects.toString(FieldUtils.readField(obj, field.getName(), true), "");
                    writeString(out, value, segment.size(), segment.isString());
                } else if ( Segment.Type.SHORT.equals(type) ){
//                    out.writeShort((Short) FieldUtils.readField(obj, field.getName(), true));
                    out.writeShort((Integer) FieldUtils.readField(obj, field.getName(), true));
                } else if ( Segment.Type.BYTE.equals(type) ){
                    out.writeByte((Byte) FieldUtils.readField(obj, field.getName(), true));
                } else if ( Segment.Type.HEX.equals(type) ){
                    String value = Objects.toString(FieldUtils.readField(obj, field.getName(), true), "");
                    writeHexString(out, value);
                } else {
                    writeHexString(out, obj.toString());
                }

            }
        } catch ( IllegalAccessException ex ){
            ex.printStackTrace();
        }
    }*/

    private static List<Field> fields(Class<?> clazz) {
        List<Class<?>> classes = Lists.newArrayList(ClassUtils.getAllSuperclasses(clazz));
        classes.add(clazz);

        List<Field> fields = Lists.newArrayList();
        for (Class<?> element : classes) {
            fields.addAll(Arrays.asList(element.getDeclaredFields()));
        }

        return fields;
    }

    private static void writeString(ByteBuf byteBuf, String value, int size, boolean isString) {
        if (isString) {
            byteBuf.writeBytes(generateByteArrayForTrans(value, size));
        } else {
            byteBuf.writeBytes(StringUtils.rightPad(StringUtils.substring(value, 0, size), size).getBytes());
        }
    }

    private static void writeHexString(ByteBuf byteBuf, String value) {
        byteBuf.writeBytes((ByteUtil.toBytesFromHexString(value)));
    }


/*
    private static void writeIp(ByteBuf byteBuf, String value) {
        String[] arry = value.split("\\.");
        byteBuf.writerIndex(byteBuf.writerIndex() + 1);
        for (int i = 0; i < arry.length; i++) {
            byteBuf.writeByte((byte) Integer.parseInt(arry[i]));
        }
    }
*/

    public static byte[] generateByteArrayForTrans(String value, int size) {
        byte[] result = new byte[size];
        Arrays.fill(result, (byte) 0);

        if (StringUtils.isNotEmpty(value)) {
            byte[] valueAsByteArray = value.getBytes();

            for (int index = 0, max = valueAsByteArray.length; index < max; index++) {
                if (index == (size - 2)) {
                    break;
                }
                result[index] = valueAsByteArray[index];
            }
        }
        return result;
    }
}
