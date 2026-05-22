package com.darcy.kotlin.server.demowebsocket.message.read

import com.alibaba.fastjson2.JSON
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.MessageReadStatusInputDTO
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import com.darcy.kotlin.server.demowebsocket.config.JwtToken.HEADER_AUTHORIZATION
import com.darcy.kotlin.server.demowebsocket.config.JwtToken.JWT_TOKEN
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class MessageReadStatusTests {

    // 注入随机端口
    @LocalServerPort
    private var port: Int = 0 // 注入随机端口

    // 模拟MVC
    @Autowired
    private lateinit var mockMvc: MockMvc
//    private val messageReadStatusInputDTO = MessageReadStatusInputDTO(
//        userId = 2,
//        targetId = 1,
//        targetName = "tony",
//        msgIds = listOf("msg_448022166593245184"),
//        conversationType = 1,
//        clientType = "",
//        deviceId = ""
//    )
    private val messageReadStatusInputDTO = MessageReadStatusInputDTO(
        userId = 1,
        targetId = 2,
        targetName = "tina",
        msgIds = listOf("msg_448051142443307008"),
        conversationType = 1,
        clientType = "",
        deviceId = ""
    )

    @Test
    fun `test-mark-message-read-by-http`(){
        val result = mockMvc.perform(
            post("http://localhost:$port/api/message/read/mark_read")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("messageReadStatusInputDTO", JSON.toJSONString(messageReadStatusInputDTO))
        ).andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString
        println("result-->$result")
    }
}