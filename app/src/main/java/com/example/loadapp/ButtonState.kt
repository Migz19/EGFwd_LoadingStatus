package com.example.loadapp

sealed class ButtonState {
    object Loading : ButtonState()
    object Completed : ButtonState()
    object Idle : ButtonState()
}
