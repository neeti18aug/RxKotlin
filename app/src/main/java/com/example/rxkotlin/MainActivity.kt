package com.example.rxkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //just
        Observable.just("Hello RxKotlin").subscribe{ value -> println(value) }

        Observable.just("Test1", "Test2", "Test3", "Test4")
                .subscribe(
                          { value -> println("Received value: $value") },
                          { error -> println("Error: $error") },
                          { println("completed") }
                )

        Observable.just("1","2","3")
                .map({input-> throw RuntimeException()})
                .subscribe({value -> println("Received value: $value")},
                { error -> println("Error: $error") },
                { println("completed") }
                )

        //create
        getObservableFromList(listOf("Android", "", "Kotlin"))
                .subscribe(
                        { success -> println("Received: $success") },
                        { error -> println("Error: $error") }
                        )
        //interval
        Observable.intervalRange(
                10L,
                5L,
                0L,
                1L,
                TimeUnit.SECONDS
        ).subscribe { println("Result we just received: $it") }

        //repeat
        Observable.just("I", "Me", "You")
                .repeat(5)
                .subscribe{ value -> println("print: $value") }

        //fromArray
        Observable.fromArray("Computer", "Mobile", "Tablet")
                .subscribe{ println(it) }

        //fromIterable
        Observable.fromIterable(listOf("Google", "Microsoft", "Robosoft"))
                .subscribe(
                        { println(it) }, //onNext
                        { error -> println(error) }, //onError
                        { println("Complete") } //onComplete
                )
        //map
        Observable.fromArray("Computer", "Mobile", "Tablet")
                .map { it to it.length }
                .subscribe{ pair ->
                    val (name, length) = pair
                    println("$name - $length")
                }

        //flatMap
        Observable.just("Apple", "Google", "Robosoft")
                .flatMap { s -> getS0(s) }
                .subscribe{ println(it) }

        //filter
        Observable.fromArray("Apple", "Google", "Robosoft", "IBM", "Samsung")
                .filter{ value -> value == "Google" }
                .subscribe{ value -> println(value) }

        //concat
        Observable.concat(cities, bb)
                .subscribe{ t -> println("$t") }

        //merge
        Observable.merge(cities, bb)
                .subscribe{ t -> println("$t") }

        //take
        Observable.just(1,2,3)
                .take(2)
                .subscribe{println("take: $it")}

        //debounce - It only issues an item from an Observable if a certain time has elapsed without issuing another item
         Observable.create <Int> {emitter ->
            emitter.onNext (1)
            Thread.sleep (400) // 400 <500, discards the 1
            emitter.onNext (2)
            Thread.sleep (600) // 600> 500, issues the 2
            emitter.onNext (3)
            Thread.sleep (100) // 100 <500, discards the 3
            emitter.onNext (4)
            Thread.sleep (600) // 600> 500, emits the 4
            emitter.onNext (5)
            Thread.sleep (600) // 600> 500, issues the 5
            emitter.onComplete ()
        }
                .debounce (500, TimeUnit.MILLISECONDS)
                .subscribe {i -> println (i.toString ())}

        //distinct
        Observable.just(1, 2, 2, 1, 3)
                .distinct()
                .subscribe{x -> println("item:  $x")}

        //disposable
        Observable.just("One", "Two", "Three")
                .subscribe(
                        { v -> println("Received disposable: $v") }
                ).dispose()

    }

    //merge and concat
    fun getRandomDelay () = (Math.random () * 3) .toLong () * 1000L

    val cities = Observable.create <String> {emitter ->
        listOf ( "Tokyo", "Rio", "Berlin", "Denver", "Moscow", "Nairobi", "Helsinki", "Oslo"
        ). forEach {
            Thread.sleep (getRandomDelay ())
            emitter.onNext (it)
        }
        emitter.onComplete()
    } .subscribeOn (Schedulers.newThread ())

    val bb = Observable.create <String> {emitter ->
        listOf ("Walt" , "Jesse", "Skyler", "Saul", "Hank")
                .forEach {
                    Thread.sleep(getRandomDelay())
                    emitter.onNext(it)
                }
        emitter.onComplete()
    } .subscribeOn (Schedulers.newThread ())

    //flatMap
    fun getS0(company: String) =
        Observable.create <String> { emitter ->
            emitter.onNext(
                    when(company){
                        "Apple" -> "iOS"
                        "Google" -> "Android"
                        "Microsoft" -> "Windows"
                        else -> "Unknown"
                    }
            )
            emitter.onComplete()
        }

    //convert list to observable
    fun getObservableFromList(myList: List<String>) =
        Observable.create<String> { emitter ->
            myList.forEach { kind ->
                if (kind == "") {
                    emitter.onError(Exception("There is no value"))
                }
                emitter.onNext(kind)
            }
            emitter.onComplete()
        }


}