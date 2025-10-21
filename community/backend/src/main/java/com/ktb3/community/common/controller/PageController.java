package com.ktb3.community.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/terms")
    public String terms() {

        // html내용 바꾸려면 아래와 같이 하면 됨
//        model.addAttribute("serviceName", "서비스 이름");
//        model.addAttribute("companyName", "운영팀");
//        model.addAttribute("supportEmail", "support@example.com");
//        model.addAttribute("effectiveDate", "2025-10-20");
//        model.addAttribute("lastUpdated", "2025-10-20");
//        model.addAttribute("version", "v1.0");

        return "terms";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "privacy";
    }
}
