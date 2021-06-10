package org.boro.breezeclient.adapter

import android.content.Context
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.list_item.view.*
import org.boro.breezeclient.R
import org.boro.breezeclient.adapter.rest.ApiClient
import org.boro.breezeclient.adapter.rest.ApiRequest
import org.boro.breezeclient.domain.PeakFlow
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class PeakFlowAdapter(val context: Context) :
    RecyclerView.Adapter<PeakFlowAdapter.LungCapacityViewHolder>() {

    private val client by lazy { ApiClient.create() }
    private val formatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("d MMMM")
            .withZone(ZoneId.of("Europe/Warsaw"))

    private var results: ArrayList<PeakFlow> = ArrayList()

    init {
        refresh()
    }

    fun refresh() {
        client.getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                results.clear()
                results.addAll(response)
                notifyDataSetChanged()
            }, { error ->
                Toast.makeText(context, "Refresh error: ${error.message}", Toast.LENGTH_LONG)
                    .show()
                Log.e("ERRORS", error.message!!)
            })
    }

    fun create(peakFlow: PeakFlow) {
        val request = ApiRequest(
            value = peakFlow.value,
            checkedAt = peakFlow.checkedAt
        )
        client.create(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ refresh() }, { error ->
                Toast.makeText(context, "Update error: ${error.message}", Toast.LENGTH_LONG)
                    .show()
                Log.e("ERRORS", error.message!!)
            })
    }

    fun update(peakFlow: PeakFlow) {
        val request = ApiRequest(
            value = peakFlow.value,
            checkedAt = peakFlow.checkedAt
        )

        client.update(peakFlow.id!!, request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ refresh() }, { error ->
                Toast.makeText(context, "Update error: ${error.message}", Toast.LENGTH_LONG)
                    .show()
                Log.e("ERRORS", error.message!!)
            })
    }

    fun delete(peakFlow: PeakFlow) {
        client.delete(peakFlow.id!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ refresh() }, { error ->
                Toast.makeText(context, "Delete error: ${error.message}", Toast.LENGTH_LONG)
                    .show()
                Log.e("ERRORS", error.message!!)
            })
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LungCapacityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return LungCapacityViewHolder(view)
    }

    override fun onBindViewHolder(holder: LungCapacityViewHolder, position: Int) {
        holder.view.value.text = results[position].value.toString()
        holder.view.checkedAt.text = formatter.format(results[position].checkedAt)
        holder.view.moreActions.setOnClickListener {showMoreActionsPopup(holder, results[position])}
    }

    private fun showMoreActionsPopup(
        holder: LungCapacityViewHolder,
        peakFlow: PeakFlow
    ) {
        //creating a popup menu
        val popup = PopupMenu(context, holder.view.moreActions)

        //inflating menu from xml resource
        popup.inflate(R.menu.options_menu)

        //adding click listener
        popup.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener,
            PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                when (item.getItemId()) {
                    R.id.editOption -> {showUpdateDialog(holder, peakFlow)}
                    R.id.deleteOption -> {showDeleteDialog(holder, peakFlow)}
                }
                return false
            }
        })

        //displaying the popup
        popup.show()
    }

    override fun getItemCount() = results.size


    class LungCapacityViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private fun showUpdateDialog(
        holder: LungCapacityViewHolder,
        peakFlow: PeakFlow
    ) {
        val input = EditText(holder.view.context)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        input.setText(peakFlow.value.toString())

        AlertDialog.Builder(holder.view.context)
            .setView(input)
            .setTitle("Update Result")
            .setPositiveButton("Update") { dialog, whichButton ->
                val result = peakFlow.copy(
                    value = input.text.toString().toInt()
                )
                this.update(result)
            }
            .setNegativeButton("Cancel") { dialog, whichButton -> dialog.cancel() }
            .create()
            .show()
    }

    private fun showDeleteDialog(
        holder: LungCapacityViewHolder,
        peakFlow: PeakFlow
    ) {
        AlertDialog.Builder(holder.view.context)
            .setTitle("Delete Result")
            .setMessage("Confirm delete?")
            .setPositiveButton("Delete") { dialog, whichButton ->
                this.delete(peakFlow)
            }
            .setNegativeButton("Cancel") { dialog, whichButton -> dialog.cancel() }
            .create()
            .show()
    }
}