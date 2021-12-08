package kr.co.nexmore.rfiddaemon;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import kr.co.nexmore.rfiddaemon.mes.Oneoone;
import kr.co.nexmore.rfiddaemon.mes.OneooneManager;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.RFIDTcpClient;
import kr.co.nexmore.rfiddaemon.reader.tcp.client.manager.ClientManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import static kr.co.nexmore.rfiddaemon.reader.tcp.client.manager.ClientManager.rfidClientsMapByIp;


@Slf4j
@Component("applicationManager")
public class ApplicationManager implements CommandLineRunner, ApplicationListener<ContextClosedEvent>, InitializingBean, DisposableBean {

    private final ClientManager clientManager;
    private final OneooneManager oneooneManager;
    private final HikariDataSource dataSource;

    public ApplicationManager(ClientManager clientManager, HikariDataSource dataSource, OneooneManager oneooneManager) {
        this.clientManager = clientManager;
        this.dataSource = dataSource;
        this.oneooneManager = oneooneManager;
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void run(String... args) throws Exception {

    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        log.info("[SHUTDOWN] RFID M/W DAEMON APPLICATION을 종료합니다.");
        LinkedHashMap<String, RFIDTcpClient> clients = clientManager.getClientsMap();
        if (clients != null && !clients.isEmpty()) {
            for (String key : clients.keySet()) {
                clients.get(key).shutdown();
//                clients.get(key).getMessageHandler().
            }
            clients.clear();
            rfidClientsMapByIp.clear();
        }

        try {
            TimeUnit.MILLISECONDS.sleep(3000);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }


        if (clients == null || clients.isEmpty()) {
            HikariPoolMXBean poolBean = dataSource.getHikariPoolMXBean();
            while (poolBean.getActiveConnections() > 0) {
                log.debug(String.valueOf(poolBean.getActiveConnections()));
                poolBean.softEvictConnections();
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (Exception e) {
                    log.error(e.toString(), e);
                }
            }
            dataSource.close();
        }

        Hashtable<Integer, Oneoone> oneooneMap = oneooneManager.getOneooneMap();
        if (oneooneMap != null && !oneooneMap.isEmpty()) {
            for (int key : oneooneMap.keySet()) {
                oneooneMap.get(key).disconnect();
            }
        }
    }
}
