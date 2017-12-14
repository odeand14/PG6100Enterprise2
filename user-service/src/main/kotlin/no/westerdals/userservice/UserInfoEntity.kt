package no.westerdals.userservice

// Created by Andreas Ødegaard on 10.12.2017.

import org.hibernate.validator.constraints.Email
import org.hibernate.validator.constraints.NotBlank
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class UserInfoEntity(

        @get:Id
        @get:NotBlank
        var userId: String?,

        @get:NotBlank
        var name: String?,

        var middleName: String?,

        @get:NotBlank
        var surname: String?,

        @get:Email
        var email: String?
)