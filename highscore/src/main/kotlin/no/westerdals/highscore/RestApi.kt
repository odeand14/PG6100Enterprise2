package no.westerdals.highscore

// Created by Andreas Ødegaard on 09.12.2017.
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class RestApi(
        private val highscoreCrud: HighscoreEntityRepository
) {

    @GetMapping(path = arrayOf("/highscoresCount"),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun getCount(): ResponseEntity<Long> {

        return ResponseEntity.ok(highscoreCrud.count())
    }


    @GetMapping(path = arrayOf("/highscores"),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun getAll(): ResponseEntity<List<HighscoreEntity>> {

        return ResponseEntity.ok(highscoreCrud.findAll().toList())
    }


    @GetMapping(path = arrayOf("/highscores/{id}"),
            produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun getById(@PathVariable id: Long)
            : ResponseEntity<HighscoreEntity> {

        val entity = highscoreCrud.findOneById(id)
                ?: return ResponseEntity.status(404).build()

        return ResponseEntity.ok(entity)
    }



    @PutMapping(path = arrayOf("/highscores/{id}"),
            consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun replace(
            @PathVariable id: Long,
            @RequestBody dto: HighscoreEntity)
            : ResponseEntity<Void> {

        if (id != dto.id) {
            return ResponseEntity.status(409).build()
        }

        val alreadyExists = highscoreCrud.existsHighscore(id)
        var code = if(alreadyExists) 204 else 201

        try {
            highscoreCrud.save(dto)
        } catch (e: Exception) {
            code = 400
        }

        return ResponseEntity.status(code).build()
    }


    @DeleteMapping(path = arrayOf("/highscores/{id}"),
            consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun delete(
            @PathVariable id: Long)
            : ResponseEntity<Void> {


        val alreadyExists = highscoreCrud.existsHighscore(id)
        var code = if(alreadyExists) 204 else 200

        try {
            highscoreCrud.deleteHighscore(id)
        } catch (e: Exception) {
            code = 400
        }

        return ResponseEntity.status(code).build()
    }


    @PostMapping(path = arrayOf("/highscores"),
            consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun post(
            @RequestBody body: String)
            : ResponseEntity<Void> {

        if (body != "") {

            val jackson = ObjectMapper()

            val rootNode = jackson.readValue(body, JsonNode::class.java)

            val user1 = rootNode.get("user1").asText()
            val user2 = rootNode.get("user2").asText()
            val score1 = rootNode.get("score1").asInt()
            val score2 = rootNode.get("score2").asInt()

            var code = 201

            try {
                highscoreCrud.createHighscore(score1, score2, user1, user2)
            } catch (e: Exception) {
                code = 400
            }
            return ResponseEntity.status(code).build()
        }

        return ResponseEntity.status(409).build()
    }
}
