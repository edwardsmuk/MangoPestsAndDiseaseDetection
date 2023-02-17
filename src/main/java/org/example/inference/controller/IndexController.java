package org.example.inference.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.logging.Logger;

/**
 * Created by edwards.mukasa on 10-Feb-23.
 */

// tag::code[]
@Controller
public class IndexController {

    protected Logger logger = Logger.getLogger(IndexController.class.getName());

    @RequestMapping(value = "/")
    public String index() {
        logger.info("IndexController.index()");
        return "index";
    }
}
// end::code[]
