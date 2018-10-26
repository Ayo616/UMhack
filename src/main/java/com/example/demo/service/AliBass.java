package com.example.demo.service;

import chaincode.invocation.QueryChaincode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAInfo;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InfoException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AliBass {

    private static final Log logger = LogFactory.getLog(AliBass.class);
    private static final long waitTime = 6000;
    private static String connectionProfilePath =System.getProperty("user.dir")+"/src/main/baas/connection-profile-standard.yaml";
    private static String channelName = "first-channel";
    private static String userName = "admin";
    private static String secret = "Admin123";
    private static String chaincodeName = "dd";
//    private static String chaincodeName = "sacc";

    private static String chaincodeVersion = "1.0";
    private HFClient client = null;
    private Channel channel = null;


    public AliBass() {
        this.init();
    }

    public String getBlockInfo(){
        try {
            System.out.println(channel.queryBlockchainInfo().getPreviousBlockHash().toString());
            System.out.println(channel.queryBlockchainInfo().getHeight());
            System.out.println(channel.queryBlockchainInfo().getCurrentBlockHash().toString());

            return channel.queryBlockchainInfo().toString();
        } catch (ProposalException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        return "dead";
    }

    public void init() {
        System.out.println(System.getProperty("user.dir")+"/src/main/baas/connection-profile-standard.yaml");
        File f = new File(connectionProfilePath);
        try {
            //配置网络
            NetworkConfig networkConfig = NetworkConfig.fromYamlFile(f);
            //网络组织信息(赋值内部类)
            NetworkConfig.OrgInfo clientOrg = networkConfig.getClientOrganization();
            //网络CA信息(赋值内部类)
            NetworkConfig.CAInfo caInfo = clientOrg.getCertificateAuthorities().get(0);

            //得到User对象
            FabricUser user = getFabricUser(clientOrg, caInfo);
            //初始化真正客户端
            client = HFClient.createNewInstance();
            client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
            client.setUserContext(user);


            //创建通道
            channel = client.loadChannelFromConfig(channelName, networkConfig);

            //service discovery function.
            //Peer p = channel.getPeers().iterator().next();
            //channel.removePeer(p);
            //channel.addPeer(p, Channel.PeerOptions.createPeerOptions().addPeerRole(Peer.PeerRole.SERVICE_DISCOVERY));
            //Collection<String> cc = channel.getDiscoveredChaincodeNames();

            channel.initialize();

            channel.registerBlockListener(blockEvent -> {

                logger.info(String.format("Receive block event (number %s) from %s", blockEvent.getBlockNumber(), blockEvent.getPeer(),blockEvent.getPreviousHash()));
            });
        } catch (Exception e) {
            logger.error("exception", e);
        }


    }

//    执行方法
    public Collection<ProposalResponse> executeChaincode(String action, String data) throws
            ProposalException, InvalidArgumentException, UnsupportedEncodingException, InterruptedException,
            ExecutionException, TimeoutException {
//        String contract = "1q534";
        ChaincodeExecuter executer = new ChaincodeExecuter(chaincodeName, chaincodeVersion);
        return executer.executeTransaction(client, channel, true, action,data);
    }


    //    执行方法
    public Collection<ProposalResponse> executeChaincode(String action, String ObjName,String ObjId,String data) throws
            ProposalException, InvalidArgumentException, UnsupportedEncodingException, InterruptedException,
            ExecutionException, TimeoutException {
//        String contract = "1q534";
        ChaincodeExecuter executer = new ChaincodeExecuter(chaincodeName, chaincodeVersion);
        return executer.executeTransaction(client, channel, true, action,ObjName,ObjId,data);
    }

//  查询方法
    public String executeChaincode(String id) throws
            ProposalException, InvalidArgumentException, UnsupportedEncodingException, InterruptedException,
            ExecutionException, TimeoutException {
        ChaincodeExecuter executer = new ChaincodeExecuter(chaincodeName, chaincodeVersion);
        Collection<ProposalResponse> responses1Query = executer.executeTransaction(client, channel, false,"query",id);

        //收集来自多个peer的请求结果
        ArrayList<String> ResultList = new ArrayList<>();
        for (ProposalResponse pres : responses1Query) {
            String stringResponse = new String(pres.getChaincodeActionResponsePayload());
            Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, stringResponse);
            System.out.println("queryResult :"+ stringResponse);
            ResultList.add(stringResponse);
        }
        return ResultList.get(0);
    }

    private FabricUser getFabricUser(NetworkConfig.OrgInfo clientOrg, NetworkConfig.CAInfo caInfo) throws
            MalformedURLException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, InfoException,
            EnrollmentException {

        //创建CA客户端
        HFCAClient hfcaClient = HFCAClient.createNewInstance(caInfo);
        HFCAInfo cainfo = hfcaClient.info();
        logger.info("CA name: " + cainfo.getCAName());
        logger.info("CA version: " + cainfo.getVersion());

        // Persistence is not part of SDK.
        //用CA对象去登录一个用户
        logger.info("Going to enroll user: " + userName);
        Enrollment enrollment = hfcaClient.enroll(userName, secret);
        logger.info("Enroll user: " + userName +  " successfully.");

        //生成User对象
        FabricUser user = new FabricUser();
        user.setMspId(clientOrg.getMspId());
        user.setName(userName);
        user.setOrganization(clientOrg.getName());
        user.setEnrollment(enrollment);
        return user;
    }


    public void blockinfo(){

    }
}
