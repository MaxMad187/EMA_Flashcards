package de.hsworms.flashcards

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.edit
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import de.hsworms.flashcard.database.FCDatabase
import de.hsworms.flashcard.database.entity.FlashcardNormal
import de.hsworms.flashcard.database.entity.Repository
import de.hsworms.flashcard.database.entity.RepositoryCardCrossRef
import de.hsworms.flashcards.ui.home.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {

    companion object {
        const val SAVE_DIALOG: Int = 404
        const val OPEN_DIALOG: Int = 405
        var SAVE_REPO_ID: Int = -1
    }

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var pref: SharedPreferences
    private var darkmode: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawer_layout)
        navView.setupWithNavController(navController)

        // Dark mode
        pref = getPreferences(Context.MODE_PRIVATE)
        darkmode = pref.getBoolean(
            "darkmode",
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        )
        when (darkmode) {
            true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val item = navView.menu.findItem(R.id.menu_darkmode)
        val switch = item.actionView as SwitchCompat
        switch.isChecked = darkmode
        switch.setOnCheckedChangeListener { _, b ->
            when (b) {
                true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            darkmode = b
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        pref.edit {
            putBoolean("darkmode", darkmode)
            commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // FOR THE EXPORTING
        if (requestCode == SAVE_DIALOG) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data?.data!!
                val ctx = applicationContext
                GlobalScope.launch {
                    val obj = JSONObject()
                    FCDatabase.getDatabase(ctx).apply {
                        val repo = repositoryDao().getRepositoryWithCards(SAVE_REPO_ID)!!
                        obj.put("name", repo.repository.name)
                        val cards = JSONArray()
                        repo.cards.forEach {
                            val card = JSONObject()
                            card.put("type", it.type)
                            val fc = flashcardDao().getOne(it.cardId!!)
                            if (fc is FlashcardNormal) {
                                card.put("front", fc.front)
                                card.put("back", fc.back)
                            }
                            cards.put(card)
                        }
                        obj.put("cards", cards)
                    }


                    withContext(Dispatchers.IO) {
                        BufferedWriter(OutputStreamWriter(contentResolver.openOutputStream(uri))).use {
                            it.write(obj.toString())
                            it.flush()
                        }
                    }
                }
            }
        }
        // FOR THE IMPORTING
        if (requestCode == OPEN_DIALOG) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data?.data!!
                val ctx = applicationContext
                GlobalScope.launch {
                    val str = withContext(Dispatchers.IO) {
                        BufferedReader(InputStreamReader(contentResolver.openInputStream(uri))).use {
                            it.readText()
                        }
                    }

                    val obj = JSONObject(str)
                    FCDatabase.getDatabase(ctx).apply {
                        val repo = Repository(name = obj.getString("name"))
                        val id = repositoryDao().insert(repo)[0].toInt()
                        val arr = obj.getJSONArray("cards")
                        for (i in 1..arr.length()) {
                            val c = arr.getJSONObject(i - 1)
                            val fc = FlashcardNormal(front = c.getString("front"), back = c.getString("back"))
                            val cid = flashcardDao().insert(fc)[0]
                            repositoryDao().addCard(RepositoryCardCrossRef(id, cid, 0, 0))
                        }
                        runOnUiThread {
                            val curr =
                                supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager?.fragments!![0]
                            if (curr is HomeFragment) {
                                curr.fetchData()
                            }
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
