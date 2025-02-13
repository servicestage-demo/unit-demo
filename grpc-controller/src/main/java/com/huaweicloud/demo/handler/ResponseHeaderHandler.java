package com.huaweicloud.demo.handler;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;

import io.envoyproxy.envoy.config.core.v3.HeaderValue;
import io.envoyproxy.envoy.config.core.v3.HeaderValueOption;
import io.envoyproxy.envoy.service.ext_proc.v3.CommonResponse;
import io.envoyproxy.envoy.service.ext_proc.v3.HeaderMutation;
import io.envoyproxy.envoy.service.ext_proc.v3.HeadersResponse;
import io.envoyproxy.envoy.service.ext_proc.v3.ProcessingRequest;
import io.envoyproxy.envoy.service.ext_proc.v3.ProcessingResponse;
import io.grpc.stub.StreamObserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseHeaderHandler implements Handler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHeaderHandler.class);

    @Override
    public void handle(StreamObserver<ProcessingResponse> responseObserver, ProcessingRequest processingRequest) {
        // 获取响应头
        for (HeaderValue headerValue : processingRequest.getResponseHeaders().getHeaders().getHeadersList()) {
            LOGGER.info("responseHeader is {}:{}.", headerValue.getKey(), headerValue.getRawValue().toStringUtf8());
        }
        HeaderMutation.Builder builderForValue1 = HeaderMutation.newBuilder();

        // 添加一个响应头，如果响应中存在该响应头，则在原响应头中，再添加一个
        HeaderValue.Builder builderForValue = HeaderValue.newBuilder();
        builderForValue.setKey("add-resp-key");
        builderForValue.setRawValue(ByteString.copyFromUtf8("add-resp-value"));
        HeaderValueOption.Builder valueBuilder = HeaderValueOption.newBuilder();
        valueBuilder.setHeader(builderForValue);

        // 设置为添加响应头（如不设置则为覆盖）
        valueBuilder.setAppend(BoolValue.newBuilder().setValue(true));
        builderForValue1.addSetHeaders(valueBuilder);

        // 添加一个响应头，如果响应中存在该响应头，则覆盖原响应头
        HeaderValue.Builder builderForValue2 = HeaderValue.newBuilder();
        builderForValue2.setKey("content-type");
        builderForValue2.setRawValue(ByteString.copyFromUtf8("text/plain"));
        HeaderValueOption.Builder valueBuilder1 = HeaderValueOption.newBuilder();
        valueBuilder1.setHeader(builderForValue2);
        builderForValue1.addSetHeaders(valueBuilder1);

        // 删除一个响应头
        builderForValue1.addRemoveHeaders("remove-resp-key");

        // 组装返回信息
        CommonResponse.Builder builderForValue3 = CommonResponse.newBuilder();
        builderForValue3.setHeaderMutation(builderForValue1);
        HeadersResponse.Builder builderForValue4 = HeadersResponse.newBuilder();
        builderForValue4.setResponse(builderForValue3);
        ProcessingResponse.Builder builder = ProcessingResponse.newBuilder();
        builder.setResponseHeaders(builderForValue4);
        responseObserver.onNext(builder.build());
    }
}
