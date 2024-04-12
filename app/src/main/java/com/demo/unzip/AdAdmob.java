package com.demo.unzip;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


public class AdAdmob {

    public  static String BannerAdID = "/6499/example/banner", FullscreenAdID = "/6499/example/interstitial";

    static ProgressDialog ProgressDialog;

    public AdAdmob(Activity activity) {


        MobileAds.initialize(activity, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus
                                                         initializationStatus) {
            }
        });


    }


    public void BannerAd(final RelativeLayout Ad_Layout, Activity activity) {


        AdView mAdView = new AdView(activity);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(BannerAdID);
        AdRequest adore = new AdRequest.Builder().build();
        mAdView.loadAd(adore);
        Ad_Layout.addView(mAdView);


        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                Ad_Layout.setVisibility(View.VISIBLE);
                super.onAdLoaded();

                Log.e("ddddd", "dddd");
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Ad_Layout.setVisibility(View.INVISIBLE);
                Log.e("ddddd1", "dddd");

            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mAdView.destroy();
                Ad_Layout.setVisibility(View.INVISIBLE);
                Log.e("ddddd2", "dddd" + loadAdError.getMessage());

            }
        });


    }

    public void FullscreenAd(final Activity activity) {
        Ad_Popup(activity);

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(activity, FullscreenAdID, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {

                        interstitialAd.show(activity);
                        ProgressDialog.dismiss();

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        
                        ProgressDialog.dismiss();
                    }
                });
    }


    private static final String Count_Ads = "Count_Ads";


    public static void setCount_Ads(Context mContext, int string) {
        mContext.getSharedPreferences(mContext.getPackageName(), 0).edit()
                .putInt(Count_Ads, string).commit();
    }

    public static int getCount_Ads(Context mContext) {
        return mContext.getSharedPreferences(mContext.getPackageName(), 0)
                .getInt(Count_Ads, 1);
    }

    public static void FullscreenAd_Counter(final Activity activity) {



        int counter_ads = getCount_Ads(activity);

        if (counter_ads >= 3) {

            setCount_Ads(activity, 1);

            try {

                Ad_Popup(activity);

                AdRequest adRequest = new AdRequest.Builder().build();

                InterstitialAd.load(activity, FullscreenAdID, adRequest,
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {

                                interstitialAd.show(activity);
                                ProgressDialog.dismiss();

                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                                ProgressDialog.dismiss();
                            }
                        });

            } catch (Exception e) {

            }

        } else {
            counter_ads = counter_ads + 1;
            setCount_Ads(activity, counter_ads);

        }






    }




     private static void Ad_Popup(Context mContext) {
        

        ProgressDialog = ProgressDialog.show(mContext, "", "Ad Loading . . .", true);
        ProgressDialog.setCancelable(true);
        ProgressDialog.show();

    }


}