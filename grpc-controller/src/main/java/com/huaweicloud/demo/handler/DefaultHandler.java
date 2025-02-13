package com.huaweicloud.demo.handler;

import io.envoyproxy.envoy.service.ext_proc.v3.ProcessingRequest;
import io.envoyproxy.envoy.service.ext_proc.v3.ProcessingResponse;
import io.grpc.stub.StreamObserver;

public class DefaultHandler implements Handler {
    @Override
    public void handle(StreamObserver<ProcessingResponse> responseObserver, ProcessingRequest processingRequest) {
        // 不需要处理的类型直接返回
        responseObserver.onNext(ProcessingResponse.newBuilder().build());
    }
}
