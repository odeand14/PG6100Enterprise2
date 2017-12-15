package no.westerdals.zuul

import io.swagger.annotations.ApiModelProperty
import org.hibernate.validator.constraints.NotBlank
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name="USERS")
class UserEntity(

        @get:Id
        @get:NotBlank
        @ApiModelProperty("The loginname of the user")
        var username: String?,

        @get:NotBlank
        @ApiModelProperty("The password of the user")
        var password: String?,

        @get:ElementCollection
        @get:NotNull
        @ApiModelProperty("The role of the user")
        var roles: Set<String>? = setOf(),

        @get:NotNull
        @ApiModelProperty("The status of the user")
        var enabled: Boolean? = true
)