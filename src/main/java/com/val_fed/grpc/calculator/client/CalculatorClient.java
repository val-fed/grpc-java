package com.val_fed.grpc.calculator.client;

import com.proto.calculator.PrimeNumberDecompositionRequest;
import com.proto.calculator.SumServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {
  public static void main(String[] args) {
    System.out.println("Hello! I'm gRPC client");

    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext()
        .build();

    System.out.println("Creating stub");

    SumServiceGrpc.SumServiceBlockingStub sumClient = SumServiceGrpc.newBlockingStub(channel);

    // Unary
//    Sum calculator = Sum.newBuilder()
//        .setFirstArg(1)
//        .setSecondArg(2)
//        .build();
//
//    SumRequest sumRequest = SumRequest.newBuilder()
//        .setSum(calculator)
//        .build();
//
//    SumResponse sumResponse = sumClient.calculator(sumRequest);
//
//    System.out.println("first arg: " + calculator.getFirstArg());
//    System.out.println("second arg: " + calculator.getSecondArg());
//    System.out.println("result: " + sumResponse.getResult());

    // Server Streaming
    PrimeNumberDecompositionRequest request = PrimeNumberDecompositionRequest.newBuilder()
        .setNumber(120)
        .build();

    // stream the responses (in a blocking manner)
    sumClient
        .primeNumberDecomposition(request)
        .forEachRemaining(primeNumberDecompositionResponse -> System.out.println(primeNumberDecompositionResponse.getPrimeFactor()));

    System.out.println("Shutting down channel");
    channel.shutdown();
  }
}
