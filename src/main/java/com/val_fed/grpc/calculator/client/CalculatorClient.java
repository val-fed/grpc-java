package com.val_fed.grpc.calculator.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.ComputeAverageRequest;
import com.proto.calculator.ComputeAverageResponse;
import com.proto.calculator.PrimeNumberDecompositionRequest;
import com.proto.calculator.Sum;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {
  public static void main(String[] args) {
    System.out.println("Hello! I'm gRPC client");

    new CalculatorClient().run();
  }

  private void run() {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052)
        .usePlaintext()
        .build();

//    doUnaryCall(channel);
//    doServerStreamingCall(channel);
    doClientStreamingCall(channel);

    System.out.println("Shutting down channel");
    channel.shutdown();
  }

  private void doClientStreamingCall(ManagedChannel channel) {
    CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);

    CountDownLatch latch = new CountDownLatch(1);
    StreamObserver<ComputeAverageRequest> requestObserver
        = asyncClient.computeAverage(new StreamObserver<ComputeAverageResponse>() {
      @Override
      public void onNext(ComputeAverageResponse value) {
        System.out.println("Received a response from the server");
        System.out.println(value.getAverage());
      }

      @Override
      public void onError(Throwable t) { }

      @Override
      public void onCompleted() {
        System.out.println("Server has completed sending us data");
        latch.countDown();
      }
    });
    requestObserver.onNext(ComputeAverageRequest.newBuilder()
        .setNumber(1)
        .build());

    requestObserver.onNext(ComputeAverageRequest.newBuilder()
        .setNumber(2)
        .build());

    requestObserver.onNext(ComputeAverageRequest.newBuilder()
        .setNumber(3)
        .build());

    requestObserver.onNext(ComputeAverageRequest.newBuilder()
        .setNumber(4)
        .build());

    requestObserver.onCompleted();
    try {
      latch.await(3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void doServerStreamingCall(ManagedChannel channel) {
    CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);

    PrimeNumberDecompositionRequest request = PrimeNumberDecompositionRequest.newBuilder()
        .setNumber(120)
        .build();

    // stream the responses (in a blocking manner)
    stub
        .primeNumberDecomposition(request)
        .forEachRemaining(primeNumberDecompositionResponse ->
            System.out.println(primeNumberDecompositionResponse.getPrimeFactor()));
  }

  private void doUnaryCall(ManagedChannel channel) {
    CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);

    Sum calculator = Sum.newBuilder()
        .setFirstArg(1)
        .setSecondArg(2)
        .build();

    SumRequest sumRequest = SumRequest.newBuilder()
        .setSum(calculator)
        .build();

    SumResponse sumResponse = stub.sum(sumRequest);

    System.out.println("first arg: " + calculator.getFirstArg());
    System.out.println("second arg: " + calculator.getSecondArg());
    System.out.println("result: " + sumResponse.getResult());
  }
}
