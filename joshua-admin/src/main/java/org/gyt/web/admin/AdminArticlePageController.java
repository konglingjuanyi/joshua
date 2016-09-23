package org.gyt.web.admin;

import org.apache.commons.lang3.StringUtils;
import org.gyt.web.api.service.ArticleService;
import org.gyt.web.api.utils.ModelAndViewUtils;
import org.gyt.web.model.Article;
import org.gyt.web.model.ArticleStatus;
import org.gyt.web.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 后台页面路由器
 * Created by Administrator on 2016/9/16.
 */
@RestController
@RequestMapping("/admin")
public class AdminArticlePageController {

    @Autowired
    private ArticleService articleService;

    @RequestMapping("/article")
    public ModelAndView tablePage(
            @RequestParam(required = false) String type
    ) {
        ModelAndView modelAndView = ModelAndViewUtils.newModelAndView("admin-article");

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Article> articleList = articleService.getFromUser(user.getUsername());

        if (StringUtils.isEmpty(type)) {
            modelAndView.addObject("items", articleList);
            modelAndView.addObject("subtitle", "我的文章");
        } else if (type.equalsIgnoreCase("RAW")) {
            modelAndView.addObject("items", articleList.stream().filter(article -> article.getStatus().equals(ArticleStatus.RAW)).collect(Collectors.toList()));
            modelAndView.addObject("subtitle", "草稿箱");
        } else if (type.equalsIgnoreCase("AUDIT")) {
            modelAndView.addObject("items", articleList.stream().filter(article -> article.getStatus().equals(ArticleStatus.AUDITING)).collect(Collectors.toList()));
            modelAndView.addObject("subtitle", "待审核文章");
        } else if (type.equalsIgnoreCase("PUBLISH")) {
            modelAndView.addObject("items", articleList.stream().filter(article -> article.getStatus().equals(ArticleStatus.PUBLISHED)).collect(Collectors.toList()));
            modelAndView.addObject("subtitle", "已发布文章");
        } else if (type.equalsIgnoreCase("REJECT")) {
            modelAndView.addObject("items", articleList.stream().filter(article -> article.getStatus().equals(ArticleStatus.REJECTED)).collect(Collectors.toList()));
            modelAndView.addObject("subtitle", "驳回文章");
        } else {
            modelAndView.addObject("items", new ArrayList<>());
            modelAndView.addObject("subtitle", "未知类型");
        }
        return modelAndView;
    }

    @RequestMapping("/article/{id}")
    public ModelAndView detailsPage(
            @PathVariable String id
    ) {
        ModelAndView modelAndView = ModelAndViewUtils.newModelAndView("admin-article-details");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Article article = articleService.get(Long.valueOf(id));

        if (null == article) {
            modelAndView.setViewName("404");
            modelAndView.addObject("message", String.format("找不到文章：%s", id));
        } else {
            modelAndView.addObject("item", article);
            modelAndView.addObject("user", user);
        }

        return modelAndView;
    }

    @RequestMapping("/article/{id}/edit")
    public ModelAndView editArticlePage(
            @PathVariable String id
    ) {
        ModelAndView modelAndView = ModelAndViewUtils.newModelAndView("admin-article-editor");
        modelAndView.addObject("title", "编辑文章");

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Article article = articleService.get(Long.valueOf(id));

        if (article == null) {
            modelAndView.addObject("error", "文章不存在");
        } else if (!article.getAuthor().getUsername().equals(user.getUsername())) {
            modelAndView.addObject("error", "只能编辑自己的文章");
        } else {
            modelAndView.addObject("item", article);
        }

        return modelAndView;
    }

    @RequestMapping("/article/{id}/audit")
    public ModelAndView auditArticlePage(
            @PathVariable String id
    ) {
        ModelAndView modelAndView = ModelAndViewUtils.newModelAndView("admin-article-audit");
        modelAndView.addObject("title", "审核文章");

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Article article = articleService.get(Long.valueOf(id));

        if (article == null) {
            modelAndView.addObject("error", "文章不存在");
        } else {
            modelAndView.addObject("item", article);
        }

        return modelAndView;
    }

    @RequestMapping("/article/new")
    public ModelAndView newArticlePage() {
        ModelAndView modelAndView = ModelAndViewUtils.newModelAndView("admin-article-editor");
        modelAndView.addObject("title", "新建文章");
        modelAndView.addObject("item", new Article());
        return modelAndView;
    }

    @RequestMapping(value = "/article/save", method = RequestMethod.POST)
    public String saveArticle(@ModelAttribute Article article) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (article.getId() == null) {
            article.setAuthor(user);
            article.setCreatedDate(new Date());
        }

        article.setLastModifiedTime(new Date());
        article.setLastModifiedUser(user);

        return articleService.create(article) ? "success" : null;
    }
}