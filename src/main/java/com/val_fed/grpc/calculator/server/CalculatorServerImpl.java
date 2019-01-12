package com.val_fed.grpc.calculator.server;

import com.proto.calculator.ComputeAverageRequest;
import com.proto.calculator.ComputeAverageResponse;
import com.proto.calculator.PrimeNumberDecompositionRequest;
import com.proto.calculator.PrimeNumberDecompositionResponse;
import com.proto.calculator.Sum;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import com.proto.calculator.CalculatorServiceGrpc;
import io.grpc.stub.StreamObserver;

public class CalculatorServerImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
  @Override
  public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
    Sum sum = request.getSum();
    int firstArg = sum.getFirstArg();
    int secondArg = sum.getSecondArg();

    int result = firstArg + secondArg;
    SumResponse response = SumResponse.newBuilder()
        .setResult(result)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void primeNumberDecomposition(PrimeNumberDecompositionRequest request,
                                       StreamObserver<PrimeNumberDecompositionResponse> responseObserver) {
    int number = request.getNumber();
    int divisor = 2;

    while (number > 1) {
      if(number % divisor == 0) {
        number = number / divisor;
        responseObserver.onNext(PrimeNumberDecompositionResponse.newBuilder()
            .setPrimeFactor(divisor)
            .build());
      } else {
        divisor = divisor + 1;
      }
    }
    responseObserver.onCompleted();
  }

  @Override
  public StreamObserver<ComputeAverageRequest> computeAverage(StreamObserver<ComputeAverageResponse> responseObserver) {

    return new StreamObserver<ComputeAverageRequest>() {
      int sum = 0;
      int count = 0;
      @Override
      public void onNext(ComputeAverageRequest value) {
        sum += value.getNumber();
        count++;
      }

      @Override
      public void onError(Throwable t) {}

      @Override
      public void onCompleted() {
        double average = (double) sum / count;
        responseObserver.onNext(
            ComputeAverageResponse.newBuilder()
                .setAverage(average)
                .build()
        );
        responseObserver.onCompleted();
      }
    };
  }
}
