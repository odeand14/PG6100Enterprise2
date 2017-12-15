package no.westerdals.userservice

// Created by Andreas Ødegaard on 10.12.2017.

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import org.springframework.data.repository.CrudRepository
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*


@Repository
interface UserInfoRepository : CrudRepository<UserInfoEntity, String>

@Api(value = "/user-service", description = "Handling of creating, editing and retrieving of users")
@Validated
@RestController
class RestApi(
        private val crud: UserInfoRepository
) {

    @ApiOperation("Get the number of existing users")
    @GetMapping(path = arrayOf("/usersInfoCount"),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun getCount(): ResponseEntity<Long> {

        return ResponseEntity.ok(crud.count())
    }


    @ApiOperation("Get all users in the database")
    @GetMapping(path = arrayOf("/usersInfo"),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun getAll(): ResponseEntity<List<UserInfoEntity>> {

        return ResponseEntity.ok(crud.findAll().toList())
    }

    @ApiOperation("Get user by ID")
    @GetMapping(path = arrayOf("/usersInfo/{id}"),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun getById(@ApiParam("The user id")
            @PathVariable id: String)
            : ResponseEntity<UserInfoEntity> {

        val entity = crud.findOne(id)
                ?: return ResponseEntity.status(404).build()

        return ResponseEntity.ok(entity)
    }


    @ApiOperation("Puts values in given user, if nonexistent creates a new user")
    @PutMapping(path = arrayOf("/usersInfo/{id}"),
            consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    @ApiResponse(code = 204 or 201, message = "The newly altered or created user")
    fun replace(@ApiParam("The user that will replace the old one. Cannot change its id though.")
            @PathVariable id: String,
            @RequestBody dto: UserInfoEntity)
            : ResponseEntity<Void> {

        if (id != dto.userId) {
            return ResponseEntity.status(409).build()
        }

        val alreadyExists = crud.exists(id)
        var code = if(alreadyExists) 204 else 201

        try {
            crud.save(dto)
        } catch (e: Exception) {
            code = 400
        }

        return ResponseEntity.status(code).build()
    }
}

