package com.dewerro.measurer.util.async

import java.util.*

private fun timerTask(runAction: () -> Unit): TimerTask {
    return object : TimerTask(){
        override fun run() {
            runAction()
        }
    }
}

fun scheduleTask(delay: Long, task: () -> Unit) {
    Timer().schedule(timerTask(task), delay)
}