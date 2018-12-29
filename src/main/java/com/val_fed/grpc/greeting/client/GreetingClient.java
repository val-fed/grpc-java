package com.val_fed.grpc.greeting.client;

import com.proto.greet.GreetManyTimesRequest;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
  public static void main(String[] args) {
    System.out.println("Hello! I'm gRPC client");

    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext()
        .build();

    System.out.println("Creating stub");

    // create a greet service client (blocking - synchronous)
    GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

// Unary
    /*
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
    */

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
}
