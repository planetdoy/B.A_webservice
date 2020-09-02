package com.breakaway.admin.BA_website.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//JSON을 반환하는 컨트롤러로 만들어준다.
//각 메소드에 선언했던 @ResponseBody를 대신합니다.
@RestController
public class HelloController {

    //HTTP METHOD 인 Get의 요청을 받을 수 있는 api
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }
}

