package com.darcy.kotlin.server.demowebsocket.x3dh

import com.alibaba.fastjson2.JSON
import com.darcy.kotlin.server.demowebsocket.domain.dto.x3dh.X3DHKeysPullDTO
import com.darcy.kotlin.server.demowebsocket.http.x3dh.exchange.ECCExchangeHelper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import com.darcy.kotlin.server.demowebsocket.http.x3dh.chain.HKDF
import com.darcy.kotlin.server.demowebsocket.utils.*
import kotlin.test.assertContentEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class X3DHFakeTests {
    // 注入随机端口
    @LocalServerPort
    private var port: Int = 0 // 注入随机端口

    // 模拟MVC
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * 测试 X3DH
     */
    @Test
    fun `test-x3dh-pull-bob-keys`() {
        val url = "http://localhost:$port/api/x3dh/pull/keys"
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("aliceUserId", "28")
                .param("bobUserId", "29")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString
        println("result-->$result")
        val resultEntity = JSON.parseObject(result)
        val x3dhBobKeys = JSON.parseObject(
            resultEntity.getString("result"),
            X3DHKeysPullDTO::class.java
        )
        println("x3dhBobKeys-->$x3dhBobKeys")
        val K1 = aliceCalculateKey(x3dhBobKeys)
        println("K1-->${K1.bytesToHexStr()}")
        val K2 = bobCalculateKey()
        println("K2-->${K2.bytesToHexStr()}")
        // a8174bd4b803e1b90483d9609f889e9177af13ec5bcf0496cd0fa3d998a87b00ff70320408ba3241063d610b5ad355d82d3ee36e9dfdaa4272e8c29c9c4c577b
        assertContentEquals(K1, K2, "共享密钥交换失败")
    }

    val aliceIdentityPrivateKey = "8c4aae7a93367905f9f8a68491173059bfd53aa6ccb9906ba59d247f650b1231"
    val aliceEphemeralPrivateKey = "f1b760d87917b117017d2328792fb28b95e652bd71d7c4db44c18b1e3dc79337"

    fun aliceCalculateKey(bobKeys: X3DHKeysPullDTO): ByteArray {
        val aliceIdentityPrivate = aliceIdentityPrivateKey.hexStrToBytes().toPrivateKey()
        val aliceEphemeralPrivate = aliceEphemeralPrivateKey.hexStrToBytes().toPrivateKey()

        val bobIdentityPublic = bobKeys.identityKey.hexStrToBytes().toPublicKey()
        val bobSignedPreKeyPublic = bobKeys.signedPreKey.hexStrToBytes().toPublicKey()
        val bobOneTimePreKeyPublic = bobKeys.oneTimePreKey.hexStrToBytes().toPublicKey()

        val dh1 = ECCExchangeHelper.getSharedSecret(aliceIdentityPrivate, bobIdentityPublic)
        val dh2 = ECCExchangeHelper.getSharedSecret(aliceEphemeralPrivate, bobIdentityPublic)
        val dh3 = ECCExchangeHelper.getSharedSecret(aliceEphemeralPrivate, bobSignedPreKeyPublic)
        val dh4 = ECCExchangeHelper.getSharedSecret(aliceEphemeralPrivate, bobOneTimePreKeyPublic)
        val sharedSecret = EncryptUtil.appendArrays(dh1, dh2, dh3, dh4)
        return HKDF.deriveSecrets(sharedSecret, ByteArray(32), "Info".toByteArray(), 64)
    }

    val aliceIdentityPublicKey = "3d4fa7151d41dd6242145c651e3b26f2c8c2b285e28f6843bffd82d6232d832a"
    val aliceEphemeralPublicKey = "a0b9b7d397986c7c2c492d6b03039eeccba10c32ad86538546a46fb2b0b07226"

    val bobIdentityPrivate = "286e814b3eb5bcee87f41fa35e68b73d24be78b46adb789c65dd0f917cb8239c"
    val bobSignedPreKeyPrivate = "51bbbd6b05dc99556ed59688154f8895d44d70897fa567e933d121d33b7f9a96"
    val bobOneTimePreKeyPrivate = "55f2e93e1f74ce86dfbf8772264a216c6cb3e69979fd50771fb5c7c7841f53a0"

    fun bobCalculateKey(): ByteArray {
        val bobIdentityPrivate = bobIdentityPrivate.hexStrToBytes().toPrivateKey()
        val bobSignedPreKeyPrivate = bobSignedPreKeyPrivate.hexStrToBytes().toPrivateKey()
        val bobOneTimePreKeyPrivate = bobOneTimePreKeyPrivate.hexStrToBytes().toPrivateKey()

        val aliceIdentityPublic = aliceIdentityPublicKey.hexStrToBytes().toPublicKey()
        val aliceEphemeralPublic = aliceEphemeralPublicKey.hexStrToBytes().toPublicKey()

        val dh1 = ECCExchangeHelper.getSharedSecret(bobIdentityPrivate, aliceIdentityPublic)
        val dh2 = ECCExchangeHelper.getSharedSecret(bobIdentityPrivate, aliceEphemeralPublic)
        val dh3 = ECCExchangeHelper.getSharedSecret(bobSignedPreKeyPrivate, aliceEphemeralPublic)
        val dh4 = ECCExchangeHelper.getSharedSecret(bobOneTimePreKeyPrivate, aliceEphemeralPublic)
        val sharedSecret = EncryptUtil.appendArrays(dh1, dh2, dh3, dh4)
        return HKDF.deriveSecrets(sharedSecret, ByteArray(32), "Info".toByteArray(), 64)

    }
}