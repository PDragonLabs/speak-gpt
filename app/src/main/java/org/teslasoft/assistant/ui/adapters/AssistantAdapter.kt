package org.teslasoft.assistant.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import org.teslasoft.assistant.R

class AssistantAdapter(data: ArrayList<HashMap<String, Any>>?, context: FragmentActivity) : AbstractChatAdapter(data, context) {
    @SuppressLint("InflateParams", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val mView: View? = if (dataArray?.get(position)?.get("isBot") == true) {
            inflater.inflate(R.layout.view_assistant_bot_message, null)
        } else {
            inflater.inflate(R.layout.view_assistant_user_message, null)
        }

        icon = mView!!.findViewById(R.id.icon)
        message = mView.findViewById(R.id.message)
        dalleImage = mView.findViewById(R.id.dalle_image)
        imageFrame = mView.findViewById(R.id.image_frame)
        btnCopy = mView.findViewById(R.id.btn_copy)

        super.getView(position, mView, parent)

        icon?.setImageResource(R.drawable.assistant)

        return mView
    }
}