package org.boro.breezeclient

import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.*
import org.boro.breezeclient.adapter.PeakFlowAdapter
import org.boro.breezeclient.domain.PeakFlow
import java.time.Instant

class MainActivity : AppCompatActivity() {

    lateinit var adapter: PeakFlowAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = PeakFlowAdapter(this.baseContext)

        resultList.layoutManager = LinearLayoutManager(this)
        resultList.adapter = adapter

        fab.setOnClickListener { showCreateDialog() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.buttons, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.refreshButton -> {
            adapter.refresh()
            Toast.makeText(this.baseContext, R.string.resultsRefreshed, Toast.LENGTH_LONG).show()
            true
        } else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun showCreateDialog() {
        val input = EditText(this@MainActivity)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        AlertDialog.Builder(this)
            .setView(input)
            .setTitle("Save Result")
            .setPositiveButton("Save") { dialog, whichButton ->
                val result = PeakFlow(
                    value = input.text.toString().toInt(),
                    checkedAt = Instant.now()
                )
                adapter.create(result)
            }
            .setNegativeButton("Cancel") { dialog, whichButton -> }
            .create()
            .show()
    }
}