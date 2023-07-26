package com.example.jetsparkle.multithreading




val thread2 = Thread(Runnable {
    for (i in 1..1000) {
        println("Thread 2 $i")
    }
})


fun main(){
//    thread.start()
//    thread.join()
//    thread2.start()
//    println("Completed")


    for (i in 1..10000){
        val thread = Thread(Runnable {
            for (i in 1..1000) {
                println("Thread 1 $i")
            }
        })
        thread.start()
    }
}