package com.baha.mediasharingapp

sealed class Screen(val route: String, val title: String) {
    object Signup: Screen("signup", "Welcome")
    object Feed:   Screen("feed",   "Feed")
    object Map:    Screen("map",    "Map")
}