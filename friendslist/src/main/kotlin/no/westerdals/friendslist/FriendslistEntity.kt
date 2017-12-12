package no.westerdals.friendslist

import org.hibernate.validator.constraints.Email
import org.hibernate.validator.constraints.NotBlank
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class FriendslistEntity(

    @get:Id
    @get:NotBlank
    var userId: String?,

    @get:NotBlank
    var friendId: String?,

    var date: String?
)