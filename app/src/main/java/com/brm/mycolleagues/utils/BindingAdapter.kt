package com.brm.mycolleagues.utils

import android.content.DialogInterface
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.brm.mycolleagues.R
import com.brm.mycolleagues.ui.fragment.list.ListFragmentDirections
import com.brm.mycolleagues.ui.fragment.list.model.PersonModel
import com.brm.mycolleagues.ui.fragment.list.vm.ListViewModel
import com.brm.mycolleagues.ui.fragment.users.UserFragmentDirections
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import com.wang.avi.AVLoadingIndicatorView
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class BindingAdapter {
    companion object{
        @BindingAdapter("android:imageFromUrl")
        @JvmStatic
        fun imageFromUrl(view: CircleImageView, url: String){
            if (url != ""){
                Picasso.with(view.context).load(url).placeholder(R.drawable.ic_person).into(view)
            }
            else{
                view.setBackgroundResource(R.drawable.ic_person)
            }

        }

        @BindingAdapter("android:workingFrom")
        @JvmStatic
        fun workingFrom(view: TextView, from: Long){
            val df = Date(from)
            val vv: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(df)
            view.text = "На работе с $vv"
        }

        @BindingAdapter("android:fromWorkerToProfile")
        @JvmStatic
        fun fromWorkerToProfile(view:RelativeLayout, model: PersonModel){
            view.setOnClickListener{
                view.findNavController().navigate(ListFragmentDirections.actionListFragmentToProfileFragment(model))
            }
        }
        @BindingAdapter("android:fromWorkerNoTitleToProfile")
        @JvmStatic
        fun fromWorkerNoTitleToProfile(view: RelativeLayout, model: PersonModel){
            view.setOnClickListener { view.findNavController().navigate(UserFragmentDirections.actionUserFragmentToProfileFragment(model)) }
        }

        @BindingAdapter("android:loaderVisibility")
        @JvmStatic
        fun loaderVisibility(view: AVLoadingIndicatorView, it: BaseModel<List<PersonModel>>){
            when(it.status){
                Status.LOADING ->{view.visibility = View.VISIBLE}
                Status.SUCCESS ->{view.visibility = View.INVISIBLE}
                Status.ERROR ->{view.visibility = View.INVISIBLE}
            }
        }

    }


}