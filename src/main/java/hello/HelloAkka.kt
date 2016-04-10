package hello

import akka.actor.*
import akka.dispatch.OnComplete
import akka.pattern.Patterns.ask;
import scala.concurrent.Await
import scala.concurrent.Future;
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    val system = ActorSystem.create("MySystem")
    val myActor = system.actorOf(Props.create(MyActor::class.java), "myActor")
    val testActor = system.actorOf(Props.create(TestActor::class.java), "testActor")

    myActor.tell("test", testActor);
    testActor.tell("ask", myActor);

}

class MyActor : UntypedActor() {

    data class Greeting(val from: String)

    object Goodbye

    override fun onReceive(message: Any?) = when (message) {
        "test" -> println("received test")
        is Greeting -> {
            println("I was greeted by " + message.from)
            sender.tell("hello " + message.from, self)
        }
        is Goodbye -> println("Someone said goodbye to me.")
        else -> println("received unknown message")
    }
}

class TestActor : UntypedActor() {
    override fun onReceive(message: Any?) {
        when (message) {
            "ask" -> {
                println("received ask")
                val future: Future<Any> = ask(sender, MyActor.Greeting("Saeed"), 5000)

//                future.onComplete(object : OnComplete<Object>() {
//                    override fun onComplete(failure: Throwable?, success: Object?) {
//                        throw UnsupportedOperationException()
//                    }
//                }, context.dispatcher())
                Await.result(future, Duration.create(6, TimeUnit.SECONDS));
            }
            else -> println("received unknown message")
        }
    }
}

//class F : OnComplete<Object>() {
//    override fun onComplete(failure: Throwable?, success: Object?) {
//        throw UnsupportedOperationException()
//    }
//
//}
