package `in`.phoenix.myspends.controller

import `in`.phoenix.myspends.R
import `in`.phoenix.myspends.model.WAEntity
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Charan on March 07, 2021
 */
class WAEntryViewHolder(private val itemView: View,
                        private val clickListener: View.OnClickListener):
        RecyclerView.ViewHolder(itemView) {

    private val tvCodeNumber: AppCompatTextView = itemView.findViewById(R.id.lweiTvCodeNumber)
    private val tvAddedOn: AppCompatTextView = itemView.findViewById(R.id.lweiTvAddedOn)

    private val ivWhatsApp: ImageView = itemView.findViewById(R.id.lweiIvWA)
    private val ivDial: ImageView = itemView.findViewById(R.id.lweiIvDial)
    private val ivDelete: ImageView = itemView.findViewById(R.id.lweiIvDelete)

    fun setData(waEntity: WAEntity, position: Int) {
        tvCodeNumber.text = "${waEntity.code} ${waEntity.number}"
        tvAddedOn.text = waEntity.addedOn

        ivWhatsApp.tag = position
        ivDial.tag = position
        ivDelete.tag = position

        ivWhatsApp.setOnClickListener(clickListener)
        ivDial.setOnClickListener(clickListener)
        ivDelete.setOnClickListener(clickListener)
    }

}