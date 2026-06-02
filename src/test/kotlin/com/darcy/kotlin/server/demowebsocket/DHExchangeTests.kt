package com.darcy.kotlin.server.demowebsocket

import com.darcy.kotlin.server.demowebsocket.domain.dto.DHKeyExchangeDTO
import com.darcy.kotlin.server.demowebsocket.domain.table.dh.DHKeyExchange
import com.darcy.kotlin.server.demowebsocket.http.service.testSharedSecret
import com.darcy.kotlin.server.demowebsocket.http.x3dh.exchange.ECCExchangeHelper
import com.darcy.kotlin.server.demowebsocket.utils.*
import org.hibernate.dialect.JsonHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.security.KeyPair
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class DHExchangeTests {
    // 注入随机端口
    @LocalServerPort
    private var port: Int = 0 // 注入随机端口

    // 模拟MVC
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `test-dh-exchange`() {
        val ephemeralKeyPair: KeyPair = ECCExchangeHelper.generateKeyPair()
        val result = mockMvc.perform(
            post("http://localhost:$port/api/transport/dh/exchange")
                .contentType("application/x-www-form-urlencoded")
                .param("userId", "1")
                .param("publicKey", ephemeralKeyPair.public.keyToString())
        ).andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString
        println("result-->$result")
        val dhKeyExchange = JsonUtil.fromJson(result, DHKeyExchangeDTO::class.java)
        assertNotNull(dhKeyExchange, "DHKeyExchange is null")
        println("dhKeyExchange-->$dhKeyExchange")
        val sharedSecret = ECCExchangeHelper.getSharedSecret(
            ephemeralKeyPair.private,
            dhKeyExchange.publicKey.hexStrToBytes().toPublicKey()
        ).bytesToHexStr()
        assertEquals(sharedSecret, testSharedSecret, "sharedSecret is not equal")
    }
}