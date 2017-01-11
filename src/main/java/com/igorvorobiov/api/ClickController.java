package com.igorvorobiov.api;

import com.igorvorobiov.core.BannerService;
import com.igorvorobiov.core.Click;
import com.igorvorobiov.core.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
/**
 * Created by Igor Vorobiov <igor.vorobioff@gmail.com>
 */

@Controller
@RequestMapping(value = "/banner/{bannerId}/click", consumes = "application/json", produces = "application/json")
public class ClickController {

    private BannerService bannerService;

    @Autowired
    public ClickController(BannerService bannerService){
        this.bannerService = bannerService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Statistics show(@PathVariable String bannerId){
        return bannerService.getStatistics(bannerId);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void store(@PathVariable String bannerId, @RequestBody Click click){
        bannerService.registerClick(bannerId, click);
    }
}
