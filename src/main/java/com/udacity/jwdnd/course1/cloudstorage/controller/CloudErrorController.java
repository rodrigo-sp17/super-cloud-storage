package com.udacity.jwdnd.course1.cloudstorage.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CloudErrorController implements ErrorController {

    /*
        Original one from https://www.baeldung.com/spring-boot-custom-error-page
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest req, Model model) {
        var status = req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorTitle = "Oops! An unexpected error has occurred!";
        String errorText = "Please, try again your last action or return to homepage below. " +
                "We are already working on it. Thanks!";

        if (status != null) {
            Integer errorCode = Integer.valueOf(status.toString());
            model.addAttribute("errorCode", errorCode);

            if (errorCode == HttpStatus.NOT_FOUND.value()) {
                errorTitle = "This page could not be found!";
                errorText = "The page you requested does not exist or is temporarily" +
                            " not available. Please, try again from the home page by following" +
                        " the link below:";
            }

            if (errorCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorTitle = "An unexpected error has occurred in our server!";
                errorText = "We are looking into it right now. We are sorry for the inconvenience!";
            }

            if (errorCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
                errorTitle = "Ops, looks like the operation you tried to do is not allowed!";
                errorText = "Please, return to the homepage and try again!";
            }
        }

        model.addAttribute("title", errorTitle);
        model.addAttribute("text", errorText);

        return "error";
    }

    @Override
    public String getErrorPath() {
        return null;
    }

}
