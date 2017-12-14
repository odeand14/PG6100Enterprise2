package no.westerdals.game

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional


@Repository
interface MovesRepository : CrudRepository<MovesEntity, Long>, MovesRepositoryCustom {

    //TODO: DETTE VAR SYNDEREN
    //fun findOneByGameId(id: Long): MovesEntity


}

@Transactional
interface MovesRepositoryCustom {

    fun createMove(gameId: Long, playerusername: String, x: Int, y: Int): Long
}


@Repository
@Transactional
open class MovesRepositoryImpl : MovesRepositoryCustom {

    @PersistenceContext
    private lateinit var em: EntityManager


    //TODO: har fucket opp denne
    override fun createMove(gameId: Long, playerusername: String, x: Int, y: Int): Long {

        val gameEntity = em.find(GameEntity::class.java, gameId)!!


        val entity = MovesEntity( gameEntity, playerusername, x, y )
        em.persist(entity)

        gameEntity.gameMoves.add(entity)
        return entity.id!!
    }
}