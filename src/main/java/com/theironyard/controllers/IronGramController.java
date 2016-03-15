package com.theironyard.controllers;

import com.theironyard.entities.User;
import com.theironyard.services.PhotoRepository;
import com.theironyard.services.UserRepository;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

/**
 * Created by PiratePowWow on 3/15/16.
 */
@RestController
public class IronGramController {
    Server dbui = null;
    @PostConstruct
    public void init() throws SQLException {
            dbui = Server.createWebServer().start();
    }
    @PreDestroy
    public void destroy(){
        dbui.stop();
    }
    @Autowired
    UserRepository users;
    @Autowired
    PhotoRepository photos;

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public User login(HttpSession session, String name, String password){
        return null;
    }
    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public User getUser(HttpSession session){
        String userName = (String) session.getAttribute("userName");
        return users.findByName(userName);
    }

}
