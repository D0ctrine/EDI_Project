package kr.co.nexmore.rfiddaemon.reader.tcp.client.manager;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import kr.co.nexmore.rfiddaemon.mes.OneooneManager;
import kr.co.nexmore.rfiddaemon.service.DBManageService;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.RFIDTcpClient;
import kr.co.nexmore.rfiddaemon.vo.reader.ReaderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component("clientManager")
public class ClientManager {

    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final LinkedHashMap<String, RFIDTcpClient> rfidClientsMapByMac = new LinkedHashMap<>();  // key: MAC
    public static final LinkedHashMap<String, RFIDTcpClient> rfidClientsMapByIp = new LinkedHashMap<>();   // key: IP -> IP 로 client를 찾기 위함.(MES write, lot end, plasma 공정 pair 찾기 위함.)

    private final DBManageService dbManageService;
    private final OneooneManager oneooneManager;
    private final ClientLogManager clientLogManager;


    public ClientManager(DBManageService dbManageService, OneooneManager oneooneManager, ClientLogManager clientLogManager) {
        this.dbManageService = dbManageService;
        this.oneooneManager = oneooneManager;
        this.clientLogManager = clientLogManager;

    }

    public LinkedHashMap<String, RFIDTcpClient> getClientsMap() {
        return this.rfidClientsMapByMac;
    }

    public synchronized void removeClient(String readerMac) {
        RFIDTcpClient client = this.getClient(readerMac);
        String readerIp = client.getReaderVO().getReaderIp();
        log.debug("[{}({})] client remove", client.getReaderVO().getReaderName(), readerIp);
        rfidClientsMapByMac.remove(readerMac);
        rfidClientsMapByIp.remove(readerIp);
    }

    public RFIDTcpClient getClient(String readerMac) {
        return this.rfidClientsMapByMac.get(readerMac);
    }

    public RFIDTcpClient addClient(ReaderVO readerVO) {
        log.debug("New Client add to rfidClientsMap");
        log.info("새로운 리더기 CLIENT 생성합니다.");

        RFIDTcpClient client = new RFIDTcpClient(readerVO, dbManageService, oneooneManager, clientLogManager);
        rfidClientsMapByMac.put(readerVO.getReaderMac(), client);
        rfidClientsMapByIp.put(readerVO.getReaderIp(), client);
        client.start();
        return rfidClientsMapByMac.get(readerVO.getReaderMac());
    }


    /**
     * DB의 리더기 테이블에서 리더기 정보를 조회 후
     * 조회된 리더기 정보로 RFIDTcpClient를 생성한다.
     * 생성한 RFIDTcpClient는 rfidClientMap에 값을 저장한다.
     */
    @PostConstruct
    public void getReaderList() {
        List<ReaderVO> readerList = new ArrayList<>();

        try {
            log.info("전체 리더기 정보 리스트 조회 START");
            readerList = dbManageService.getReaderList();
        } catch (Exception e) {
            log.error("전체 리더기 정보 리스트 조회 실패");
            log.error(e.toString(), e);
        }

        // 리더기와 연결할 client 생성
        if (readerList != null && !readerList.isEmpty()) {
            for (ReaderVO vo : readerList) {
                RFIDTcpClient client = new RFIDTcpClient(vo, dbManageService, oneooneManager, clientLogManager);
                rfidClientsMapByMac.put(vo.getReaderMac(), client);
                rfidClientsMapByIp.put(vo.getReaderIp(), client);
            }
        }

        if (!rfidClientsMapByMac.isEmpty()) {
            int clientCnt = rfidClientsMapByMac.size();
            int second = 5;

            if ((clientCnt / 100) < 5) {
                second = (clientCnt / 100);
            }

            if (second == 0) {
                second = 1;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(second * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 리더기 연결 시작
            int connectionCnt = 0;
            for (String readerMac : rfidClientsMapByMac.keySet()) {
                // count: 250 마다 3초 sleep 후 connection
                if (connectionCnt > 250) {
                    if ((connectionCnt % 250) == 0) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(3000);
                        } catch (InterruptedException e) {
                            log.error(e.toString(), e);
                        }
                    }
                }

                rfidClientsMapByMac.get(readerMac).start();
                connectionCnt++;
            }
        }
    }
}
