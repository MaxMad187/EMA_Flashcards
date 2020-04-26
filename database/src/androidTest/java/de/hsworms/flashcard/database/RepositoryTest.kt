package de.hsworms.flashcard.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hsworms.flashcard.database.dao.FlashcardDao
import de.hsworms.flashcard.database.dao.RepositoryDao
import de.hsworms.flashcard.database.entity.FlashcardNormal
import de.hsworms.flashcard.database.entity.Repository
import de.hsworms.flashcard.database.entity.RepositoryCardCrossRef
import de.hsworms.flashcard.database.entity.RepositoryWithCards
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RepositoryTest {
    private lateinit var db: FCDatabase
    private lateinit var repositoryDao: RepositoryDao
    private lateinit var flashcardDao: FlashcardDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, FCDatabase::class.java).build()
        repositoryDao = db.repositoryDao()
        flashcardDao = db.flashcardDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertGetDeleteRoutine() {
        repositoryDao.insert(Repository(name = ""), Repository(name = ""))
        val repo = repositoryDao.getRepository(1)
        Assert.assertNotNull(repo)
        repositoryDao.delete(repo!!)
        Assert.assertNull(repositoryDao.getRepository(1))
    }

    @Test
    fun addGetRemoveCards() {
        repositoryDao.insert(Repository(name = ""))
        flashcardDao.insert(FlashcardNormal("test", "abc"))
        repositoryDao.addCard(RepositoryCardCrossRef(1,1))

        val repo = repositoryDao.getRepositoryWithCards(1)!!
        Assert.assertEquals(1, repo.cards.size)

        val card = flashcardDao.getOne(repo.cards[0].cardId!!) as FlashcardNormal
        Assert.assertEquals(1L, card.cardId)
        Assert.assertEquals("test", card.front)
        Assert.assertEquals("abc", card.back)

        repositoryDao.removeCard(RepositoryCardCrossRef(1, 1L))

        Assert.assertEquals(0, repositoryDao.getRepositoryWithCards(1)!!.cards.size)
    }
}