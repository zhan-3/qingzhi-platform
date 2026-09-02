package com.zhan.qingzhiplatform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Forwards Vue history-mode routes to the bundled frontend entry point. */
@Controller
public class SpaController {

    @GetMapping({"/", "/login", "/register", "/dashboard", "/users", "/resources"})
    public String index() {
        return "forward:/index.html";
    }
}
