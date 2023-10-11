package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.repository.ModuleHistoryRepository;
import com.newlife.Connect_multiple.service.IModuleHistoryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;

public class test {
    public static void main(String[] args) {
        IModuleHistoryService moduleHistoryService;

        Integer idProbe = 1;
        Date timeBefore = Date.valueOf("2023-10-01");
        Date timeAfter = Date.valueOf("2023-10-08");
        String status = "failed";
        String mess = null;
        //mess = moduleHistoryService.solveEPW(idProbe);
       // mess = moduleHistoryService.solveErrorPerWeekOfModule(idProbe, timeBefore, timeAfter, status);
        System.out.println(mess);
    }
}
