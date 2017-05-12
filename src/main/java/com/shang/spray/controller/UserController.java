package com.shang.spray.controller;

import com.shang.spray.entity.Config;
import com.shang.spray.entity.User;
import com.shang.spray.utils.MD5;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;

/**
 * info:
 * Created by shang on 2017/5/11.
 */
@Controller
public class UserController extends BaseController {

    @RequestMapping(value = "/admin/login", method = RequestMethod.GET)
    public String login() {
        return "/admin/login";
    }

    @RequestMapping(value = "/admin/login", method = RequestMethod.POST)
    public String login(String username, String password, RedirectAttributes redirectAttributes) {

        UsernamePasswordToken token = new UsernamePasswordToken(username, MD5.getMD5(password));
        //获取当前的Subject
        Subject subject = SecurityUtils.getSubject();
        try {
            //在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查
            //每个Realm都能在必要时对提交的AuthenticationTokens作出反应
            //所以这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法
            subject.login(token);
            User user = userService.findByUsername(username);
            Session session = subject.getSession();
            session.setAttribute("user", user);
        }catch(UnknownAccountException uae){
            redirectAttributes.addFlashAttribute("message", "未知账户");
        }catch(IncorrectCredentialsException ice){
            redirectAttributes.addFlashAttribute("message", "密码不正确");
        }catch(LockedAccountException lae){
            redirectAttributes.addFlashAttribute("message", "账户已锁定");
        }catch(ExcessiveAttemptsException eae){
            redirectAttributes.addFlashAttribute("message", "用户名或密码错误次数过多");
        }catch(AuthenticationException ae){
            //通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
            ae.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "用户名或密码不正确");
        }
        //验证是否登录成功
        if(subject.isAuthenticated()){
            return "redirect:/admin/main";
        }else{
            token.clear();
            return "redirect:/admin/login";
        }
    }

    @RequestMapping(value = "/admin/main", method = RequestMethod.GET)
    public String main() {
        return "/admin/main";
    }

    @RequestMapping(value = "/admin/logout", method = RequestMethod.GET)
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "redirect:/admin/login";
    }

    @RequestMapping(value = "/admin/users", method = RequestMethod.GET)
    public String users(Model model, @RequestParam(defaultValue = "0")Integer page, @RequestParam(defaultValue = "8") Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC, "createDate");
        Pageable pageable = new PageRequest(page, size, sort);
        model.addAttribute("users", userService.findAll(pageable));
        return "/admin/users";
    }

    @RequestMapping(value = "/admin/addUser", method = RequestMethod.GET)
    public String addUser() {
        return "/admin/addUser";
    }

    @RequestMapping(value = "/admin/updUser/{id}", method = RequestMethod.GET)
    public String updUser(@PathVariable Integer id, Model model) {
        model.addAttribute("user1", userService.findOne(id));
        return "/admin/updUser";
    }

    @RequestMapping(value = "/admin/updUser", method = RequestMethod.POST)
    public String updUser(User user) {
        User user1 = userService.findOne(user.getId());
        user1.setUsername(user.getUsername());
        user1.setName(user.getName());
        if (StringUtils.isNotEmpty(user.getPassword())) {
            user1.setPassword(MD5.getMD5(user.getPassword()));
        }
        user1.setModifyDate(new Date());
        userService.saveAndFlush(user1);
        return "redirect:/admin/users";
    }

    @RequestMapping(value = "/admin/addUser", method = RequestMethod.POST)
    public String addUser(User user, Config config, String configName) {
        try {
            user.setPassword(MD5.getMD5(user.getPassword()));
            user.setCreateDate(new Date());
            user.setModifyDate(user.getCreateDate());
            userService.save(user);

            config.setUserId(user.getId());
            config.setName(configName);
            config.setTheme("hydrogen_index");
            config.setCreateDate(new Date());
            config.setModifyDate(config.getCreateDate());
            configService.save(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/users";
    }
}
