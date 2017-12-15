package no.westerdals.zuul


// Created by Andreas Ødegaard on 15.12.2017.

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import springfox.documentation.swagger.web.SwaggerResource
import springfox.documentation.swagger.web.SwaggerResourcesProvider
import java.util.*

@Component
@Primary
@EnableAutoConfiguration
class DocumentationController : SwaggerResourcesProvider {

    override fun get(): List<SwaggerResource> {
        val resources = ArrayList<SwaggerResource>()
        resources.add(swaggerResource("user-service", "/api/user-service/v2/api-docs", "2.0"))
        resources.add(swaggerResource("highscore-service", "/api/highscore/v2/api-docs", "2.0"))
        resources.add(swaggerResource("game-service", "/api/game/v2/api-docs", "2.0"))
        resources.add(swaggerResource("friendslist-service", "/api/friendslist/v2/api-docs", "2.0"))
        return resources
    }

    private fun swaggerResource(name: String, location: String, version: String): SwaggerResource {
        val swaggerResource = SwaggerResource()
        swaggerResource.setName(name)
        swaggerResource.setLocation(location)
        swaggerResource.setSwaggerVersion(version)
        return swaggerResource
    }

}