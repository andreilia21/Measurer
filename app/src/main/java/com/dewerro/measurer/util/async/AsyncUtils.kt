package com.dewerro.measurer.util.async

import java.util.*

private fun timerTask(runAction: () -> Unit): TimerTask {
    return object : TimerTask(){
        override fun run() {
            runAction()
        }
    }
}

/**
 * Используется для запуска какой-либо задачи через заданное время
 * @param delay время, через которое должна выполниться задача
 * @param task задача
 */
fun scheduleTask(delay: Long, task: () -> Unit) {
    Timer().schedule(timerTask(task), delay)
}