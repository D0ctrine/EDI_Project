package kr.co.nexmore.rfiddaemon.vo.reader.tcp.request;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RequestVO implements Serializable {
    private static final long serialVersionUID = 4044738891483110229L;

    RequestHeaderVO headerVO;
    Object body;
}
