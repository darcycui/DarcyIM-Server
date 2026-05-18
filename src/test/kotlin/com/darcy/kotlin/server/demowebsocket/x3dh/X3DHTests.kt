package com.darcy.kotlin.server.demowebsocket.x3dh

import com.darcy.kotlin.server.demowebsocket.config.JwtToken.HEADER_AUTHORIZATION
import com.darcy.kotlin.server.demowebsocket.config.JwtToken.JWT_TOKEN
import com.darcy.kotlin.server.demowebsocket.http.x3dh.user.Alice
import com.darcy.kotlin.server.demowebsocket.utils.keyToString
import com.darcy.kotlin.server.demowebsocket.utils.keysToString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class X3DHTests {
    // 注入随机端口
    @LocalServerPort
    private var port: Int = 0 // 注入随机端口

    // 模拟MVC
    @Autowired
    private lateinit var mockMvc: MockMvc

    private val alice = Alice()

    @Test
    fun `test-x3dh-push-keys`() {
        val url = "http://localhost:$port/api/x3dh/push/keys"
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HEADER_AUTHORIZATION, JWT_TOKEN)
                .param("userId", "14")
                .param("identityKey", alice.getIdentityPublicKey().keyToString())
                .param("signedPreKey", alice.getSignedPreKeyPublicKey().keyToString())
                .param("oneTimePreKeys", alice.getOneTimePreKeyPublicKeyList().keysToString())

        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString
        println("result-->$result")
    }

    @Test
    fun `test-x3dh-pull-keys`() {
        val url = "http://localhost:$port/api/x3dh/pull/keys"
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HEADER_AUTHORIZATION, JWT_TOKEN)
                .param("aliceUserId", "13")
                .param("bobUserId", "14")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString
        println("result-->$result")
    }

    @Test
    fun `test-push-hello-message`() {
        val url = "http://localhost:$port/api/x3dh/push/alice/hello"
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HEADER_AUTHORIZATION, JWT_TOKEN)
                .param("aliceUserId", "13")
                .param("bobUserId", "14")
                .param("aliceIdentityKey", "alicekey1")
                .param("aliceEphemeralKey", "alicekey2")
                .param("bobOneTimePreKeyIndex", "301")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString
        println("result-->$result")
    }

    @Test
    fun `test-pull-hello-message`() {
        val url = "http://localhost:$port/api/x3dh/pull/alice/hello"
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HEADER_AUTHORIZATION, JWT_TOKEN)
                .param("aliceUserId", "13")
                .param("bobUserId", "14")
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString
        println("result-->$result")
    }
}