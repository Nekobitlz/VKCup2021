package com.nekobitlz.news_tinder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.lifecycle.ViewModelProvider
import com.nekobitlz.news_tinder.databinding.CardNewsTinderBinding
import com.nekobitlz.news_tinder.databinding.FragmentNewsTinderBinding
import com.nekobitlz.vkcup.commons.getTimeAgo
import com.nekobitlz.vkcup.commons.setImageOrGone
import com.nekobitlz.vkcup.commons.setTextOrGone
import com.nekobitlz.vkcup.commons.setTextOrHide

class NewsTinderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = FragmentNewsTinderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel = ViewModelProvider(this).get(TinderContactViewModel::class.java)

        viewModel.modelStream.observe(this, { bindCard(binding, it) })

        binding.motionLayout.setTransitionListener(object : TransitionAdapter() {
            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                when (currentId) {
                    R.id.offScreenUnlike,
                    R.id.offScreenLike -> {
                        motionLayout.progress = 0f
                        motionLayout.setTransition(R.id.start, R.id.detail)
                        viewModel.swipe()
                    }
                }
            }

            override fun onTransitionStarted(
                motionLayout: MotionLayout,
                startId: Int,
                endId: Int
            ) {
                if (startId == R.id.start && (endId == R.id.unlike || endId == R.id.like)) {
                //    binding.likeFloating.setOnClickListener(null)
                  //  binding.unlikeFloating.setOnClickListener(null)
                }
            }
        })

        binding.likeFloating.setOnClickListener {
            binding.motionLayout.transitionToState(R.id.like)
        }

        binding.unlikeFloating.setOnClickListener {
            binding.motionLayout.transitionToState(R.id.unlike)
        }
    }

    private fun bindCard(binding: FragmentNewsTinderBinding, model: TinderContactModel) =
        with(binding) {
            bindCard(cardOneContainer, model.cardTop)
            bindCard(cardSecondContainer, model.cardBottom)
        }

    private fun bindCard(binding: CardNewsTinderBinding, model: TinderContactCardModel) = with (binding) {
        tvName.setTextOrHide(model.name)
        tvTitle.setTextOrGone(model.title)
        tvTime.text = getTimeAgo(resources, model.date?.toLong() ?: 0)
        ivBackground.setImageURI(model.imageUrl)
        ivAvatar.setImageOrGone(model.avatarUrl)
        tvLikeCount.text = model.getSaveCount(model.likeCount)
        tvCommentsCount.text = model.getSaveCount(model.commentCount)
        tvShareCount.text = model.getSaveCount(model.shareCount)
    }
}