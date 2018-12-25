package `in`.phoenix.myspends.ui.fragment

import `in`.phoenix.myspends.R
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AppRateFragment.OnAppRateActionListener] interface
 * to handle interaction events.
 * Use the [AppRateFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

const val ACTION_LATER = -1
const val ACTION_RATE_NOW = 0

class AppRateFragment : DialogFragment() {

    private var listener: OnAppRateActionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_app_rate, container, false)
        view.findViewById<AppCompatButton>(R.id.far_abutton_later).setOnClickListener(clickListener)
        view.findViewById<AppCompatButton>(R.id.far_abutton_rate_now).setOnClickListener(clickListener)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnAppRateActionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnAppRateActionListener")
        }
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            with(dialog) {
                window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setTitle("Rate " + getString(R.string.app_name))
                setCanceledOnTouchOutside(false)
                setOnKeyListener(keyListener)
            }
        }
    }

    private val keyListener = DialogInterface.OnKeyListener { dialogInterface: DialogInterface, keyCode: Int, keyEvent: KeyEvent ->
        keyCode == KeyEvent.KEYCODE_BACK
    }

    private val clickListener = View.OnClickListener {
        dismissAllowingStateLoss()
        when (it.id) {
            R.id.far_abutton_later -> {
                listener?.onAppRateAction(ACTION_LATER)
            }
            R.id.far_abutton_rate_now -> {
                listener?.onAppRateAction(ACTION_RATE_NOW)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnAppRateActionListener {
        fun onAppRateAction(action: Int)
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment AppRateFragment.
         */
        @JvmStatic
        fun newInstance() = AppRateFragment()
    }
}
