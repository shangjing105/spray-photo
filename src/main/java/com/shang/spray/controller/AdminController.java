package com.shang.spray.controller;

import com.shang.spray.entity.Config;
import com.shang.spray.entity.Photo;
import com.shang.spray.utils.specification.Criteria;
import com.shang.spray.utils.specification.Restrictions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    @RequestMapping(value = "/admin/addPhotoBatch", method = RequestMethod.GET)
    public String addPhotoBatch() {
        return "/admin/addPhoto_batch";
    }

    @RequestMapping(value = "/admin/addPhotoBatch", method = RequestMethod.POST)
    public String addPhotoBatch(String[] link) {
        if (link.length > 0) {
            for (String s : link) {
                Photo photo = new Photo();
                photo.setLink(s);
                photo.setCreateDate(new Date());
                photo.setUserId(getUser().getId());
                photo.setModifyDate(new Date());
                photo.setPlacedTop(false);
                photo.setRecommend(false);
                photoService.save(photo);
            }
        }
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
        model.addAttribute("themes", themeService.findAll());
        return "/admin/config";
    }

    @RequestMapping(value = "/admin/config", method = RequestMethod.POST)
    public String config(Config config) {
        Config config1 = configService.findOne(config.getId());
        config.setModifyDate(new Date());
        config.setCreateDate(config1.getCreateDate());
        config.setUserId(config1.getUserId());
        configService.save(config);
        return "redirect:/admin/config";
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
