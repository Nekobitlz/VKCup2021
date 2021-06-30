package com.nekobitlz.news_tinder.view

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nekobitlz.news_tinder.R
import com.nekobitlz.news_tinder.viewmodel.NewsTinderViewModel
import com.nekobitlz.news_tinder.databinding.FragmentNewsTinderBinding
import com.nekobitlz.news_tinder.databinding.NewsFooterViewBinding
import com.nekobitlz.news_tinder.view.custom.CardListener
import com.nekobitlz.news_tinder.view.custom.pulse
import com.nekobitlz.news_tinder.view.custom.px
import com.nekobitlz.news_tinder.viewmodel.TinderState
import com.nekobitlz.vkcup.commons.BaseEmptyView
import com.nekobitlz.vkcup.commons.EmptyViewState
import com.nekobitlz.vkcup.vkcup.databinding.BaseEmptyViewBinding

class NewsTinderFragment : Fragment(R.layout.fragment_news_tinder) {

    private lateinit var adapter: NewsTinderAdapter
    private lateinit var emptyView: BaseEmptyView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emptyView = generateEmptyView()
        val binding = FragmentNewsTinderBinding.bind(view)
        val cardContainer = binding.cardContainer
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        cardContainer.setOnCardActionListener(object : CardListener {
            override fun onLeftSwipe(position: Int, model: Any) {
                Toast.makeText(context, "Left", Toast.LENGTH_SHORT).show()
            }

            override fun onRightSwipe(position: Int, model: Any) {
                Toast.makeText(context, "Right", Toast.LENGTH_SHORT).show()
            }

            override fun onItemShow(position: Int, model: Any) {
                Toast.makeText(context, "Item", Toast.LENGTH_SHORT).show()
            }

            override fun onSwipeCancel(view: View, model: Any) {
                adapter.onSwipeCancel(view)
            }

            override fun onSwipeCompleted() {
                emptyView.state = EmptyViewState.Error(
                    getString(R.string.no_more_items),
                    null
                )
            }

            override fun onLeftMove(view: View) {
                adapter.onLeftMove(view)
            }

            override fun onRightMove(view: View) {
                adapter.onRightMove(view)
            }
        })

        cardContainer.setEmptyView(emptyView)
        cardContainer.addFooterView(generateFooterView())
        adapter = NewsTinderAdapter(requireContext())
        cardContainer.setAdapter(adapter)

        val viewModel = ViewModelProvider(this).get(NewsTinderViewModel::class.java)
        viewModel.state.observe(viewLifecycleOwner, {
            when (it) {
                is TinderState.Success -> {
                    emptyView.state = EmptyViewState.None
                    adapter.submitList(it.list)
                }
                TinderState.Loading -> {
                    emptyView.state = EmptyViewState.Loading
                }
                is TinderState.Error -> {
                    emptyView.state = EmptyViewState.Error(
                        getString(R.string.default_error),
                        getString(R.string.retry)
                    )
                }
            }
        })
    }

    private fun generateFooterView(): View {
        val binding = NewsFooterViewBinding.inflate(layoutInflater)
        binding.unlikeFloating.setOnClickListener {
            binding.unlikeFloating.pulse(R.color.unlike_tint_color)
            adapter.swipeLeft()
        }
        binding.likeFloating.setOnClickListener {
            binding.likeFloating.pulse(R.color.like_tint_color)
            adapter.swipeRight()
        }
        return binding.root
    }

    private fun generateEmptyView(): BaseEmptyView = BaseEmptyView(requireContext())
}