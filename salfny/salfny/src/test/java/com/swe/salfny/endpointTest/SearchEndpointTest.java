package com.swe.salfny.endpointTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.swe.salfny.Model.post.Post;
import com.swe.salfny.Model.post.PostRepository;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest

public class SearchEndpointTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PostRepository postRepository;
    Post post1;
    Post post2;
    Post post3;
    @BeforeEach
    public void initTest() {
        post1 =  new Post("BMW", null, 1500000, 0, 1, LocalDateTime.now());
        postRepository.save(post1);
        post2 =  new Post("T_shirt", "Cotton 100%", 150, 4, 1, LocalDateTime.now());
        postRepository.save(post2);
        post3 =  new Post("LabTop", "Core i7", 17000, 5, 1, LocalDateTime.now());
        postRepository.save(post3);
    }
    @AfterEach
    public void endTest(){
        postRepository.delete(post1);
        postRepository.delete(post2);
        postRepository.delete(post3);
    }

    @Test
    @Order(1)
    public void searchLike_foundPosts() throws Exception{
                mvc.perform(MockMvcRequestBuilders
                        .post("/search")
                        .content("BM@All").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..title").isArray())
                .andExpect((jsonPath("$..title", Matchers.containsInAnyOrder("BMW"))));

    }
    @Test
    @Order(2)
    public void searchLike_notFoundPosts() throws Exception{

        mvc.perform(MockMvcRequestBuilders
                        .post("/search")
                        .content("ZX@All").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..title").isEmpty());
    }

    @Test
    @Order(3)
    public void allCategory() throws Exception{
                mvc.perform(MockMvcRequestBuilders
                        .post("/search")
                        .content(" @All").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..title").isArray())
                .andExpect((jsonPath("$..title", Matchers.containsInAnyOrder("BMW", "LabTop", "T_shirt"))));
    }


    @Test
    @Order(4)
    public void searchByCategoryLike_foundPosts() throws Exception{
                mvc.perform(MockMvcRequestBuilders
                        .post("/search")
                        .content(" @devices").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..title").isArray())
                .andExpect((jsonPath("$..title", Matchers.containsInAnyOrder("LabTop"))));

                mvc.perform(MockMvcRequestBuilders
                        .post("/search")
                        .content(" @dresses").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..title").isArray())
                .andExpect((jsonPath("$..title", Matchers.containsInAnyOrder("T_shirt"))));
    }

    @Test
    @Order(5)
    public void searchByCategoryLike_notFoundPosts() throws Exception{
                mvc.perform(MockMvcRequestBuilders
                        .post("/search")
                        .content("ZX@devices").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..title").isEmpty());

                mvc.perform(MockMvcRequestBuilders
                        .post("/search")
                        .content(" @others").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..title").isEmpty());
    }








    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
