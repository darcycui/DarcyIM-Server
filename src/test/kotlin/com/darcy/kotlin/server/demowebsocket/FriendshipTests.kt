package com.darcy.kotlin.server.demowebsocket

import com.darcy.kotlin.server.demowebsocket.config.JwtToken.HEADER_AUTHORIZATION
import com.darcy.kotlin.server.demowebsocket.config.JwtToken.JWT_TOKEN
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class FriendshipTests {

    // 注入随机端口
    @LocalServerPort
    private var port: Int = 0 // 注入随机端口

    // 模拟MVC
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `test-query-friendships-all`() {
        val result = mockMvc.perform(
            post("http://localhost:$port/api/friendships/query/all")
                .header(HEADER_AUTHORIZATION, JWT_TOKEN)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("userId", "15")
        ).andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString
        println("result-->$result")
    }
    @Test
    fun `test-delete-friendship`(){
        val result = mockMvc.perform(
            post("http://localhost:$port/api/friendships/delete")
                .header(HEADER_AUTHORIZATION, JWT_TOKEN)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("userId", "3")
                .param("friendId", "4")
        ).andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString
        println("result-->$result")
    }
}