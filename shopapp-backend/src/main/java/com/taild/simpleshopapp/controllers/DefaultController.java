package com.taild.simpleshopapp.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DefaultController {

    private static final Logger log = LoggerFactory.getLogger(DefaultController.class);

    @GetMapping("/error")
    public void error() {
        log.info("error");
    }
}
