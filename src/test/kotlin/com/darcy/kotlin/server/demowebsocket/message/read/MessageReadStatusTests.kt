package com.darcy.kotlin.server.demowebsocket.message.read

import com.alibaba.fastjson2.JSON
import com.darcy.kotlin.server.demowebsocket.domain.dto.input.ReceiverMessageReadStatusMarkInputDTO
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
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

    private val receiverMessageReadStatusMarkInputDTO = ReceiverMessageReadStatusMarkInputDTO(
        userId = 4,
        fromUserName = "电脑",
        targetId = 5,
        targetName = "手机",
        msgIds = listOf("msg_bfcd5b4ace2a41ec98a974259ff00ccb"),
        conversationType = 1,
        clientType = "",
        deviceId = ""
    )

    @Test
    fun `test-receiver-pull-offline-messages`() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("http://localhost:$port/api/message/read/receiver/pull/offline")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("userId", "4")
                .param("targetId", "3")
                .param("conversationType", "1")
                .param("lastMsgId", "msg_393aa96312694f12b6818ab7a68cb651")
                .param("lastSyncTime", "")
                .param("limit", "50")
        ).andExpect(MockMvcResultMatchers.status().isOk).andReturn().response.contentAsString
        println(result)
    }

    @Test
    fun `test-receiver-mark-messages-as-read`() {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("http://localhost:$port/api/message/read/receiver/push/read")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("messageReadStatusInputDTO", JSON.toJSONString(receiverMessageReadStatusMarkInputDTO))
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString
        println("result-->$result")
    }

    @Test
    fun `test-sender-sync-offline-message-status`() {
        val result = mockMvc.perform(
            post("http://localhost:$port/api/message/read/sender/sync/offline")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("userId", "3")
                .param("targetId", "4")
                .param("since", "")
                .param("until", "")
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString
        println(result)
    }

}