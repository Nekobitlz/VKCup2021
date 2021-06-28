package com.nekobitlz.news_tinder.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nekobitlz.news_tinder.R
import com.nekobitlz.news_tinder.viewmodel.NewsTinderViewModel
import com.nekobitlz.news_tinder.databinding.FragmentNewsTinderBinding
import com.nekobitlz.news_tinder.databinding.NewsFooterViewBinding
import com.nekobitlz.news_tinder.view.custom.CardListener
import com.nekobitlz.news_tinder.view.custom.pulse
import com.nekobitlz.news_tinder.view.custom.px

class NewsTinderFragment : Fragment(R.layout.fragment_news_tinder) {

    private lateinit var adapter: NewsTinderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentNewsTinderBinding.bind(view)
        val cardContainer = binding.cardContainer

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

            override fun onSwipeCancel(position: Int, model: Any) {
                Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show()
            }

            override fun onSwipeCompleted() {
                Toast.makeText(context, "Completed", Toast.LENGTH_SHORT).show()
            }

        })

        cardContainer.maxStackSize = 3
        cardContainer.marginTop = 13.px
        cardContainer.margin = 20.px

        cardContainer.addFooterView(generateFooterView())
        adapter = NewsTinderAdapter(requireContext())
        cardContainer.setAdapter(adapter)

        val viewModel = ViewModelProvider(this).get(NewsTinderViewModel::class.java)
        viewModel.models.observe(viewLifecycleOwner, adapter::submitList)

    }
    private fun generateFooterView(): View {
        val binding = NewsFooterViewBinding.inflate(layoutInflater)
        binding.unlikeFloating.setOnClickListener {
            it.pulse()
            adapter.swipeLeft()
        }
        binding.likeFloating.setOnClickListener {
            it.pulse()
            adapter.swipeRight()
        }
        return binding.root
    }
}