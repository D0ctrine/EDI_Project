package kr.co.nexmore.rfiddaemon.reader.util;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import kr.co.nexmore.netty.lib.annotation.Segment;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * 
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
public class ByteUnmarshaller {

    public static <T> T unmarshal(Class<T> classType, ByteBuf byteBuf){
        return (T) unmarshalInternal(classType, byteBuf);
    }

    public static <T> T unmarshal(Class<T> classType, ByteBuf byteBuf, String ... excludedFields){
        return (T) unmarshalInternal(classType, byteBuf, excludedFields);
    }

    private static <T> T unmarshalInternal(Class<T> clazz, ByteBuf byteBuf, String ... excludedFields){

        T instance = null;
        try{
            instance = clazz.newInstance();
        } catch ( InstantiationException | IllegalAccessException ex ){
            ex.printStackTrace();
        }
        assert instance != null;

        for ( Field field : fields(clazz) ){
            Segment segment = field.getAnnotation(Segment.class);

            if ( segment == null ){
                continue;
            }

            if ( ArrayUtils.contains(excludedFields, field.getName()) ){
                continue;
            }

            Segment.Type type = segment.type();
            try{
                if ( Segment.Type.INT.equals(type) ){
                    FieldUtils.writeField(instance, field.getName(), byteBuf.readInt(), true);
                } else if ( Segment.Type.CHAR.equals(type) ){
                    String value = readString(byteBuf, segment.size());
                    FieldUtils.writeField(instance, field.getName(), value, true);
                } else if ( Segment.Type.HEX.equals(type) ){
                    String value = readStringToHex(byteBuf, segment.size());
                    FieldUtils.writeField(instance, field.getName(), value, true);
                } else if ( Segment.Type.SHORT.equals(type) ){
                    FieldUtils.writeField(instance, field.getName(), byteBuf.readShort(), true);
                } else if ( Segment.Type.BYTE.equals(type) ){
                    FieldUtils.writeField(instance, field.getName(), byteBuf.readByte(), true);
                } else if ( Segment.Type.LONG.equals(type) ){
                    FieldUtils.writeField(instance, field.getName(), byteBuf.readLong(), true);
                }
            } catch ( IllegalAccessException ex ){
                ex.printStackTrace();
            }
        }

        return instance;
    }

    private static List<Field> fields(Class<?> clazz){
        List<Class<?>> classes = Lists.newArrayList(ClassUtils.getAllSuperclasses(clazz));
        classes.add(clazz);

        List<Field> fields = Lists.newArrayList();
        for ( Class<?> element : classes ){
            fields.addAll(Arrays.asList(element.getDeclaredFields()));
        }

        return fields;
    }

    public static String readString(ByteBuf byteBuf, int size){
        byte[] result = new byte[size];
        for ( int i = 0; i < size; i++ ){
            result[i] = byteBuf.readByte();
        }
        return new String(result).trim();
    }

    public static String readStringToHex(ByteBuf byteBuf, int size) {
        byte[] result = new byte[size];
        for ( int i = 0; i < size; i++ ){
            result[i] = byteBuf.readByte();
        }

        return ByteUtil.toHexString(result);
    }
}
