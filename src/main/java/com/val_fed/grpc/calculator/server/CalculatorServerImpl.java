package com.val_fed.grpc.calculator.server;

import com.proto.calculator.PrimeNumberDecompositionRequest;
import com.proto.calculator.PrimeNumberDecompositionResponse;
import com.proto.calculator.Sum;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import com.proto.calculator.SumServiceGrpc;
import io.grpc.stub.StreamObserver;

public class CalculatorServerImpl extends SumServiceGrpc.SumServiceImplBase {
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
}
