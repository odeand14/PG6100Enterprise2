package no.westerdals.userservice

// Created by Andreas Ødegaard on 10.12.2017.

import io.swagger.annotations.ApiModelProperty
import org.hibernate.validator.constraints.Email
import org.hibernate.validator.constraints.NotBlank
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class UserInfoEntity(

        @get:Id
        @get:NotBlank
        @ApiModelProperty("The id of the user")
        var userId: String?,

        @get:NotBlank
        @ApiModelProperty("The name of the user")
        var name: String?,

        @ApiModelProperty("The middle-name of the user")
        var middleName: String?,

        @get:NotBlank
        @ApiModelProperty("The surname of the user")
        var surname: String?,

        @get:Email
        @ApiModelProperty("The users email")
        var email: String?
)