package com.brm.mycolleagues.ui.fragment.list

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import com.brm.mycolleagues.utils.*
import com.google.android.material.snackbar.Snackbar
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
                adapter.newList(it.response?.data ?: listOf())
                    .also {
                        binding.fragmentListSwipeRefresh.isRefreshing = false
                        dialog.dismiss()
                    }
            }

            Status.ERROR ->{
                binding.fragmentListSwipeRefresh.isRefreshing = false
                dialog.show()
            }
        }
    }

    private val listEmptyChecker = Observer<Boolean>{
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.circle_explosion_anim).apply {
            duration = 700
            interpolator = AccelerateDecelerateInterpolator()
        }

        when(it){
            true ->{
                binding.apply {
                    circleRevert.isVisible = true
                    circleRevert.startAnimation(animation){
                        listRoot.setBackgroundColor(ContextCompat.
                        getColor(requireContext(), R.color.grey_10))
                        circleRevert.isVisible = false
                        fragmentListEmptyListLayout.isVisible = true
                        fragmentListRecycler.isVisible = false
                    }
                }
            }
            false ->{
                binding.apply {
                    circle.isVisible = true
                    circle.startAnimation(animation){
                        listRoot.setBackgroundColor(ContextCompat.
                        getColor(requireContext(), R.color.white))
                        circle.isVisible = false
                        fragmentListEmptyListLayout.isVisible = false
                        fragmentListRecycler.isVisible = true
                    }
                }
            }
        }
    }

    private val listWorkStatus = Observer<BaseModel<Boolean>>{
        when(it.status){
            Status.LOADING ->{
                dialog.dismiss()
            }
            Status.SUCCESS ->{
                if (it.response?.data != null){
                    it.response.data.apply {
                        AppPreferences.is_online = this
                        listViewModel.isFabVisible(this)
                    }
                }
                else{
                    showMessage("Ошибка обновления статуса работы. Попробуйте еще раз!")
                }
            }
            Status.ERROR ->{
                dialog.show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        listViewModel.isFabVisible(AppPreferences.is_online)

        val mills = System.currentTimeMillis()
        val df = Date(mills)
        val vv: String = SimpleDateFormat("Сегодня dd-MM-yyyy", Locale.getDefault()).format(df)
        (activity as AppCompatActivity).supportActionBar?.title =vv

        view.fragmentListRecycler.adapter = adapter
        view.fragmentListRecycler.layoutManager = LinearLayoutManager(requireContext())
        listViewModel.loadList()

        dialog = Dialog(requireContext(), R.style.AppTheme_NoActionbar)
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

        binding.fragmentListFabStop.setOnClickListener {
            listViewModel.workEndDialog(requireContext())

        }
        binding.fragmentListFabStart.setOnClickListener {
            listViewModel.workStartDialog(requireContext())
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
        searchView?.apply {
            isSubmitButtonEnabled = true
            setOnQueryTextListener(this@ListFragment)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.listMenuAllUsers ->{findNavController().
            navigate(R.id.action_listFragment_to_userFragment)}
            R.id.listMenuProfile ->{ startActivity(Intent(
                requireActivity(), AccountActivity::class.java))}
            R.id.listMenuSignOut ->{signOut()}
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showMessage(message: String){
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
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
        listViewModel.apply {
            loading_status.removeObserver(listObservable)
            is_list_empty.removeObserver(listEmptyChecker)
            work_status.removeObserver(listWorkStatus)
        }
        super.onDestroyView()
    }
}