package com.codeleg.dailyscope.utils

import android.app.Activity
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle


    fun Activity.showSuccessToast(activity: Activity, message: String) {
        MotionToast.darkToast(
            activity,
            "Success",
            message,
            MotionToastStyle.SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            null
        )
    }
    fun Activity.showErrorToast(activity: Activity, message: String) {
        MotionToast.darkToast(
            activity,
            "Error",
            message,
            MotionToastStyle.ERROR,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            null
        )
    }

    fun Activity.showWarningToast(activity: Activity, message: String) {
        MotionToast.darkToast(
            activity,
            "Warning",
            message,
            MotionToastStyle.WARNING,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            null
        )

        fun Activity.showDeletedToast(activity: Activity, message: String) {
            MotionToast.darkToast(
                activity,
                "Deleted",
                message,
                MotionToastStyle.DELETE,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                null
            )
        }
    }
