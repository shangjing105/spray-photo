package com.shang.spray.controller;

import com.shang.spray.entity.Config;
import com.shang.spray.entity.Photo;
import com.shang.spray.entity.User;
import com.shang.spray.utils.MD5;
import com.shang.spray.utils.specification.Criteria;
import com.shang.spray.utils.specification.Restrictions;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * info:
 * Created by shang on 16/7/26.
 */
@Controller
public class AdminController extends BaseController{

    @Value("${upload.image.path}")
    private String imagePath;

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

    @RequestMapping(value = "/admin/photo")
    public String hello(Model model, @RequestParam(defaultValue = "0")Integer page, @RequestParam(defaultValue = "8") Integer size) {
        Criteria<Photo> criteria = new Criteria<>();
        criteria.add(Restrictions.eq("userId", getUser().getId()));
        Sort sort = new Sort(Sort.Direction.DESC, "placedTop", "recommend", "createDate");
        Pageable pageable = new PageRequest(page, size, sort);
        model.addAttribute("photos",photoService.findAll(criteria, pageable));
        return "/admin/photo";
    }

    @RequestMapping(value = "/admin/addPhoto", method = RequestMethod.GET)
    public String addPhoto() {
        return "/admin/addPhoto";
    }

    @RequestMapping(value = "/admin/addPhoto", method = RequestMethod.POST)
    public String addPhoto(Photo photo) {
        if (photo.getCreateDate() == null) {
            photo.setCreateDate(new Date());
        }
        photo.setUserId(getUser().getId());
        photo.setModifyDate(new Date());
        photoService.save(photo);
        return "redirect:/admin/photo";
    }

    @RequestMapping(value = "/admin/delPhoto/{id}", method = RequestMethod.GET)
    public String delPhoto(@PathVariable Integer id) {
        photoService.delete(id);
        return "redirect:/admin/photo";
    }

    @RequestMapping(value = "/admin/config", method = RequestMethod.GET)
    public String config(Model model) {
        Criteria<Config> criteria = new Criteria<>();
        criteria.add(Restrictions.eq("userId", getUser().getId()));
        model.addAttribute("config", configService.findOne(criteria));
        return "/admin/config";
    }

    @RequestMapping(value = "/admin/config", method = RequestMethod.POST)
    public String config(Config config) {
        config.setModifyDate(new Date());
        configService.save(config);
        return "redirect:/admin/config";
    }

    @RequestMapping(value = "/admin/addUser", method = RequestMethod.GET)
    public String addUser() {
        return "/admin/addUser";
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
            config.setCreateDate(new Date());
            config.setModifyDate(config.getCreateDate());
            configService.save(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/addUser";
    }

    @ResponseBody
    @RequestMapping(value = "/admin/fileUpload", method = RequestMethod.POST)
    public Map<String, Object> fileUpload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> map = createMap();
        try {
            byte[] bytes = file.getBytes();
            File dirPath = new File(imagePath + "image/" + getUser().getUsername());
            if (!dirPath.exists()) {
                dirPath.mkdirs();
            }
            String imageName = UUID.randomUUID() + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String pathImage = imagePath + "image/" + getUser().getUsername() + "/" + imageName;//生成文件路径
            File uploadedFile = new File(pathImage);
            FileCopyUtils.copy(bytes, uploadedFile);

            map.put("state", true);
            map.put("pathImage", "/image/" + getUser().getUsername() + "/" + imageName);
        } catch (IOException e) {
            map.put("state", false);
        }
        return map;

    }

}
