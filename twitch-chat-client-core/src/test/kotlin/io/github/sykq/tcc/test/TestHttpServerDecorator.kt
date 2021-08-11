package io.github.sykq.tcc.test

import reactor.netty.DisposableServer
import reactor.netty.http.server.HttpServer
import reactor.netty.http.server.HttpServerConfig

class TestHttpServerDecorator(private val initializer: (TestContext) -> HttpServer) : HttpServer() {

    // this would not work properly, but I won't need it anyways, so just leave it as a dummy
    override fun configuration(): HttpServerConfig = throw NotImplementedError()

    override fun duplicate(): HttpServer = TestHttpServerDecorator(initializer)

    fun bindNowForTest(testContext: TestContext): DisposableServer = initializer(testContext).bindNow()

}