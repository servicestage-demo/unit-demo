package com.huaweicloud.demo.handler;

import com.huaweicloud.demo.util.RsaUtil;

import com.google.protobuf.ByteString;

import io.envoyproxy.envoy.service.ext_proc.v3.BodyMutation;
import io.envoyproxy.envoy.service.ext_proc.v3.BodyResponse;
import io.envoyproxy.envoy.service.ext_proc.v3.CommonResponse;
import io.envoyproxy.envoy.service.ext_proc.v3.ProcessingRequest;
import io.envoyproxy.envoy.service.ext_proc.v3.ProcessingResponse;
import io.grpc.stub.StreamObserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestBodyHandler implements Handler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestBodyHandler.class);

    private final RsaUtil rsaUtil;

    public RequestBodyHandler(RsaUtil rsaUtil) {
        this.rsaUtil = rsaUtil;
    }

    @Override
    public void handle(StreamObserver<ProcessingResponse> responseObserver, ProcessingRequest processingRequest) {
        // 获取请求体
        String requestBody = processingRequest.getRequestBody().getBody().toStringUtf8();
        LOGGER.info("requestBody is: {}.", requestBody);

        // 处理请求体
        ByteString string = ByteString.copyFromUtf8(rsaUtil.decrypt(requestBody));
        LOGGER.info("replace requestBody is: {}.", string.toStringUtf8());

        // 存入处理后的请求体，返回给后端服务
        BodyMutation.Builder builderForValue1 = BodyMutation.newBuilder();
        builderForValue1.setBody(string);

        // 组装返回信息
        CommonResponse.Builder builderForValue = CommonResponse.newBuilder();
        builderForValue.setBodyMutation(builderForValue1);
        BodyResponse.Builder request = BodyResponse.newBuilder();
        request.setResponse(builderForValue);
        ProcessingResponse.Builder builder = ProcessingResponse.newBuilder();
        builder.setRequestBody(request);
        responseObserver.onNext(builder.build());
    }
}
