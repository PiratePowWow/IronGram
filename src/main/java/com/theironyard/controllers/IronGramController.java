package com.theironyard.controllers;

import com.sun.deploy.net.HttpResponse;
import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import com.theironyard.services.PhotoRepository;
import com.theironyard.services.UserRepository;
import com.theironyard.utils.PasswordStorage;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.management.FileSystem;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.sql.Array;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    public Photo upload(MultipartFile photo, HttpSession session, HttpServletResponse response, Integer duration) throws Exception {
        String userName = (String) session.getAttribute("userName");
        if (userName == null){
            throw new Exception("Not logged in");
        }
        User user = users.findByName(userName);
        File photoFile = File.createTempFile("image", photo.getOriginalFilename(), new File("public"));
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());
        Photo p = new Photo(user, null, photoFile.getName(), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString(), duration==null?10:duration);
        photos.save(p);
        response.sendRedirect("/");
        return p;
    }
    @RequestMapping(path = "/photos", method = RequestMethod.GET)
    public ResponseEntity<Object> showPhotos() throws IOException {
        for(Photo photo: photos.findAll()){
            if (Duration.between(LocalDateTime.parse(photo.getTimeStamp()), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)).get(ChronoUnit.SECONDS) > photo.getDuration()){
                try {
                    Files.delete(FileSystems.getDefault().getPath("public", photo.getFilename()));
                }catch (IOException x){
                    return new ResponseEntity<Object>(x, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                photos.delete(photo);
            }
        }
        ArrayList<Photo> allPhotos = new ArrayList<Photo>();
        List<Photo> userPhotos = (List<Photo>) photos.findAll();
        allPhotos.addAll((Collection) userPhotos);
        return new ResponseEntity<Object>(allPhotos, HttpStatus.OK);
    }
}
