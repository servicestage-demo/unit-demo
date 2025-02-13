package com.huaweicloud.demo.service;

import com.huaweicloud.demo.handler.DefaultHandler;
import com.huaweicloud.demo.handler.Handler;
import com.huaweicloud.demo.handler.RequestBodyHandler;
import com.huaweicloud.demo.handler.RequestHeaderHandler;
import com.huaweicloud.demo.handler.ResponseBodyHandler;
import com.huaweicloud.demo.handler.ResponseHeaderHandler;
import com.huaweicloud.demo.util.RsaUtil;

import io.envoyproxy.envoy.service.ext_proc.v3.ExternalProcessorGrpc;
import io.envoyproxy.envoy.service.ext_proc.v3.ProcessingRequest;
import io.envoyproxy.envoy.service.ext_proc.v3.ProcessingResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import net.devh.boot.grpc.server.service.GrpcService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

@GrpcService
public class GrpcServerService extends ExternalProcessorGrpc.ExternalProcessorImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServerService.class);

    private static final Map<ProcessingRequest.RequestCase, Handler> HANDLER = new HashMap<>();

    private static final DefaultHandler DEFAULT_HANDLER = new DefaultHandler();

    @Autowired
    private RsaUtil rsaUtil;

    @PostConstruct
    public void init() {
        HANDLER.put(ProcessingRequest.RequestCase.REQUEST_BODY, new RequestBodyHandler(rsaUtil));
        HANDLER.put(ProcessingRequest.RequestCase.RESPONSE_BODY, new ResponseBodyHandler(rsaUtil));
        HANDLER.put(ProcessingRequest.RequestCase.REQUEST_HEADERS, new RequestHeaderHandler());
        HANDLER.put(ProcessingRequest.RequestCase.RESPONSE_HEADERS, new ResponseHeaderHandler());
    }

    @Override
    public StreamObserver<ProcessingRequest> process(StreamObserver<ProcessingResponse> responseObserver) {
        return new StreamObserver<ProcessingRequest>() {
            @Override
            public void onNext(ProcessingRequest processingRequest) {
                // 获取本次请求类型
                ProcessingRequest.RequestCase requestCase = processingRequest.getRequestCase();
                LOGGER.info("requestCase is {}.", requestCase);
                HANDLER.getOrDefault(requestCase, DEFAULT_HANDLER).handle(responseObserver, processingRequest);
            }

            @Override
            public void onError(Throwable throwable) {
                if (throwable instanceof StatusRuntimeException) {
                    StatusRuntimeException exception = (StatusRuntimeException) throwable;
                    if (exception.getStatus().getCode() == Status.CANCELLED.getCode()) {
                        // ignore
                        return;
                    }
                }
                LOGGER.error("-------------process error-----------", throwable);
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                LOGGER.info("!!!!!!!!!!!!onCompleted!!!!!!!!!!!!!");
                responseObserver.onCompleted();
            }
        };
    }
}
