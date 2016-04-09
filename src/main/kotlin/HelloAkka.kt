package hello

import akka.actor.*
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun getHelloString(): String {
    return "Hello, world!"
}

fun main(args: Array<String>) {
    val system = ActorSystem.create("MySystem")
    val myActor = system.actorOf(Props.create(MyActor::class.java),"myactor")

    val inbox = Inbox.create(system);
    inbox.send(myActor, "test");
    try {
        println(inbox.receive(Duration.create(1, TimeUnit.SECONDS)));
    } catch (e: TimeoutException) {
        println("timeout")
    }


}

class MyActor : UntypedActor() {

    override fun onReceive(message: Any?) {
        when (message) {
            "test" -> print("received test")
            else -> print("received unknown message")
        }
    }

}
