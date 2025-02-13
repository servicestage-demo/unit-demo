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

public class ResponseBodyHandler implements Handler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseBodyHandler.class);

    private final RsaUtil rsaUtil;

    public ResponseBodyHandler(RsaUtil rsaUtil) {
        this.rsaUtil = rsaUtil;
    }

    @Override
    public void handle(StreamObserver<ProcessingResponse> responseObserver, ProcessingRequest processingRequest) {
        // 获取响应体
        String responseBody = processingRequest.getResponseBody().getBody().toStringUtf8();
        LOGGER.info("responseBody is {}.", responseBody);

        // 处理响应体
        ByteString string = ByteString.copyFromUtf8(rsaUtil.encrypt(responseBody));
        LOGGER.info("replace responseBody is: {}.", string.toStringUtf8());

        // 存入处理后的响应体，返回给客户端
        BodyMutation.Builder builderForValue1 = BodyMutation.newBuilder();
        builderForValue1.setBody(string);

        // 组装返回信息
        CommonResponse.Builder builderForValue = CommonResponse.newBuilder();
        builderForValue.setBodyMutation(builderForValue1);
        BodyResponse.Builder request = BodyResponse.newBuilder();
        request.setResponse(builderForValue);
        ProcessingResponse.Builder builder = ProcessingResponse.newBuilder();
        builder.setResponseBody(request);
        responseObserver.onNext(builder.build());

        // 仅类型为响应体才需要调用onCompleted方法
        responseObserver.onCompleted();
    }
}
