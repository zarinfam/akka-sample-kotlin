package hello;

import akka.actor.*;
import akka.dispatch.OnComplete;
import static akka.pattern.Patterns.ask;
import scala.concurrent.Future;

class JavaHello {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("MySystem");
        ActorRef myActor = system.actorOf(Props.create(MyActor.class), "myActor");
        ActorRef testActor = system.actorOf(Props.create(TestActor.class), "testActor");

        myActor.tell("test", testActor);
        testActor.tell("ask", myActor);
    }

    public static class MyActor extends UntypedActor {

        public void onReceive(Object message) throws Exception {
            if (message instanceof String){
                System.out.println("received test");
            }else if (message instanceof Greeting){
                Greeting greeting = (Greeting) message;
                System.out.println("I was greeted by " + greeting.from);
                sender().tell("hello " + greeting.from, self());
            }else if (message instanceof Goodbye){
                System.out.println("Someone said goodbye to me.");
            }else {
                System.out.println("received unknown message");
            }
        }

        public static class Greeting {
            public final String from;

            public Greeting(String from) {
                this.from = from;
            }
        }
        public static class Goodbye{}

    }

    public static class TestActor extends UntypedActor {

        public void onReceive(Object message) throws Exception {
            if (message instanceof String){
                String s = (String) message;
                System.out.println("received ask");
                Future<Object> future = ask(sender(), new MyActor.Greeting("Saeed"), 5000);

                future.onComplete(new OnComplete<Object>() {
                    public void onComplete(Throwable failure, Object result) {
                        if (failure != null) {
                            System.out.println("We got a failure, handle it here");
                        } else {
                            System.out.println("result = "+(String) result);
                        }
                    }
                },context().dispatcher());
            }else {
                System.out.println("received unknown message");
            }
        }
    }
}