package com.shang.spray.controller;

import com.shang.spray.entity.User;
import com.shang.spray.service.ConfigService;
import com.shang.spray.service.PhotoService;
import com.shang.spray.service.ThemeService;
import com.shang.spray.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

/**
 * info:
 * Created by shang on 16/7/8.
 */
public class BaseController {

    protected final Logger _logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected UserService userService;

    @Autowired
    protected PhotoService photoService;

    @Autowired
    protected ConfigService configService;

    @Autowired
    protected ThemeService themeService;

    public HashMap<String,Object> createMap() {
        return new HashMap<String,Object>();
    }

    /**
     * 异常日志扑捉打印
     */
    public String getTrace(Throwable t) {
        StringWriter stringWriter= new StringWriter();
        PrintWriter writer= new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer= stringWriter.getBuffer();
        return buffer.toString();
    }

    public User getUser() {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        return (User) session.getAttribute("user");
    }
}
