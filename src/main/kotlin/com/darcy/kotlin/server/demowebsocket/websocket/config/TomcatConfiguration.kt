//package com.darcy.kotlin.server.demowebsocket.websocket.config
//
//import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
//import org.apache.catalina.connector.Connector
//import org.apache.tomcat.websocket.server.WsSci
//import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
//import org.springframework.boot.web.servlet.server.ServletWebServerFactory
//import org.springframework.context.annotation.Bean
//
///**
// * wss配置
// */
//class TomcatConfiguration {
//    @Bean
//    fun servletContainer(): ServletWebServerFactory {
//        val tomcat = TomcatServletWebServerFactory()
//
//        tomcat.addAdditionalTomcatConnectors(createSslConnector())
//        return tomcat
//    }
//
//    private fun createSslConnector(): Connector {
//        val connector: Connector = Connector("org.apache.coyote.http11.Http11NioProtocol")
//        connector.scheme = "http"
//        //Connector监听的http的端口号
//        connector.port = 8080
//        connector.secure = false
//        //监听到http的端口号后转向到的https的端口号
//        connector.redirectPort = 7443
//        return connector
//    }
//
//    /**
//     * 创建wss协议接口
//     *
//     * @return
//     */
//    @Bean
//    fun tomcatContextCustomizer(): TomcatContextCustomizer {
//        logI("init")
//        return TomcatContextCustomizer { context ->
//            logI("init   customize")
//            context.addServletContainerInitializer(WsSci(), null)
//        }
//    }
//}