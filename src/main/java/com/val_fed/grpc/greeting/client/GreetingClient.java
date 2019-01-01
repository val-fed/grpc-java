package com.val_fed.grpc.greeting.client;

import com.proto.greet.GreetManyTimesRequest;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import com.proto.greet.LongGreetRequest;
import com.proto.greet.LongGreetResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

  private ManagedChannel channel;

  private void run() {
    channel = ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext()
        .build();

//   doUnaryCall(channel);
//   doServerStreamingCall(channel);
    doClientStreamingCall(channel);
  }

  private void doClientStreamingCall(ManagedChannel channel) {
    // create asynchronous client
    GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);
    CountDownLatch latch = new CountDownLatch(1);
    StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
      @Override
      public void onNext(LongGreetResponse value) {
        // we get a response from the server
        System.out.println("Receiver a response from the server");
        System.out.println(value.getResult());
        // onNext will be called only once
      }

      @Override
      public void onError(Throwable t) {
        // we get an error from the server
      }

      @Override
      public void onCompleted() {
        // the server is done sending us data
        // onCompleted will be called right after onNext()
        System.out.println("Receiver a response from the server");
        latch.countDown();
      }
    });
    // streaming message #1
    System.out.println("Sending message #1 ...");
    requestObserver.onNext(LongGreetRequest.newBuilder()
        .setGreeting(Greeting.newBuilder()
            .setFirstName("AAA")
            .build())
        .build());

    // streaming message #2
    System.out.println("Sending message #2 ...");
    requestObserver.onNext(LongGreetRequest.newBuilder()
        .setGreeting(Greeting.newBuilder()
            .setFirstName("BBB")
            .build())
        .build());

    // streaming message #3
    System.out.println("Sending message #3 ...");
    requestObserver.onNext(LongGreetRequest.newBuilder()
        .setGreeting(Greeting.newBuilder()
            .setFirstName("CCC")
            .build())
        .build());

    // we tell the server that the client is done sending data
    requestObserver.onCompleted();

    try {
      latch.await(3L, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void doServerStreamingCall(ManagedChannel channel) {
    // create a greet service client (blocking - synchronous)
    GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

    // Server Streaming
    GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
        .setGreeting(Greeting.newBuilder().setFirstName("Valerii").build())
        .build();

    // stream the responses (in a blocking manner)
    greetClient.greetManyTimes(greetManyTimesRequest)
        .forEachRemaining(greetManyTimesResponse -> System.out.println(greetManyTimesResponse.getResult()));

    System.out.println("Shutting down channel");
    channel.shutdown();
  }

  private void doUnaryCall(ManagedChannel channel) {
    // create a greet service client (blocking - synchronous)
    GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

    Greeting greeting = Greeting.newBuilder()
        .setFirstName("Val")
        .setLastName("Fed")
        .build();

    GreetRequest greetRequest = GreetRequest.newBuilder()
        .setGreeting(greeting)
        .build();

    // call the RPC and get back a GreetResponse
    GreetResponse greetResponse = greetClient.greet(greetRequest);

    System.out.println(greetResponse.getResult());

    System.out.println("Shutting down channel");
    channel.shutdown();
  }

  public static void main(String[] args) {
    System.out.println("Hello! I'm gRPC client");

    GreetingClient main = new GreetingClient();
    main.run();
  }
}
