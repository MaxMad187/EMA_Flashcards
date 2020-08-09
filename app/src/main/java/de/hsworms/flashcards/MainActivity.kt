package de.hsworms.flashcards

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {

    companion object {
        const val SAVE_DIALOG: Int = 404
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
        darkmode = pref.getBoolean("darkmode", resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
        when(darkmode) {
            true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val item = navView.menu.findItem(R.id.menu_darkmode)
        val switch = item.actionView as SwitchCompat
        switch.isChecked = darkmode
        switch.setOnCheckedChangeListener { _, b ->
            when(b) {
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // FOR THE EXPORTING
        if(requestCode == SAVE_DIALOG) {
            if(resultCode == Activity.RESULT_OK) {
                val uri = data?.data!!
                GlobalScope.launch {
                    val repo = FCDatabase.getDatabase(applicationContext).repositoryDao().getRepositoryWithCards(SAVE_REPO_ID)!!
                    val obj = JSONObject()
                    obj.put("name", repo.repository.name)
                    val cards = JSONArray()
                    repo.cards.forEach {
                        val card = JSONObject()
                        card.put("type", it.type)
                        val fc = FCDatabase.getDatabase(applicationContext).flashcardDao().getOne(it.cardId!!)
                        if(fc is FlashcardNormal) {
                            card.put("front", fc.front)
                            card.put("back", fc.back)
                        }
                        cards.put(card)
                    }
                    obj.put("cards", cards)
                    val bw = BufferedWriter(OutputStreamWriter(contentResolver.openOutputStream(uri)))
                    bw.write(obj.toString())
                    bw.flush()
                    bw.close()
                }
            }
        }
        // FOR THE IMPORTING
        // TODO
        super.onActivityResult(requestCode, resultCode, data)
    }
}
