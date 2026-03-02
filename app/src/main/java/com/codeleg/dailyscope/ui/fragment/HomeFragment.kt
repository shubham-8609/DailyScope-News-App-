package com.codeleg.dailyscope.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.codeleg.dailyscope.database.model.NewsUiState
import com.codeleg.dailyscope.database.network.RetrofitInstance
import com.codeleg.dailyscope.database.repository.NewsRepository
import com.codeleg.dailyscope.databinding.FragmentHomeBinding
import com.codeleg.dailyscope.ui.viewmodel.MainViewModel
import com.codeleg.dailyscope.ui.viewmodel.MainViewModelFactory
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private val newsRepo by lazy {
        NewsRepository(RetrofitInstance.newsApi)
    }
    val mainVM: MainViewModel by activityViewModels {
        MainViewModelFactory(newsRepo)
    }
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

        /*viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainVM.topNews.collect { state ->
                    binding.tvTest.text = when (state) {
                        NewsUiState.Loading -> "Loading..."
                        is NewsUiState.Success -> "Success: ${state.news.size} articles"
                        is NewsUiState.Error -> "Error: ${state.message}"
                    }
                }
            }
        }*/
        mainVM.fetchNews()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}