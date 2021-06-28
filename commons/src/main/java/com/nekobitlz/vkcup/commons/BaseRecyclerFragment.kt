package com.nekobitlz.vkcup.commons

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.nekobitlz.vkcup.vkcup.R
import com.nekobitlz.vkcup.vkcup.databinding.FragmentBaseRecyclerBinding

abstract class BaseRecyclerFragment : Fragment(R.layout.fragment_base_recycler) {

    protected val recyclerBinding: FragmentBaseRecyclerBinding by viewBinding()

    protected open val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder> by lazyUnsychronized {
        createRecyclerAdapter()
    }

    protected lateinit var recyclerView: RecyclerView

    protected abstract fun createRecyclerAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>

    protected fun createRecyclerView(): RecyclerView = recyclerBinding.list

    protected open fun createRecyclerLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRecyclerView()
    }

    protected fun initRecyclerView() {
        recyclerView = createRecyclerView().apply {
            layoutManager = createRecyclerLayoutManager()
            setHasFixedSize(true)
            this.adapter = this@BaseRecyclerFragment.adapter
        }
        recyclerBinding.swipeRefresh.setOnRefreshListener {
            onRefresh()
        }
    }

    protected open fun onRefresh() {
    }

    protected open fun showData() {
        recyclerBinding.swipeRefresh.isRefreshing = false
        recyclerBinding.emptyView.state = EmptyViewState.None
        recyclerBinding.list.visible()
    }

    protected open fun showError(
        text: String = resources.getString(R.string.default_error),
        onRetryClick: (() -> Unit)? = { onRefresh() },
    ) {
        recyclerBinding.swipeRefresh.isRefreshing = false
        recyclerBinding.emptyView.state = EmptyViewState.Error(text, null, onRetryClick)
        recyclerBinding.list.gone()
    }

    protected open fun showLoading() {
        recyclerBinding.emptyView.state = EmptyViewState.Loading
        recyclerBinding.list.gone()
    }
}