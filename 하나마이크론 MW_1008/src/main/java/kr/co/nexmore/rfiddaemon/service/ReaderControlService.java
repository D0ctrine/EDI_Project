package kr.co.nexmore.rfiddaemon.service;

import kr.co.nexmore.rfiddaemon.reader.tcp.client.RFIDTcpClient;
import kr.co.nexmore.rfiddaemon.vo.reader.tcp.request.RequestVO;
import kr.co.nexmore.rfiddaemon.vo.reader.udp.UdpResponseVO;

import java.io.File;
import java.util.LinkedHashMap;

public interface ReaderControlService {

//    List sendCommande(RequestCommand command, RequestHeaderVO headerVO, String param, String readerIp, boolean save, LinkedHashMap<String, RFIDTcpClient> clientMap) throws InterruptedException;
//    LinkedHashMap sendTcpCommand(RequestVO requestVO, LinkedHashMap<String, RFIDTcpClient> clientMap) throws InterruptedException;
//    LinkedHashMap sendTcpCommand(RequestVO requestVO, LinkedHashMap<String, RFIDTcpClient> clientMap, boolean save) throws InterruptedException;

    LinkedHashMap sendTcpCommand(RequestVO requestVO, RFIDTcpClient client) throws InterruptedException;
    LinkedHashMap sendTcpCommand(RequestVO requestVO, RFIDTcpClient client, boolean save) throws InterruptedException;

    UdpResponseVO searchReader(String readerIp) throws InterruptedException;

    UdpResponseVO setReaderIp(String readerMac, String readerIp, String changeIp, String gateway, UdpResponseVO searchResponse) throws InterruptedException;

    LinkedHashMap firmwareUpdate(File file, String readerMac, String readerIp) throws InterruptedException;

//    void coreUpdate(File binFile, File dddFile, File firmwareFile, String readerMac, String readerIp);

//    void test();
}
