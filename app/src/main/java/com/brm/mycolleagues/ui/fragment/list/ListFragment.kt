package com.brm.mycolleagues.ui.fragment.list

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.brm.mycolleagues.R
import com.brm.mycolleagues.databinding.FragmentListBinding
import com.brm.mycolleagues.ui.activity.AccountActivity
import com.brm.mycolleagues.ui.activity.LoginActivity
import com.brm.mycolleagues.ui.fragment.list.model.MonthModel
import com.brm.mycolleagues.ui.fragment.list.model.PersonModel
import com.brm.mycolleagues.ui.fragment.list.vm.ListViewModel
import com.brm.mycolleagues.utils.AppPreferences
import com.brm.mycolleagues.utils.BaseModel
import com.brm.mycolleagues.utils.JsonConverter
import com.brm.mycolleagues.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_list.view.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class ListFragment : Fragment(),
        SearchView.OnQueryTextListener{


    private var _binding : FragmentListBinding? = null
    val binding get() = _binding!!

    private val listViewModel by viewModels<ListViewModel>()
    private val adapter = com.brm.mycolleagues.ui.fragment.list.adapter.ListAdapter()
    private lateinit var dialog: Dialog
    private lateinit var personModel: PersonModel
    private lateinit var monthModel: MonthModel

    @Inject lateinit var jsonConverter: JsonConverter


    private val listObservable = Observer<BaseModel<List<PersonModel>>>{
        when(it.status){
            Status.LOADING ->{

            }
            Status.SUCCESS ->{
                adapter.newList(it.response!!.data!!)
                binding.fragmentListSwipeRefresh.isRefreshing = false
                dialog.dismiss()
            }

            Status.ERROR ->{
                binding.fragmentListSwipeRefresh.isRefreshing = false
                dialog.show()
            }
        }
    }

    private val listEmptyChecker = Observer<Boolean>{

        when(it){
            true ->{
                binding.fragmentListEmptyListLayout.visibility = View.VISIBLE
                binding.fragmentListRecycler.visibility = View.INVISIBLE
            }
            false ->{
                binding.fragmentListEmptyListLayout.visibility = View.INVISIBLE
                binding.fragmentListRecycler.visibility = View.VISIBLE
            }
        }
    }

    private val listWorkStatus = Observer<BaseModel<Boolean>>{
        when(it.status){
            Status.LOADING ->{}
            Status.SUCCESS ->{
                AppPreferences.is_online = it.response?.data!!
                dialog.dismiss()
            }
            Status.ERROR ->{
                dialog.show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        _binding =  FragmentListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = listViewModel
        binding.context = requireContext()

        val mills = System.currentTimeMillis()
        val df = Date(mills)
        val vv: String = SimpleDateFormat("Сегодня dd-MM-yyyy", Locale.getDefault()).format(df)
        (activity as AppCompatActivity).supportActionBar?.title =vv

        view.fragmentListRecycler.adapter = adapter
        view.fragmentListRecycler.layoutManager = LinearLayoutManager(requireContext())
        listViewModel.loadList()

        dialog = Dialog(requireContext(), R.style.Theme_MyColleagues_NoActionBar)
        dialog.setContentView(R.layout.dialog_server_error)
        val btnRetry = dialog.findViewById<TextView>(R.id.dialogServerRetryText)
        val btnCancel = dialog.findViewById<TextView>(R.id.dialogServerCancel)

        val month : Int = SimpleDateFormat("M", Locale.getDefault()).format(df).toInt()
        val year : Int = SimpleDateFormat("yyyy", Locale.getDefault()).format(df).toInt()
        personModel = jsonConverter.reconvertResponse()
        monthModel = MonthModel(month, year, 0)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnRetry.setOnClickListener {
            listViewModel.loadList()
            dialog.dismiss()
        }

        listViewModel.loading_status.observe(viewLifecycleOwner, listObservable)
        listViewModel.is_list_empty.observe(viewLifecycleOwner, listEmptyChecker)
        listViewModel.work_status.observe(viewLifecycleOwner, listWorkStatus)


        binding.fragmentListSwipeRefresh.setOnRefreshListener {
            listViewModel.loadList()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null){
            adapter.filter.filter(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null){
            adapter.filter.filter(newText)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
        val search = menu.findItem(R.id.listMenuSearch)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.listMenuAllUsers ->{findNavController().navigate(R.id.action_listFragment_to_userFragment)}
            R.id.listMenuProfile ->{ startActivity(Intent(requireActivity(), AccountActivity::class.java))}
            R.id.listMenuSignOut ->{signOut()}
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOut() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
                dialogBuilder.setMessage(requireContext().getString(R.string.sign_out_conformation))
                    // if the dialog is cancelable
                    .setCancelable(true)
                    .setPositiveButton(requireContext().getString(R.string.yes)) { _, _ ->
                        AppPreferences.removeAll()
                        startActivity(Intent(requireActivity(), LoginActivity::class.java))
                        activity?.finish()
                    }
                        .setNegativeButton(requireContext().getString(R.string.cancel)) { _, _ ->}
                val alert = dialogBuilder.create()
                alert.show()

    }

    override fun onDestroyView() {
        _binding = null
        listViewModel.loading_status.removeObserver(listObservable)
        listViewModel.is_list_empty.removeObserver(listEmptyChecker)
        listViewModel.work_status.removeObserver(listWorkStatus)
        super.onDestroyView()
    }
}