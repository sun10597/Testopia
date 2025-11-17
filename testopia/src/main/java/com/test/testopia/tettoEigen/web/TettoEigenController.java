package com.test.testopia.tettoEigen.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TettoEigenController {

    @GetMapping("/tetto-eigen")
    public String tettoEigen() {
        return "tettoEigen";
    }
}
