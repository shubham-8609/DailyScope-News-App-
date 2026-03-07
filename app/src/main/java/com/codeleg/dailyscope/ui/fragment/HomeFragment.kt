package com.codeleg.dailyscope.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.codeleg.dailyscope.DailyScope
import com.codeleg.dailyscope.R
import com.codeleg.dailyscope.database.network.RetrofitInstance
import com.codeleg.dailyscope.database.repository.NewsRepository
import com.codeleg.dailyscope.databinding.FragmentHomeBinding
import com.codeleg.dailyscope.ui.adapter.NewsListAdapter
import com.codeleg.dailyscope.ui.viewmodel.MainViewModel
import com.codeleg.dailyscope.ui.viewmodel.MainViewModelFactory
import com.codeleg.dailyscope.utils.FilterState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var newsAdapter: NewsListAdapter
    private val newsRepo by lazy {
        NewsRepository(RetrofitInstance.newsApi, (requireActivity().application as DailyScope).newsDao)
    }
    private val mainVM: MainViewModel by activityViewModels { MainViewModelFactory(newsRepo) }
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainVM.news.collectLatest { pagingData ->
                    newsAdapter.submitData(pagingData)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainVM.isRefreshing.collect { isRefreshing ->
                    binding.swipeRefresh.isRefreshing = isRefreshing
                }
            }
        }
        binding.swipeRefresh.setOnRefreshListener {
            refreshNews()

        }
        requireActivity().addMenuProvider(object: MenuProvider{
            override fun onCreateMenu(
                menu: Menu,
                menuInflater: MenuInflater
            ) {
                menu.clear()
                menuInflater.inflate(com.codeleg.dailyscope.R.menu.home_menu, menu)
                val clearFilterItem =   menu.findItem(R.id.option_clear_filter)
                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        mainVM.filterState.collect { state ->
                            val isApplied = state.category != null || state.date != null
                            clearFilterItem?.isVisible = isApplied
                        }
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {


                return when(menuItem.itemId){
                    R.id.option_refresh -> {
                        refreshNews()
                        true
                    }
                    R.id.option_filter_news -> {
                        // Handle filter news action
                        FilterFragment().show(parentFragmentManager , "FilterFragment")
                        true
                    }
                    R.id.option_clear_filter -> {
                        mainVM.clearFilters()
                        Toast.makeText(requireActivity() , "Filters cleared. Showing all news." , Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }


        } , viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun refreshNews() {
        mainVM.refreshNews()
        if(mainVM.isFilterApplied){
            Toast.makeText(requireActivity() , "Filters are reset after refresh. Please apply filters again." , Toast.LENGTH_SHORT).show()
            mainVM.clearFilters()
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsListAdapter()
        binding.rvNews.apply {
            adapter = newsAdapter
            setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}