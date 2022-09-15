package com.example.racertimer.sailingToolsFragment

import org.junit.Test
//TODO: разобраться с особенностями тестирования VM
class ViewModelTest {
    var velocity = 0
    var maxVelocity = 0
    var vmg = 0
    var maxUpwind = 0
    var maxDownwind = 0
    var windDir = 10000

    val viewModel = ViewModel()
    //viewModel.onWindChanged(180)

    fun checkoutVMFields() {
        velocity = viewModel.speedLive.value as Int
        maxVelocity = viewModel.maxSpeedLive.value as Int
        vmg = viewModel.VMGLive.value as Int
        maxUpwind = viewModel.maxUpwindLive.value as Int
        maxDownwind = viewModel.maxDownwindLive.value as Int
    }

    @Test
    fun maxVelocity() {
        viewModel.onLocationChanged(10, 90)
        viewModel.onLocationChanged(30, 90)
        viewModel.onLocationChanged(10, 90)
        checkoutVMFields()
        assert((maxVelocity == 30))
    }
}