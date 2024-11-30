package com.malinga.ISO8583Parser.controller;

import com.malinga.ISO8583Parser.services.ParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/")
@RequiredArgsConstructor
public class MainController {
    private final ParserService parserService;

    @GetMapping({"", "/"})
    public String homePage(){
        parserService.parseMsg();
        return "index";
    }
}
