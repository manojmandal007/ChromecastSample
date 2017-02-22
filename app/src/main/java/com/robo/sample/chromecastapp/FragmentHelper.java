

package com.robo.sample.chromecastapp;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FragmentHelper {
    /**
     * Replaces fragment without adding it to the back stack .
     */
    public static void replaceFragment(FragmentActivity activity, Fragment fragment,
                                       int containerId) {
        if (!activity.isFinishing()) {
            FragmentManager manager = activity.getSupportFragmentManager();
            manager.beginTransaction().replace(containerId, fragment).commitAllowingStateLoss();
        }
    }

    /**
     * Replaces fragment without adding it to the back stack .
     */
    public static void replaceFragment(FragmentActivity activity, Fragment fragment,
                                       int containerId, String tag) {
        if (!activity.isFinishing()) {
            FragmentManager manager = activity.getSupportFragmentManager();
            manager.beginTransaction().replace(containerId, fragment, tag).commitAllowingStateLoss();
        }
    }


    /**
     * Replaces and adds the fragment to the back stack.
     */
    public static void replaceAndAddFragment(FragmentActivity activity, Fragment fragment, int containerId) {
        if (activity != null && !activity.isFinishing()) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * Replaces and adds the fragment to the back stack.
     */
    public static void addFragment(FragmentActivity activity, Fragment fragment, int containerId) {
        if (activity != null && !activity.isFinishing()) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.add(containerId, fragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
    }

    public static void replaceAndAddFragment(FragmentActivity activity, Fragment fragment, int containerId, String tag) {
        if (activity != null && !activity.isFinishing()) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, tag);
            transaction.addToBackStack(tag);
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * This removes current fragment and adds a new child fragment.
     */
    public static void replaceChildFragment(Fragment parentFragment, int containerId, Fragment childFragment) {
        if (parentFragment != null && !parentFragment.isDetached())
            parentFragment.getChildFragmentManager().beginTransaction().replace(containerId, childFragment).commitAllowingStateLoss();
    }

    /**
     * This adds current fragment to backstack and child fragment is added to
     * container
     */

    public static void replaceAndAddChildFragment(Fragment parentFragment, final int containerId, Fragment childFragment) {
        if (parentFragment != null && !parentFragment.isDetached()) {
            FragmentManager fragmentManager = parentFragment.getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(containerId, childFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    public static Fragment getCurrentFragment(FragmentActivity activity, int containerId) {
        if (activity != null)
            return activity.getSupportFragmentManager().findFragmentById(containerId);
        return null;
    }


    public static void popBackStackImmediate(FragmentActivity activity) {
        activity.getSupportFragmentManager().popBackStackImmediate();
    }

    public static void showDialogFragment(FragmentManager fragmentManager, DialogFragment dialogFragment, String tag) {
        if (fragmentManager.findFragmentByTag(tag) == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(dialogFragment, tag);
            transaction.commitAllowingStateLoss();
        }
    }
}
