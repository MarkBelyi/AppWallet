package com.example.walletapp.animation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp

class ColumnAnimator {

    companion object {
        @Composable
        fun animateColumnOffset(targetOffset: Dp): Dp {
            val animatedOffset by animateDpAsState(targetValue = targetOffset)
            return animatedOffset
        }
    }
}

fun springAnimationSpec(): AnimationSpec<Dp> {
    return spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
}