package com.besaba.anvarov.orenerlangcalculator

import android.app.Activity
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

object Ads {
    fun showBottomBanner(activity: Activity) {

        val bannerH = bannerHeight(activity)

        val mAdView = AdView(activity)
        mAdView.adSize = AdSize.SMART_BANNER
        mAdView.adUnitId = activity.getString(R.string.banner_ad_unit_id)

        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                setupContentViewPadding(activity, true, bannerH)
            }
        }

        val mAdRequest = AdRequest.Builder().build()
        //AdRequest mAdRequest =new com.google.android.gms.ads.AdRequest.Builder()
        //      .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        //    .addTestDevice("33175F884727E60CF084D3AB52AE82F4").build();

        mAdView.loadAd(mAdRequest)

        val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, bannerH + 32)

        layoutParams.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        activity.window.addContentView(mAdView, layoutParams)
    }

    fun setupContentViewPadding(activity: Activity, top: Boolean, margin: Int) {
        val view = (activity.window.decorView.findViewById(android.R.id.content) as ViewGroup).getChildAt(0)
        if (top)
            view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, margin)
        else
            view.setPadding(view.paddingLeft, margin, view.paddingRight, view.paddingBottom)
    }

    private fun bannerHeight(activity: Activity): Int {
        val display = activity.windowManager.defaultDisplay
        val displayMetrics = DisplayMetrics()
        display.getMetrics(displayMetrics)
        val buf = displayMetrics.widthPixels
        return when {
            buf <= 400 -> 50
            buf > 720 -> 108
            else -> 68
        }
    }
}