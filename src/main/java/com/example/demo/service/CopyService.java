package com.example.demo.service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.Course;
import org.apache.commons.io.IOUtils;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


public class CopyService {

    public static void set() {
        AliBass aliBass = new AliBass();
        String action = "addSports";
        Collection<ProposalResponse> results  = null;
        String connectionProfilePath =System.getProperty("user.dir")+"/src/main/baas/P_Sports.txt";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(connectionProfilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String text = null;
        try {
            text = IOUtils.toString(inputStream, "utf8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray courses = JSON.parseArray(text);
        for(Object element :courses){
            JSONObject object = new JSONObject();
            object.put("data", element);
            System.out.println(object.toString());
            try {
                results = aliBass.executeChaincode(action,object.getString("data").toString());
                Thread.sleep(5000);
            } catch (ProposalException e) {
                e.printStackTrace();
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
//        System.out.println(courses);

    }

    public static void main(String args[]){
        set();
    }
}
