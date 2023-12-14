package com.rohit.springboot.MaverickBank.basicEndpointManager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("")
public class HomeController {
    @ResponseBody
    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String index(){
        return "This is Home";
    }

    @ResponseBody
    @RequestMapping(value = "/error",method = RequestMethod.GET)
    public String error(){
        return "Some Error occured!";
    }


}
