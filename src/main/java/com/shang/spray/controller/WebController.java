package com.shang.spray.controller;

import com.shang.spray.entity.Config;
import com.shang.spray.entity.Photo;
import com.shang.spray.entity.User;
import com.shang.spray.utils.specification.Criteria;
import com.shang.spray.utils.specification.Restrictions;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * info:
 * Created by shang on 2017/4/27.
 */
@Controller
public class WebController extends BaseController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {

        getPhoto(model, "admin");//默认admin
        model.addAttribute("username", "admin");
        return "/web/index";
    }

    private void getPhoto(Model model, String username) {
        User user=userService.findByUsername(username);
        Criteria<Photo> criteria = new Criteria<>();
        criteria.add(Restrictions.eq("userId", user.getId()));
        Sort sort = new Sort(Sort.Direction.DESC, "placedTop", "recommend", "createDate");
        model.addAttribute("photos",photoService.findAll(criteria, sort));

        Criteria<Config> configCriteria = new Criteria<>();
        configCriteria.add(Restrictions.eq("userId", user.getId()));
        model.addAttribute("config", configService.findOne(configCriteria));
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    public String username(Model model, @PathVariable String username) {
        getPhoto(model, username);//默认admin
        model.addAttribute("username", username);
        return "/web/index";
    }

    @ResponseBody
    @RequestMapping(value = "/getPhoto", method = RequestMethod.GET)
    public List<Photo> getAjaxPhoto(String username, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        User user=userService.findByUsername(username);
        Criteria<Photo> criteria = new Criteria<>();
        criteria.add(Restrictions.eq("userId", user.getId()));
        Sort sort = new Sort(Sort.Direction.DESC, "placedTop", "recommend", "createDate");
        Pageable pageable = new PageRequest(page, size, sort);
        return photoService.findAll(criteria, pageable).getContent();
    }

//    @RequestMapping(value = "/about", method = RequestMethod.GET)
//    public String about(Model model) {
//        User user=userService.findByUsername("admin");
//        Criteria<Config> configCriteria = new Criteria<>();
//        configCriteria.add(Restrictions.eq("userId", user.getId()));
//        model.addAttribute("config", configService.findOne(configCriteria));
//        return "/web/about";
//    }
}
