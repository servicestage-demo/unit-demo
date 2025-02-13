package com.huaweicloud.demo.handler;

import io.envoyproxy.envoy.service.ext_proc.v3.ProcessingRequest;
import io.envoyproxy.envoy.service.ext_proc.v3.ProcessingResponse;
import io.grpc.stub.StreamObserver;

public interface Handler {
    void handle(StreamObserver<ProcessingResponse> responseObserver, ProcessingRequest processingRequest);
}
