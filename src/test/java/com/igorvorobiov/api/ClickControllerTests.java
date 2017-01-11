package com.igorvorobiov.api;

import com.igorvorobiov.Application;

import static org.hamcrest.Matchers.*;

import com.igorvorobiov.core.Click;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Igor Vorobiov <igor.vorobioff@gmail.com>
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
public class ClickControllerTests {

    private WebApplicationContext context;

    @Autowired
    public void setWebApplicationContext(WebApplicationContext context){
        this.context = context;
    }

    private MongoTemplate mongo;

    @Autowired
    public void setMongoTemplate(MongoTemplate mongo){
        this.mongo = mongo;
    }

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mongo.dropCollection(Click.class);
    }

    @Test
    public void addClicks() throws Exception {

        mockMvc.perform(post("/banner/4124/click").content("{ \"cost\": 10 }").contentType("application/json")).andExpect(status().isNoContent());
        mockMvc.perform(post("/banner/4124/click").content("{ \"cost\": 24 }").contentType("application/json")).andExpect(status().isNoContent());

        mockMvc.perform(get("/banner/4124/click").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cost", is(34)));
    }
}
