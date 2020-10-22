@file:Suppress("unused")

package com.codegames.simplelist.anim

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import com.codegames.simplelist.SimpleConf
import com.codegames.simplelist.adapter.SimpleAdapter

@Suppress("PropertyName")
class SimpleItemAnimatorConfig(
    var duration: Long? = null,
    var delay: Long? = null,
    var direction: Int? = null
)

fun SimpleAdapter<*>.animateItemFadeIn(
    view: View,
    position: Int,
    config: SimpleItemAnimatorConfig.() -> Unit
) {
    val c = SimpleItemAnimatorConfig().also {
        config(it)
    }
    val p = position + 1
    val duration = c.duration ?: 500
    val delay = c.delay ?: 300
    view.alpha = 0f
    val animatorSet = AnimatorSet()
    val animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 0.5f, 1.0f)
    ObjectAnimator.ofFloat(view, "alpha", 0f).start()
    animator.startDelay = (if (this.scrolled) delay else p * delay * 0.66).toLong()
    animator.duration = duration
    animatorSet.play(animator)
    animator.start()
}


fun SimpleAdapter<*>.animateItemSlideIn(
    view: View,
    position: Int,
    config: SimpleItemAnimatorConfig.() -> Unit
) {
    val c = SimpleItemAnimatorConfig().also {
        config(it)
    }
    val p = position + 1
    val duration = c.duration ?: 300
    val delay = c.delay ?: 300
    val direction = c.direction ?: SimpleConf.DIR_LEFT_TO_RIGHT
    val start = when (direction) {
        SimpleConf.DIR_LEFT_TO_RIGHT -> -view.width.toFloat()
        SimpleConf.DIR_RIGHT_TO_LEFT -> view.width.toFloat()
        SimpleConf.DIR_TOP_TO_BOTTOM -> -view.height.toFloat()
        SimpleConf.DIR_BOTTOM_TO_TOP -> view.height.toFloat()
        else -> throw Throwable("Wrong Direction: $direction")
    }

    when (direction) {
        SimpleConf.DIR_LEFT_TO_RIGHT, SimpleConf.DIR_RIGHT_TO_LEFT -> view.translationX = start
        SimpleConf.DIR_TOP_TO_BOTTOM, SimpleConf.DIR_BOTTOM_TO_TOP -> view.translationY = start
        else -> throw Throwable("Wrong Direction: $direction")
    }

    view.alpha = 0f
    val animatorSet = AnimatorSet()
    val animatorTranslate: ObjectAnimator =
        ObjectAnimator.ofFloat(
            view, when (direction) {
                SimpleConf.DIR_LEFT_TO_RIGHT, SimpleConf.DIR_RIGHT_TO_LEFT -> "translationX"
                SimpleConf.DIR_TOP_TO_BOTTOM, SimpleConf.DIR_BOTTOM_TO_TOP -> "translationY"
                else -> throw Throwable("Wrong Direction: $direction")
            }, start, 0F
        )
    val animatorAlpha = ObjectAnimator.ofFloat(view, "alpha", 1f)
    animatorTranslate.startDelay = if (this.scrolled) delay / 2 else p * delay / 2
    animatorTranslate.duration = ((if (this.scrolled) 1F else 0.5F) * duration).toLong()
    animatorSet.playTogether(animatorTranslate, animatorAlpha)
    animatorSet.start()
}