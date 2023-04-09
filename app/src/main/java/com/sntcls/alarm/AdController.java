package com.sntcls.alarm;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsLoadOptions;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.IUnityBannerListener;
import com.unity3d.services.banners.UnityBannerSize;
import com.unity3d.services.banners.UnityBanners;
import com.unity3d.services.banners.view.BannerPosition;

import java.util.List;

public class AdController
{
    static String unityAdsGameId = "5137508";
    static String unityAdRewarded = "Rewarded_Android";
    static String unityAdBanner = "Banner_Android";
    static String unityAdInterstitial = "Interstitial_Android";
    static boolean unityAdsTestMode = true;

    static void ShowGoogleAdBanner(Context context, View view)
    {
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                AdView adView = (AdView) view;
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
            }
        });
    }
    static void ShowGoogleAdBanners(Context context, List<View> views)
    {
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                for (View view : views) {
                    AdView adView = (AdView) view;
                    AdRequest adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);
                }
            }
        });
    }

    static void ShowUnityAdBanner(Activity activity, ViewGroup targetContainer)
    {
        UnityBanners.setBannerListener(new IUnityBannerListener() {
            @Override
            public void onUnityBannerLoaded(String s, View view) {
                targetContainer.removeAllViews();
                targetContainer.addView(view);
            }

            @Override
            public void onUnityBannerUnloaded(String s) {

            }

            @Override
            public void onUnityBannerShow(String s) {

            }

            @Override
            public void onUnityBannerClick(String s) {

            }

            @Override
            public void onUnityBannerHide(String s) {

            }

            @Override
            public void onUnityBannerError(String s) {

            }
        });
        UnityAds.initialize(activity, unityAdsGameId, new IUnityAdsInitializationListener() {
            @Override
            public void onInitializationComplete() {
                UnityBanners.setBannerPosition(BannerPosition.BOTTOM_CENTER);
                UnityBanners.loadBanner(activity, unityAdBanner);
            }

            @Override
            public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {

            }
        });
    }

    static void PlayAdVideo(Activity activity)
    {
        UnityAds.initialize(activity, unityAdsGameId, unityAdsTestMode, new IUnityAdsInitializationListener() {
            @Override
            public void onInitializationComplete() {
                UnityAds.load(unityAdRewarded, new IUnityAdsLoadListener() {
                    @Override
                    public void onUnityAdsAdLoaded(String s) {
                        UnityAds.show(activity, unityAdRewarded, new IUnityAdsShowListener() {
                            @Override
                            public void onUnityAdsShowFailure(String s, UnityAds.UnityAdsShowError unityAdsShowError, String s1) {

                            }

                            @Override
                            public void onUnityAdsShowStart(String s) {
                            }

                            @Override
                            public void onUnityAdsShowClick(String s) {

                            }

                            @Override
                            public void onUnityAdsShowComplete(String s, UnityAds.UnityAdsShowCompletionState unityAdsShowCompletionState) {

                            }
                        });
                    }

                    @Override
                    public void onUnityAdsFailedToLoad(String s, UnityAds.UnityAdsLoadError unityAdsLoadError, String s1) {

                    }
                });
            }

            @Override
            public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {

            }
        });
    }
    static void ShowClosableAd(Activity activity)
    {

        UnityAds.initialize(activity, unityAdsGameId, unityAdsTestMode, new IUnityAdsInitializationListener() {
            @Override
            public void onInitializationComplete() {
                UnityAds.load(unityAdInterstitial, new IUnityAdsLoadListener() {
                    @Override
                    public void onUnityAdsAdLoaded(String s) {
                        UnityAds.show(activity, unityAdInterstitial, new IUnityAdsShowListener() {
                            @Override
                            public void onUnityAdsShowFailure(String s, UnityAds.UnityAdsShowError unityAdsShowError, String s1) {

                            }

                            @Override
                            public void onUnityAdsShowStart(String s) {
                            }

                            @Override
                            public void onUnityAdsShowClick(String s) {

                            }

                            @Override
                            public void onUnityAdsShowComplete(String s, UnityAds.UnityAdsShowCompletionState unityAdsShowCompletionState) {

                            }
                        });
                    }

                    @Override
                    public void onUnityAdsFailedToLoad(String s, UnityAds.UnityAdsLoadError unityAdsLoadError, String s1) {

                    }
                });
            }

            @Override
            public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {

            }
        });
    }
    static void SetBanner(Activity activity, ViewGroup viewGroup)
    {
//        UnityAds.setDebugMode(true);
        UnityAds.initialize(activity, unityAdsGameId, unityAdsTestMode, new IUnityAdsInitializationListener() {
            @Override
            public void onInitializationComplete() {

                UnityBannerSize unityBannerSize = new UnityBannerSize(320, 50);
                BannerView bannerView = new BannerView(activity, unityAdBanner, unityBannerSize);
                bannerView.setListener(new BannerView.IListener() {
                    public void onBannerLoaded(BannerView bannerAdView) {
                        Toast.makeText(activity, "Banner loaded", Toast.LENGTH_SHORT).show();
                    }
                    public void onBannerClick(BannerView bannerAdView) {
                    }
                    public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo bannerErrorInfo) {
                        Toast.makeText(activity, "Banner failed to load " + bannerErrorInfo.errorMessage, Toast.LENGTH_LONG).show();
                    }
                    public void onBannerLeftApplication(BannerView bannerView) {
                        Toast.makeText(activity, "Banner left application", Toast.LENGTH_SHORT).show();
                    }
                });
                bannerView.load();
//                UnityBanners.loadBanner(activity, unityAdBanner);
//                BannerView bottomBanner = new BannerView(activity, unityAdBanner, new UnityBannerSize(320, 50));
//                bottomBanner.setListener(new BannerView.Listener() {
//                    @Override
//                    public void onBannerLoaded(BannerView bannerAdView) {
//                        super.onBannerLoaded(bannerAdView);
//                    }
//                });
//                bottomBanner.load();
//                viewGroup.removeAllViews();
//                viewGroup.addView(bottomBanner);
            }

            @Override
            public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {

            }
        });
    }
}
