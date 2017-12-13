package no.westerdals.game

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional


@Repository
interface MovesRepository : CrudRepository<MovesEntity, Long>, MovesRepositoryCustom {

    fun findOneByGameId(id: Long): MovesEntity


}

@Transactional
interface MovesRepositoryCustom {

    fun createMove(gameId: Long, playerId: Long, x: Int, y: Int): Long
}


@Repository
@Transactional
open class MovesRepositoryImpl : MovesRepositoryCustom {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun createMove(gameId: Long, playerId: Long, x: Int, y: Int): Long {
        val entity = MovesEntity( gameId, playerId, x, y )
        em.persist(entity)
        return entity.id!!
    }
}