package com.ej.restaurant.controllertests;

import com.ej.restaurant.controller.MenuInfoController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@WebAppConfiguration
public class MenuInfoControllerTests {

    private MockMvc mockMvc;

    @Autowired
    private MenuInfoController menuInfoController;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(menuInfoController).build();
    }

    @Test
    public void allMenusTest() throws Exception{
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("merchantId", "123");
        mockMvc.perform(MockMvcRequestBuilders.get("/wx/menu/allMenus")
                        //.param("merchantId", "123")
                        .params(params)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                        //.andDo(print()); //打印结果
                        //.andReturn().getResponse().getContentAsString(); //获取结果字符串
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(100000));

    }
}
