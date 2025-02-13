# 报文转换服务开发指南

## 模块介绍

- [grpc-controller](./grpc-controller)为报文转换服务，使用grpc协议进行开发。
- [ex-processor-provider](./unit-provider)为常见的spring-cloud服务，使用http协议进行开发，这里将作为网关的后端服务。

## 注意

- 本demo给出的加密方法仅作示例，不可在生产环境中使用。

- grpc-controller为grpc服务，无法被传统的spring-boot微服务（http服务）调用及发现，实际使用时，应作为单独的服务开发。

- grpc-controller使用的[spring-cloud-huawei](https://github.com/huaweicloud/spring-cloud-huawei)框架仅作注册使用，不提供grpc服务的功能，grpc服务的功能由[grpc-spring-boot-starter](https://github.com/grpc-ecosystem/grpc-spring)及[envoyproxy-controlplane](https://github.com/envoyproxy/java-control-plane)提供。

## 使用 Dependency Management 管理依赖

为了减少依赖冲突，建议在服务的pom.xml使用Dependency Management来管理依赖项，具体如下：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring.cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>com.huaweicloud</groupId>
            <artifactId>spring-cloud-huawei-bom</artifactId>
            <version>${spring.cloud.huawei.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>net.devh</groupId>
            <artifactId>grpc-server-spring-boot-starter</artifactId>
            <version>${grpc.starter.version}</version>
        </dependency>
        <dependency>
            <groupId>io.envoyproxy.controlplane</groupId>
            <artifactId>api</artifactId>
            <version>${envoyproxy.controlplane.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

具体版本：

```xml
<properties>
    <spring.cloud.version>2021.0.8</spring.cloud.version>
    <spring.cloud.huawei.version>1.11.10-2021.0.x</spring.cloud.huawei.version>
    <envoyproxy.controlplane.version>1.0.39</envoyproxy.controlplane.version>
    <grpc.starter.version>2.15.0.RELEASE</grpc.starter.version>
</properties>
```

展示的具体版本为最低的版本要求，具体版本请做如下参考：
- spring-cloud-huawei请参考：https://github.com/huaweicloud/spring-cloud-huawei
- grpc-spring-boot-starter请参考：https://github.com/grpc-ecosystem/grpc-spring
- envoyproxy-controlplane请参考：https://github.com/envoyproxy/java-control-plane

## bootstrap.yml配置

bootstrap.yml指定了应用程序启动必须的信息，这些信息通常无法通过配置中心下发，只能使用环境变量和配置文件
进行配置。 bootstrap.yml的信息包括：

* 微服务的基本信息：应用名称，微服务名称，微服务版本，环境，其他属性。
* 微服务实例的基本信息：初始状态，其他属性。
* 注册中心信息：注册中心地址及其参数。
* 配置中心信息: 配置中心地址及其参数， 包括配置中心类型等。

```yaml
grpc:
  server:
    # grpc服务的端口
    port: 9898

spring:
  application:
    # 微服务名称，本示例使用固定值(可替换成自己想要的名字)，因为微服务名称会被客户端使用，不能轻易变化。
    name: grpc-controller
  cloud:
    servicecomb:
      discovery:
        # 应用名称
        appName: grpc-controller
        # 微服务名称，和spring.application.name保持一致。
        serviceName: ${spring.application.name}
        # 注册中心地址，本示例使用ServiceStage环境变量。建议保留这种配置方式，部署的时候，不用手工修改地址。
        address: ${PAAS_CSE_SC_ENDPOINT:http://127.0.0.1:30100}
        # 微服务版本号，本示例使用ServiceStage环境变量。建议保留这种配置方式，部署的时候，不用手工修改版本号，防止契约注册失败。
        version: ${CAS_INSTANCE_VERSION:0.0.1}
        # 注册的端口，这里需要把grpc服务的端口注册上去
        port: ${grpc.server.port}
      config:
        # 配置中心地址，本示例使用ServiceStage环境变量。建议保留这种配置方式，部署的时候，不用手工修改地址。
        serverAddr: ${PAAS_CSE_CC_ENDPOINT:http://127.0.0.1:30110}
        serverType: kie
```

## 报文转换服务开发指南

报文转换服务需要继承envoyproxy-controlplane的`ExternalProcessorGrpc.ExternalProcessorImplBase`抽象类，并使用`@GrpcService`注解，如下所示：

```java
@GrpcService
public class GrpcServerService extends ExternalProcessorGrpc.ExternalProcessorImplBase {
    @Override
    public StreamObserver<ProcessingRequest> process(StreamObserver<ProcessingResponse> responseObserver) {
        return new StreamObserver<ProcessingRequest>() {
            @Override
            public void onNext(ProcessingRequest processingRequest) {
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onCompleted() {
            }
        };
    }
}
```

### 具体使用方法

1.`onNext`方法为主要业务逻辑，其中`processingRequest`为方法入参，其中包括本次请求类型，请求头，请求体，响应头，响应体等，具体解释如下：

- `processingRequest.getRequestCase()`方法可以获取本次的请求类型，包括REQUEST_BODY/RESPONSE_BODY/REQUEST_HEADERS/RESPONSE_HEADERS等，网关每次只会发送一种请求类型到服务中，如果配置了多种类型，则会请求多次。
- `processingRequest.getRequestBody().getBody().toStringUtf8()`方法可以获取本次的请求体。
- `processingRequest.getRequestHeaders().getHeaders().getHeadersList()`方法可以获取本次的请求头。
- `processingRequest.getResponseBody().getBody().toStringUtf8()`方法可以获取本次的响应体。
- `processingRequest.getResponseHeaders().getHeaders().getHeadersList()`方法可以获取本次的响应头。

2.在`onNext`方法的最后需要调用`responseObserver.onNext(ProcessingResponse.newBuilder().build())`，`ProcessingResponse`为响应参数，需要根据不同的请求类型，返回不同的参数，具体解释如下：

- `ProcessingResponse.newBuilder().setRequestBody`方法可以设置修改后的请求体。
- `ProcessingResponse.newBuilder().setRequestHeaders`方法可以设置修改后的请求头。
- `ProcessingResponse.newBuilder().setResponseBody`方法可以设置修改后的响应体，需要注意的是，在处理完成响应体后，需要调用`responseObserver.onCompleted()`方法。
- `ProcessingResponse.newBuilder().setResponseHeaders`方法可以设置修改后的响应头。

3.在`onError`方法中，可以记录错误日志。

4.在`onCompleted`方法中，也需要调用`responseObserver.onCompleted()`方法。

> **说明**：更多具体的使用方法，请参考[GrpcServerService](./grpc-controller/src/main/java/com/huaweicloud/demo/service/GrpcServerService.java)。