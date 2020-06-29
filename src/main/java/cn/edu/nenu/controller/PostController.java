package cn.edu.nenu.controller;

import cn.edu.nenu.config.Constants;
import cn.edu.nenu.config.HttpServlet;
import cn.edu.nenu.domain.Post;
import cn.edu.nenu.service.PostService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import static cn.edu.nenu.config.Constants.PAGE_SIZE;

/**
 * PostController Class
 *
 * @author <b>Oxidyc</b>, Copyright &#169; 2003
 * @version 1.0, 2020-03-04 22:54
 */
@CommonsLog
@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    PostService postService;

    @RequestMapping("")
    public String list(@RequestParam(value = "page", defaultValue = "1") int pageNumber, Model model, ServletRequest request, HttpSession httpSession){

        Map<String, Object> searchParams = HttpServlet.getParametersStartingWith(request, "s_");
        Page<Post> posts = postService.getEntityPage(searchParams, pageNumber, PAGE_SIZE);
        model.addAttribute("posts", posts);
        model.addAttribute("PAGE_SIZE", PAGE_SIZE);
        model.addAttribute("searchParams", HttpServlet.encodeParameterStringWithPrefix(searchParams, "s_"));
        String path = "/WEB-INF/views/post/list.jsp";
        return "post/list";
    }

    @GetMapping(value = "create")
    public String createForm(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("action", "create");
        return "post/form";
    }

    @PostMapping(value = "create")
    public String create(@Valid Post newPost, RedirectAttributes redirectAttributes) {
        LocalDateTime date = LocalDateTime.now(Clock.systemUTC());
        newPost.setCreatedAt(date);
        newPost.setLastModifiedAt(date);
        postService.save(newPost);
        redirectAttributes.addFlashAttribute("message", "创建文章成功");
        return "redirect:/post";
    }

    @GetMapping(value = "update/{id}")
    public String updateForm(@PathVariable("id") Integer pkId, Model model){
        Post post =  postService.findOne(pkId);
        model.addAttribute("post",post);
        model.addAttribute("action", "update");
        return "post/form";
    }

    /**
     * 页面编辑后，保存
     */

    @PostMapping(value = "update")
    public String update(@Valid Post post, RedirectAttributes redirectAttributes){
        Integer pkId = post.getId();
        Post newPost = postService.findOne(pkId);
        LocalDateTime date = LocalDateTime.now(Clock.systemUTC());
        newPost.setTitle(post.getTitle());
        newPost.setContent(post.getContent());
        newPost.setCategories(post.getCategories());
        newPost.setCreator(post.getCreator());
        newPost.setStatus(post.getStatus());
        newPost.setLastModifiedAt(date);
        postService.save(newPost);
        redirectAttributes.addFlashAttribute("message", "更改文章信息成功");
        return "redirect:/post/";
    }

    @RequestMapping(value = "delete/{id}")
    public String delete(@PathVariable("id") Integer pkId, RedirectAttributes redirectAttributes) {
        String message = "删除文章成功";
        try {
            postService.remove(pkId);
        }catch (Exception e){
            message = "删除文章失败，该文章被使用";
        }
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/post/";
    }

    @PostMapping(value = "delete")
    public String deleteBatch(ServletRequest request,RedirectAttributes redirectAttributes){
        String[] chkIds = request.getParameterValues("chkIds");
        for (String id:chkIds){
            postService.remove(Integer.valueOf(id));
        }
        return "redirect:/post/";
    }
}