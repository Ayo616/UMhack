package com.example.demo;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.service.AliBass;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.events.Event;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import com.alibaba.fastjson.JSON;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

@RestController
public class MyControll {

    AliBass aliBass = new AliBass();

    @RequestMapping(value = "/id", method = RequestMethod.GET)
    public String receive(){
        return "d";
    }

    @PostMapping("/post")
    public void createUserByMap(@RequestBody String params){
        JSONObject jsonObject = JSONObject.parseObject(params);
        System.out.println("Your Message : " + jsonObject.getString("tel"));
    }

    @PostMapping("/invoke")
    @ResponseBody
    public String invokeInfomation(@RequestBody String params){
        JSONObject jsonObject = JSONObject.parseObject(params);
        String action = jsonObject.getString("action").toString();
        String data = jsonObject.getString("data").toString();

//        data = JSON.toJSONString(jsonObject.getString("data"));
//        String a = new String("");
        System.out.println("action : " + action);
        System.out.println("data : " + data);
        Collection<ProposalResponse> results  = null;
        try {
            results = aliBass.executeChaincode(action,data);
        }  catch (Exception e) {
//            logger.error("exception", e);
        }
        System.out.println("results : " + results);
        return "sucessfull";
    }

    @PostMapping("/query")
    public String queryInfomation(@RequestBody String params){
        JSONObject jsonObject = JSONObject.parseObject(params);
        String id = jsonObject.getString("id").toString();
        System.out.println("Request query id : " + id);
        String result = null;
        try {
            result =  aliBass.executeChaincode(id);
        }  catch (Exception e) {
//            logger.error("exception", e);
        }
//        JSONObject jsonresult = JSON.parseObject(result);
//        aliBass.getBlockInfo();
        return result;
    }


    @PostMapping("/update")
    @ResponseBody
    public String updateInfomation(@RequestBody String params){
        JSONObject jsonObject = JSONObject.parseObject(params);
        String action = jsonObject.getString("action").toString();
        String name = jsonObject.getString("name").toString();
        String id = jsonObject.getString("id").toString();
        String data = jsonObject.getString("data").toString();

//        data = JSON.toJSONString(jsonObject.getString("data"));
//        String a = new String("");
        System.out.println("action : " + action);
        System.out.println("data : " + data);
        System.out.println("name : " + name);
        System.out.println("id : " + id);

        Collection<ProposalResponse> results  = null;
        try {
            results = aliBass.executeChaincode(action,name,id,data);
        }  catch (Exception e) {
//            logger.error("exception", e);
        }
        System.out.println("results : " + results);
        return "sucessfull";
    }
}
