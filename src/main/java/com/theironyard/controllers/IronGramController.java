package com.theironyard.controllers;

import com.sun.deploy.net.HttpResponse;
import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import com.theironyard.services.PhotoRepository;
import com.theironyard.services.UserRepository;
import com.theironyard.utils.PasswordStorage;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.List;

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
    public User login(HttpSession session, String name, String password, HttpServletResponse response) throws Exception {
        User user = users.findByName(name);
        if(user==null){
            user = new User(name, PasswordStorage.createHash(password));
            users.save((user));
        }else if(!PasswordStorage.verifyPassword(password, user.getPasswordHash())){
            throw new Exception("Wrong Password");
        }
        session.setAttribute("userName", name);
        response.sendRedirect("/");
        return user;
    }
    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public User getUser(HttpSession session){
        String userName = (String) session.getAttribute("userName");
        return users.findByName(userName);
    }
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public Photo upload(MultipartFile photo, HttpSession session, HttpServletResponse response) throws Exception {
        String userName = (String) session.getAttribute("userName");
        if (userName == null){
            throw new Exception("Not logged in");
        }
        User user = users.findByName(userName);
        File photoFile = File.createTempFile("image", photo.getOriginalFilename(), new File("public"));
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());
        Photo p = new Photo(user, null, photoFile.getName());
        photos.save(p);
        response.sendRedirect("/");
        return p;
    }
    @RequestMapping(path = "/photos", method = RequestMethod.GET)
    public List<Photo> showPhotos(){
        return (List<Photo>) photos.findAll();
    }

}
