package com.materialdesign.whatsouldido

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView

class AnimationManager {

    fun animateSuggestion(textView: TextView, suggestion: String) {
        // Önce kaybolma animasyonu
        val fadeOut = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f)
        fadeOut.duration = 200

        // Sonra belirme animasyonu
        val fadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f)
        fadeIn.duration = 400

        // Boyut animasyonu
        val scaleX = ObjectAnimator.ofFloat(textView, "scaleX", 0.8f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(textView, "scaleY", 0.8f, 1.2f, 1f)

        scaleX.duration = 500
        scaleY.duration = 500

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(fadeOut, fadeIn)

        val scaleSet = AnimatorSet()
        scaleSet.playTogether(scaleX, scaleY)

        fadeOut.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                textView.text = suggestion
            }
        })

        animatorSet.start()
        scaleSet.start()
    }

    fun animateEmoji(textView: TextView) {
        // Emoji için bounce animasyonu
        val scaleX = ObjectAnimator.ofFloat(textView, "scaleX", 1f, 1.5f, 1f)
        val scaleY = ObjectAnimator.ofFloat(textView, "scaleY", 1f, 1.5f, 1f)

        scaleX.duration = 500
        scaleY.duration = 500
        scaleX.interpolator = AccelerateDecelerateInterpolator()
        scaleY.interpolator = AccelerateDecelerateInterpolator()

        val bounceSet = AnimatorSet()
        bounceSet.playTogether(scaleX, scaleY)
        bounceSet.start()
    }
}