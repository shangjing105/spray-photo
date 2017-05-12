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

        Config config = getPhoto(model, "admin");//默认admin
        model.addAttribute("config", config);
        model.addAttribute("username", "admin");
        return "/web/"+config.getTheme();
    }

    private Config getPhoto(Model model, String username) {
        User user=userService.findByUsername(username);
        Criteria<Photo> criteria = new Criteria<>();
        criteria.add(Restrictions.eq("userId", user.getId()));
        Sort sort = new Sort(Sort.Direction.DESC, "placedTop", "recommend", "createDate");
        model.addAttribute("photos",photoService.findAll(criteria, sort));

        Criteria<Config> configCriteria = new Criteria<>();
        configCriteria.add(Restrictions.eq("userId", user.getId()));
        return configService.findOne(configCriteria);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    public String username(Model model, @PathVariable String username) {
        Config config = getPhoto(model, username);
        model.addAttribute("config", config);
        model.addAttribute("username", username);
        return "/web/"+config.getTheme();
    }


}
