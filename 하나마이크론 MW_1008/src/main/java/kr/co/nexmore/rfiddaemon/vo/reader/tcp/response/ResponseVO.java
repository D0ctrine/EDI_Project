package kr.co.nexmore.rfiddaemon.vo.reader.tcp.response;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResponseVO implements Serializable {
    private static final long serialVersionUID = 9196764564123477044L;

    ResponseHeaderVO headerVO;
    Object body;
}
