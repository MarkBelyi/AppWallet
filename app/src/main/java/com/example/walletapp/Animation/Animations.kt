package com.example.walletapp.Animation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset

class Animations {
    @OptIn(ExperimentalAnimationApi::class)
    fun slideTransitionSpec(): ContentTransform {
        val animationSpec = tween<IntOffset>(durationMillis = 500, easing = FastOutSlowInEasing)
        return (slideInHorizontally(animationSpec = animationSpec) { fullWidth -> fullWidth } + fadeIn(animationSpec = tween(500))).togetherWith(
            slideOutHorizontally(animationSpec = animationSpec) { fullWidth -> -fullWidth } + fadeOut(animationSpec = tween(500))
        )

        /*return if (targetState > initialState) {
            //Влево
            (slideInHorizontally(animationSpec = animationSpec) { fullWidth -> -fullWidth } + fadeIn(animationSpec = tween(500))).togetherWith(
                slideOutHorizontally(animationSpec = animationSpec) { fullWidth -> fullWidth } + fadeOut(animationSpec = tween(500))
            )
        } else {
            //Вправо
            (slideInHorizontally(animationSpec = animationSpec) { fullWidth -> fullWidth } + fadeIn(animationSpec = tween(500))).togetherWith(
                slideOutHorizontally(animationSpec = animationSpec) { fullWidth -> -fullWidth } + fadeOut(animationSpec = tween(500))
            )
        }*/
    }


    @OptIn(ExperimentalAnimationApi::class)
    fun fadeTransitionSpec(): ContentTransform {
        return fadeIn(animationSpec = tween(500, easing = FastOutSlowInEasing)) togetherWith
                fadeOut(animationSpec = tween(500, easing = FastOutSlowInEasing))
    }
}
