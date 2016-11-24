package org.gyt.web.security;

import org.gyt.web.core.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 错误处理页面
 * Created by y27chen on 2016/7/12.
 */
@RestController
@RequestMapping("/error")
public class BasicErrorController implements ErrorController {

    @Autowired
    private ErrorAttributes errorAttributes;

    @Autowired
    private ArticleService articleService;

    @RequestMapping
    public ModelAndView handleError(HttpServletRequest request) {
        Map<String, Object> errors = errorAttributes.getErrorAttributes(new ServletRequestAttributes(request), true);
        ModelAndView modelAndView = new ModelAndView("error/5xx");

        if (errors.get("status").equals(404)) {
            modelAndView.setViewName("error/404");
            modelAndView.addObject("timestamp", errors.get("timestamp"));
            modelAndView.addObject("path", errors.get("path"));
            modelAndView.addObject("articles", articleService.getChurchArticles());
        } else if (errors.get("status").equals(403)) {
            modelAndView.setViewName("error/403");
            modelAndView.addObject("timestamp", errors.get("timestamp"));
            modelAndView.addObject("message", errors.get("message"));
            modelAndView.addObject("path", errors.get("path"));
        } else if (errors.get("status").equals(500)) {
            modelAndView.addObject("exception", errors.get("exception"));
            modelAndView.addObject("message", errors.get("message"));
            modelAndView.addObject("trace", errors.get("trace"));
            modelAndView.addObject("path", errors.get("path"));
        }

        return modelAndView;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
