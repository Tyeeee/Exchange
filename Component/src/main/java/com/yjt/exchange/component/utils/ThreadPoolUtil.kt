package com.hynet.heebit.components.utils

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class ThreadPoolUtil {

    companion object {

        private var threadPoolExecutor: ThreadPoolExecutor? = null
        private val workQueue = ArrayBlockingQueue<Runnable>(10)
        
        init {
            val CORE_POOL_SIZE = 5
            val MAX_POOL_SIZE = 100
            val KEEP_ALIVE_TIME = 10000
            threadPoolExecutor = ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME.toLong(), TimeUnit.SECONDS, workQueue, object : ThreadFactory {
                private val count = AtomicInteger()

                override fun newThread(runnable: Runnable): Thread {
                    return Thread(runnable, "threadPool thread:" + count.getAndIncrement())
                }
            })
        }

        fun execute(runnable: Runnable) {
            threadPoolExecutor?.execute(runnable)
        }

    }

}