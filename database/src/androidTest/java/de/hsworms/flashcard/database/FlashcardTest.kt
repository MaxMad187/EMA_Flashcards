package de.hsworms.flashcard.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hsworms.flashcard.database.dao.FlashcardDao
import de.hsworms.flashcard.database.entity.Flashcard
import de.hsworms.flashcard.database.entity.FlashcardNormal
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class FlashcardTest {
    private lateinit var db: FCDatabase
    private lateinit var flashcardDao: FlashcardDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, FCDatabase::class.java).build()
        flashcardDao = db.flashcardDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun getOneNotExists() {
        val fc = flashcardDao.getOne(0)
        Assert.assertNull(fc);
    }

    @Test
    fun insertGetDeleteRoutine() {
        flashcardDao.insert(FlashcardNormal("test", "asda"), FlashcardNormal("testasd", "abc"))
        val fc1 = flashcardDao.getOne(1L)!!
        val fc2 = flashcardDao.getOne(2L)!!
        Assert.assertEquals(1L, fc1.id)
        Assert.assertEquals(2L, fc2.id)
        flashcardDao.delete(fc1, fc2)
    }

    @Test
    fun flashcardNormal() {
        flashcardDao.insert(FlashcardNormal("test", "abc"))
        val fc = flashcardDao.getOne(1) as FlashcardNormal
        Assert.assertEquals("test", fc.front)
        Assert.assertEquals("abc", fc.back)
    }
}