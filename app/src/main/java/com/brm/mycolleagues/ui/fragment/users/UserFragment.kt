package com.brm.mycolleagues.ui.fragment.users

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.brm.mycolleagues.R
import com.brm.mycolleagues.databinding.FragmentUserBinding
import com.brm.mycolleagues.ui.fragment.list.adapter.ListAdapter
import com.brm.mycolleagues.ui.fragment.list.model.PersonModel
import com.brm.mycolleagues.ui.fragment.users.adapter.UserAdapter
import com.brm.mycolleagues.ui.fragment.users.vm.UserViewModel
import com.brm.mycolleagues.utils.BaseModel
import com.brm.mycolleagues.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_user.view.*

@AndroidEntryPoint
class UserFragment : Fragment() {

    private val userViewModel by viewModels<UserViewModel>()
    private val adapter = UserAdapter()
    private lateinit var dialog: Dialog

    private var _binding: FragmentUserBinding? = null
    val binding get() = _binding!!

    private val userObserver = Observer<BaseModel<List<PersonModel>>>{
        when(it.status){
            Status.LOADING ->{
                userViewModel.loaderStatusChange(true)
            }
            Status.SUCCESS ->{
                if (it.response?.data != null){
                    with(adapter) { newList(list = it.response.data) }
                }
                userViewModel.loaderStatusChange(false)
                dialog.dismiss()
                }
            Status.ERROR ->{
                userViewModel.loaderStatusChange(false)
                dialog.show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =  FragmentUserBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this
        binding.viewModel = userViewModel

        binding.fragmentUserRecycler.adapter = adapter
        binding.fragmentUserRecycler.layoutManager = LinearLayoutManager(requireContext())

        userViewModel.loading_status.observe(viewLifecycleOwner, userObserver)
        userViewModel.loadUsers()

        dialog = Dialog(requireContext(), R.style.AppTheme_NoActionbar)
        dialog.setContentView(R.layout.dialog_server_error)

        val btnRetry = dialog.findViewById<TextView>(R.id.dialogServerRetryText)
        val btnCancel = dialog.findViewById<TextView>(R.id.dialogServerCancel)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnRetry.setOnClickListener {
            userViewModel.loadUsers()
            dialog.dismiss()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        userViewModel.loading_status.removeObserver(userObserver)
    }
}