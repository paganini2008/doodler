package com.github.doodler.common.hazelcast;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Hazelcast 缓存服务接口
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: hazelcast.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class HazelcastServiceGrpc {

  private HazelcastServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.github.doodler.common.hazelcast.grpc.HazelcastService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.MapEntry,
      com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getPutMapMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PutMap",
      requestType = com.github.doodler.common.hazelcast.Hazelcast.MapEntry.class,
      responseType = com.github.doodler.common.hazelcast.Hazelcast.OperationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.MapEntry,
      com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getPutMapMethod() {
    io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.MapEntry, com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getPutMapMethod;
    if ((getPutMapMethod = HazelcastServiceGrpc.getPutMapMethod) == null) {
      synchronized (HazelcastServiceGrpc.class) {
        if ((getPutMapMethod = HazelcastServiceGrpc.getPutMapMethod) == null) {
          HazelcastServiceGrpc.getPutMapMethod = getPutMapMethod =
              io.grpc.MethodDescriptor.<com.github.doodler.common.hazelcast.Hazelcast.MapEntry, com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "PutMap"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.MapEntry.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.OperationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new HazelcastServiceMethodDescriptorSupplier("PutMap"))
              .build();
        }
      }
    }
    return getPutMapMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.KeyRequest,
      com.github.doodler.common.hazelcast.Hazelcast.ValueResponse> getGetMapMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetMap",
      requestType = com.github.doodler.common.hazelcast.Hazelcast.KeyRequest.class,
      responseType = com.github.doodler.common.hazelcast.Hazelcast.ValueResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.KeyRequest,
      com.github.doodler.common.hazelcast.Hazelcast.ValueResponse> getGetMapMethod() {
    io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.KeyRequest, com.github.doodler.common.hazelcast.Hazelcast.ValueResponse> getGetMapMethod;
    if ((getGetMapMethod = HazelcastServiceGrpc.getGetMapMethod) == null) {
      synchronized (HazelcastServiceGrpc.class) {
        if ((getGetMapMethod = HazelcastServiceGrpc.getGetMapMethod) == null) {
          HazelcastServiceGrpc.getGetMapMethod = getGetMapMethod =
              io.grpc.MethodDescriptor.<com.github.doodler.common.hazelcast.Hazelcast.KeyRequest, com.github.doodler.common.hazelcast.Hazelcast.ValueResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetMap"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.KeyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.ValueResponse.getDefaultInstance()))
              .setSchemaDescriptor(new HazelcastServiceMethodDescriptorSupplier("GetMap"))
              .build();
        }
      }
    }
    return getGetMapMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.KeyRequest,
      com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getRemoveMapMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RemoveMap",
      requestType = com.github.doodler.common.hazelcast.Hazelcast.KeyRequest.class,
      responseType = com.github.doodler.common.hazelcast.Hazelcast.OperationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.KeyRequest,
      com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getRemoveMapMethod() {
    io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.KeyRequest, com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getRemoveMapMethod;
    if ((getRemoveMapMethod = HazelcastServiceGrpc.getRemoveMapMethod) == null) {
      synchronized (HazelcastServiceGrpc.class) {
        if ((getRemoveMapMethod = HazelcastServiceGrpc.getRemoveMapMethod) == null) {
          HazelcastServiceGrpc.getRemoveMapMethod = getRemoveMapMethod =
              io.grpc.MethodDescriptor.<com.github.doodler.common.hazelcast.Hazelcast.KeyRequest, com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RemoveMap"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.KeyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.OperationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new HazelcastServiceMethodDescriptorSupplier("RemoveMap"))
              .build();
        }
      }
    }
    return getRemoveMapMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.ListEntry,
      com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getAddToListMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AddToList",
      requestType = com.github.doodler.common.hazelcast.Hazelcast.ListEntry.class,
      responseType = com.github.doodler.common.hazelcast.Hazelcast.OperationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.ListEntry,
      com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getAddToListMethod() {
    io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.ListEntry, com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getAddToListMethod;
    if ((getAddToListMethod = HazelcastServiceGrpc.getAddToListMethod) == null) {
      synchronized (HazelcastServiceGrpc.class) {
        if ((getAddToListMethod = HazelcastServiceGrpc.getAddToListMethod) == null) {
          HazelcastServiceGrpc.getAddToListMethod = getAddToListMethod =
              io.grpc.MethodDescriptor.<com.github.doodler.common.hazelcast.Hazelcast.ListEntry, com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AddToList"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.ListEntry.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.OperationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new HazelcastServiceMethodDescriptorSupplier("AddToList"))
              .build();
        }
      }
    }
    return getAddToListMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.ListRequest,
      com.github.doodler.common.hazelcast.Hazelcast.ListResponse> getGetListMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetList",
      requestType = com.github.doodler.common.hazelcast.Hazelcast.ListRequest.class,
      responseType = com.github.doodler.common.hazelcast.Hazelcast.ListResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.ListRequest,
      com.github.doodler.common.hazelcast.Hazelcast.ListResponse> getGetListMethod() {
    io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.ListRequest, com.github.doodler.common.hazelcast.Hazelcast.ListResponse> getGetListMethod;
    if ((getGetListMethod = HazelcastServiceGrpc.getGetListMethod) == null) {
      synchronized (HazelcastServiceGrpc.class) {
        if ((getGetListMethod = HazelcastServiceGrpc.getGetListMethod) == null) {
          HazelcastServiceGrpc.getGetListMethod = getGetListMethod =
              io.grpc.MethodDescriptor.<com.github.doodler.common.hazelcast.Hazelcast.ListRequest, com.github.doodler.common.hazelcast.Hazelcast.ListResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetList"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.ListRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.ListResponse.getDefaultInstance()))
              .setSchemaDescriptor(new HazelcastServiceMethodDescriptorSupplier("GetList"))
              .build();
        }
      }
    }
    return getGetListMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.ListEntry,
      com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getRemoveFromListMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RemoveFromList",
      requestType = com.github.doodler.common.hazelcast.Hazelcast.ListEntry.class,
      responseType = com.github.doodler.common.hazelcast.Hazelcast.OperationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.ListEntry,
      com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getRemoveFromListMethod() {
    io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.ListEntry, com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getRemoveFromListMethod;
    if ((getRemoveFromListMethod = HazelcastServiceGrpc.getRemoveFromListMethod) == null) {
      synchronized (HazelcastServiceGrpc.class) {
        if ((getRemoveFromListMethod = HazelcastServiceGrpc.getRemoveFromListMethod) == null) {
          HazelcastServiceGrpc.getRemoveFromListMethod = getRemoveFromListMethod =
              io.grpc.MethodDescriptor.<com.github.doodler.common.hazelcast.Hazelcast.ListEntry, com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RemoveFromList"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.ListEntry.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.OperationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new HazelcastServiceMethodDescriptorSupplier("RemoveFromList"))
              .build();
        }
      }
    }
    return getRemoveFromListMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.SetEntry,
      com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getAddToSetMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AddToSet",
      requestType = com.github.doodler.common.hazelcast.Hazelcast.SetEntry.class,
      responseType = com.github.doodler.common.hazelcast.Hazelcast.OperationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.SetEntry,
      com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getAddToSetMethod() {
    io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.SetEntry, com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getAddToSetMethod;
    if ((getAddToSetMethod = HazelcastServiceGrpc.getAddToSetMethod) == null) {
      synchronized (HazelcastServiceGrpc.class) {
        if ((getAddToSetMethod = HazelcastServiceGrpc.getAddToSetMethod) == null) {
          HazelcastServiceGrpc.getAddToSetMethod = getAddToSetMethod =
              io.grpc.MethodDescriptor.<com.github.doodler.common.hazelcast.Hazelcast.SetEntry, com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AddToSet"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.SetEntry.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.OperationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new HazelcastServiceMethodDescriptorSupplier("AddToSet"))
              .build();
        }
      }
    }
    return getAddToSetMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.SetRequest,
      com.github.doodler.common.hazelcast.Hazelcast.SetResponse> getGetSetMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetSet",
      requestType = com.github.doodler.common.hazelcast.Hazelcast.SetRequest.class,
      responseType = com.github.doodler.common.hazelcast.Hazelcast.SetResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.SetRequest,
      com.github.doodler.common.hazelcast.Hazelcast.SetResponse> getGetSetMethod() {
    io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.SetRequest, com.github.doodler.common.hazelcast.Hazelcast.SetResponse> getGetSetMethod;
    if ((getGetSetMethod = HazelcastServiceGrpc.getGetSetMethod) == null) {
      synchronized (HazelcastServiceGrpc.class) {
        if ((getGetSetMethod = HazelcastServiceGrpc.getGetSetMethod) == null) {
          HazelcastServiceGrpc.getGetSetMethod = getGetSetMethod =
              io.grpc.MethodDescriptor.<com.github.doodler.common.hazelcast.Hazelcast.SetRequest, com.github.doodler.common.hazelcast.Hazelcast.SetResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetSet"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.SetRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.SetResponse.getDefaultInstance()))
              .setSchemaDescriptor(new HazelcastServiceMethodDescriptorSupplier("GetSet"))
              .build();
        }
      }
    }
    return getGetSetMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.SetEntry,
      com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getRemoveFromSetMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RemoveFromSet",
      requestType = com.github.doodler.common.hazelcast.Hazelcast.SetEntry.class,
      responseType = com.github.doodler.common.hazelcast.Hazelcast.OperationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.SetEntry,
      com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getRemoveFromSetMethod() {
    io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.SetEntry, com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getRemoveFromSetMethod;
    if ((getRemoveFromSetMethod = HazelcastServiceGrpc.getRemoveFromSetMethod) == null) {
      synchronized (HazelcastServiceGrpc.class) {
        if ((getRemoveFromSetMethod = HazelcastServiceGrpc.getRemoveFromSetMethod) == null) {
          HazelcastServiceGrpc.getRemoveFromSetMethod = getRemoveFromSetMethod =
              io.grpc.MethodDescriptor.<com.github.doodler.common.hazelcast.Hazelcast.SetEntry, com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RemoveFromSet"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.SetEntry.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.OperationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new HazelcastServiceMethodDescriptorSupplier("RemoveFromSet"))
              .build();
        }
      }
    }
    return getRemoveFromSetMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.QueueEntry,
      com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getEnqueueMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Enqueue",
      requestType = com.github.doodler.common.hazelcast.Hazelcast.QueueEntry.class,
      responseType = com.github.doodler.common.hazelcast.Hazelcast.OperationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.QueueEntry,
      com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getEnqueueMethod() {
    io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.QueueEntry, com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> getEnqueueMethod;
    if ((getEnqueueMethod = HazelcastServiceGrpc.getEnqueueMethod) == null) {
      synchronized (HazelcastServiceGrpc.class) {
        if ((getEnqueueMethod = HazelcastServiceGrpc.getEnqueueMethod) == null) {
          HazelcastServiceGrpc.getEnqueueMethod = getEnqueueMethod =
              io.grpc.MethodDescriptor.<com.github.doodler.common.hazelcast.Hazelcast.QueueEntry, com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Enqueue"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.QueueEntry.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.OperationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new HazelcastServiceMethodDescriptorSupplier("Enqueue"))
              .build();
        }
      }
    }
    return getEnqueueMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.QueueRequest,
      com.github.doodler.common.hazelcast.Hazelcast.ValueResponse> getDequeueMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Dequeue",
      requestType = com.github.doodler.common.hazelcast.Hazelcast.QueueRequest.class,
      responseType = com.github.doodler.common.hazelcast.Hazelcast.ValueResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.QueueRequest,
      com.github.doodler.common.hazelcast.Hazelcast.ValueResponse> getDequeueMethod() {
    io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.QueueRequest, com.github.doodler.common.hazelcast.Hazelcast.ValueResponse> getDequeueMethod;
    if ((getDequeueMethod = HazelcastServiceGrpc.getDequeueMethod) == null) {
      synchronized (HazelcastServiceGrpc.class) {
        if ((getDequeueMethod = HazelcastServiceGrpc.getDequeueMethod) == null) {
          HazelcastServiceGrpc.getDequeueMethod = getDequeueMethod =
              io.grpc.MethodDescriptor.<com.github.doodler.common.hazelcast.Hazelcast.QueueRequest, com.github.doodler.common.hazelcast.Hazelcast.ValueResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Dequeue"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.QueueRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.ValueResponse.getDefaultInstance()))
              .setSchemaDescriptor(new HazelcastServiceMethodDescriptorSupplier("Dequeue"))
              .build();
        }
      }
    }
    return getDequeueMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.QueueRequest,
      com.github.doodler.common.hazelcast.Hazelcast.SizeResponse> getGetQueueSizeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetQueueSize",
      requestType = com.github.doodler.common.hazelcast.Hazelcast.QueueRequest.class,
      responseType = com.github.doodler.common.hazelcast.Hazelcast.SizeResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.QueueRequest,
      com.github.doodler.common.hazelcast.Hazelcast.SizeResponse> getGetQueueSizeMethod() {
    io.grpc.MethodDescriptor<com.github.doodler.common.hazelcast.Hazelcast.QueueRequest, com.github.doodler.common.hazelcast.Hazelcast.SizeResponse> getGetQueueSizeMethod;
    if ((getGetQueueSizeMethod = HazelcastServiceGrpc.getGetQueueSizeMethod) == null) {
      synchronized (HazelcastServiceGrpc.class) {
        if ((getGetQueueSizeMethod = HazelcastServiceGrpc.getGetQueueSizeMethod) == null) {
          HazelcastServiceGrpc.getGetQueueSizeMethod = getGetQueueSizeMethod =
              io.grpc.MethodDescriptor.<com.github.doodler.common.hazelcast.Hazelcast.QueueRequest, com.github.doodler.common.hazelcast.Hazelcast.SizeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetQueueSize"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.QueueRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.doodler.common.hazelcast.Hazelcast.SizeResponse.getDefaultInstance()))
              .setSchemaDescriptor(new HazelcastServiceMethodDescriptorSupplier("GetQueueSize"))
              .build();
        }
      }
    }
    return getGetQueueSizeMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static HazelcastServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<HazelcastServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<HazelcastServiceStub>() {
        @java.lang.Override
        public HazelcastServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new HazelcastServiceStub(channel, callOptions);
        }
      };
    return HazelcastServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static HazelcastServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<HazelcastServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<HazelcastServiceBlockingStub>() {
        @java.lang.Override
        public HazelcastServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new HazelcastServiceBlockingStub(channel, callOptions);
        }
      };
    return HazelcastServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static HazelcastServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<HazelcastServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<HazelcastServiceFutureStub>() {
        @java.lang.Override
        public HazelcastServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new HazelcastServiceFutureStub(channel, callOptions);
        }
      };
    return HazelcastServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Hazelcast 缓存服务接口
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * 操作Map
     * </pre>
     */
    default void putMap(com.github.doodler.common.hazelcast.Hazelcast.MapEntry request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPutMapMethod(), responseObserver);
    }

    /**
     */
    default void getMap(com.github.doodler.common.hazelcast.Hazelcast.KeyRequest request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.ValueResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetMapMethod(), responseObserver);
    }

    /**
     */
    default void removeMap(com.github.doodler.common.hazelcast.Hazelcast.KeyRequest request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRemoveMapMethod(), responseObserver);
    }

    /**
     * <pre>
     * 操作List
     * </pre>
     */
    default void addToList(com.github.doodler.common.hazelcast.Hazelcast.ListEntry request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAddToListMethod(), responseObserver);
    }

    /**
     */
    default void getList(com.github.doodler.common.hazelcast.Hazelcast.ListRequest request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.ListResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetListMethod(), responseObserver);
    }

    /**
     */
    default void removeFromList(com.github.doodler.common.hazelcast.Hazelcast.ListEntry request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRemoveFromListMethod(), responseObserver);
    }

    /**
     * <pre>
     * 操作Set
     * </pre>
     */
    default void addToSet(com.github.doodler.common.hazelcast.Hazelcast.SetEntry request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAddToSetMethod(), responseObserver);
    }

    /**
     */
    default void getSet(com.github.doodler.common.hazelcast.Hazelcast.SetRequest request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.SetResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetSetMethod(), responseObserver);
    }

    /**
     */
    default void removeFromSet(com.github.doodler.common.hazelcast.Hazelcast.SetEntry request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRemoveFromSetMethod(), responseObserver);
    }

    /**
     * <pre>
     * 操作Queue
     * </pre>
     */
    default void enqueue(com.github.doodler.common.hazelcast.Hazelcast.QueueEntry request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getEnqueueMethod(), responseObserver);
    }

    /**
     */
    default void dequeue(com.github.doodler.common.hazelcast.Hazelcast.QueueRequest request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.ValueResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDequeueMethod(), responseObserver);
    }

    /**
     */
    default void getQueueSize(com.github.doodler.common.hazelcast.Hazelcast.QueueRequest request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.SizeResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetQueueSizeMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service HazelcastService.
   * <pre>
   * Hazelcast 缓存服务接口
   * </pre>
   */
  public static abstract class HazelcastServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return HazelcastServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service HazelcastService.
   * <pre>
   * Hazelcast 缓存服务接口
   * </pre>
   */
  public static final class HazelcastServiceStub
      extends io.grpc.stub.AbstractAsyncStub<HazelcastServiceStub> {
    private HazelcastServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HazelcastServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new HazelcastServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * 操作Map
     * </pre>
     */
    public void putMap(com.github.doodler.common.hazelcast.Hazelcast.MapEntry request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPutMapMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getMap(com.github.doodler.common.hazelcast.Hazelcast.KeyRequest request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.ValueResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetMapMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void removeMap(com.github.doodler.common.hazelcast.Hazelcast.KeyRequest request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRemoveMapMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 操作List
     * </pre>
     */
    public void addToList(com.github.doodler.common.hazelcast.Hazelcast.ListEntry request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAddToListMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getList(com.github.doodler.common.hazelcast.Hazelcast.ListRequest request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.ListResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetListMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void removeFromList(com.github.doodler.common.hazelcast.Hazelcast.ListEntry request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRemoveFromListMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 操作Set
     * </pre>
     */
    public void addToSet(com.github.doodler.common.hazelcast.Hazelcast.SetEntry request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAddToSetMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getSet(com.github.doodler.common.hazelcast.Hazelcast.SetRequest request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.SetResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetSetMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void removeFromSet(com.github.doodler.common.hazelcast.Hazelcast.SetEntry request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRemoveFromSetMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 操作Queue
     * </pre>
     */
    public void enqueue(com.github.doodler.common.hazelcast.Hazelcast.QueueEntry request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getEnqueueMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void dequeue(com.github.doodler.common.hazelcast.Hazelcast.QueueRequest request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.ValueResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDequeueMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getQueueSize(com.github.doodler.common.hazelcast.Hazelcast.QueueRequest request,
        io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.SizeResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetQueueSizeMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service HazelcastService.
   * <pre>
   * Hazelcast 缓存服务接口
   * </pre>
   */
  public static final class HazelcastServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<HazelcastServiceBlockingStub> {
    private HazelcastServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HazelcastServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new HazelcastServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * 操作Map
     * </pre>
     */
    public com.github.doodler.common.hazelcast.Hazelcast.OperationResponse putMap(com.github.doodler.common.hazelcast.Hazelcast.MapEntry request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPutMapMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.doodler.common.hazelcast.Hazelcast.ValueResponse getMap(com.github.doodler.common.hazelcast.Hazelcast.KeyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetMapMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.doodler.common.hazelcast.Hazelcast.OperationResponse removeMap(com.github.doodler.common.hazelcast.Hazelcast.KeyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRemoveMapMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 操作List
     * </pre>
     */
    public com.github.doodler.common.hazelcast.Hazelcast.OperationResponse addToList(com.github.doodler.common.hazelcast.Hazelcast.ListEntry request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddToListMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.doodler.common.hazelcast.Hazelcast.ListResponse getList(com.github.doodler.common.hazelcast.Hazelcast.ListRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetListMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.doodler.common.hazelcast.Hazelcast.OperationResponse removeFromList(com.github.doodler.common.hazelcast.Hazelcast.ListEntry request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRemoveFromListMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 操作Set
     * </pre>
     */
    public com.github.doodler.common.hazelcast.Hazelcast.OperationResponse addToSet(com.github.doodler.common.hazelcast.Hazelcast.SetEntry request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddToSetMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.doodler.common.hazelcast.Hazelcast.SetResponse getSet(com.github.doodler.common.hazelcast.Hazelcast.SetRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetSetMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.doodler.common.hazelcast.Hazelcast.OperationResponse removeFromSet(com.github.doodler.common.hazelcast.Hazelcast.SetEntry request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRemoveFromSetMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 操作Queue
     * </pre>
     */
    public com.github.doodler.common.hazelcast.Hazelcast.OperationResponse enqueue(com.github.doodler.common.hazelcast.Hazelcast.QueueEntry request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getEnqueueMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.doodler.common.hazelcast.Hazelcast.ValueResponse dequeue(com.github.doodler.common.hazelcast.Hazelcast.QueueRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDequeueMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.doodler.common.hazelcast.Hazelcast.SizeResponse getQueueSize(com.github.doodler.common.hazelcast.Hazelcast.QueueRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetQueueSizeMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service HazelcastService.
   * <pre>
   * Hazelcast 缓存服务接口
   * </pre>
   */
  public static final class HazelcastServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<HazelcastServiceFutureStub> {
    private HazelcastServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HazelcastServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new HazelcastServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * 操作Map
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> putMap(
        com.github.doodler.common.hazelcast.Hazelcast.MapEntry request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPutMapMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.doodler.common.hazelcast.Hazelcast.ValueResponse> getMap(
        com.github.doodler.common.hazelcast.Hazelcast.KeyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetMapMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> removeMap(
        com.github.doodler.common.hazelcast.Hazelcast.KeyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRemoveMapMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 操作List
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> addToList(
        com.github.doodler.common.hazelcast.Hazelcast.ListEntry request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAddToListMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.doodler.common.hazelcast.Hazelcast.ListResponse> getList(
        com.github.doodler.common.hazelcast.Hazelcast.ListRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetListMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> removeFromList(
        com.github.doodler.common.hazelcast.Hazelcast.ListEntry request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRemoveFromListMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 操作Set
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> addToSet(
        com.github.doodler.common.hazelcast.Hazelcast.SetEntry request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAddToSetMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.doodler.common.hazelcast.Hazelcast.SetResponse> getSet(
        com.github.doodler.common.hazelcast.Hazelcast.SetRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetSetMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> removeFromSet(
        com.github.doodler.common.hazelcast.Hazelcast.SetEntry request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRemoveFromSetMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 操作Queue
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse> enqueue(
        com.github.doodler.common.hazelcast.Hazelcast.QueueEntry request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getEnqueueMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.doodler.common.hazelcast.Hazelcast.ValueResponse> dequeue(
        com.github.doodler.common.hazelcast.Hazelcast.QueueRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDequeueMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.doodler.common.hazelcast.Hazelcast.SizeResponse> getQueueSize(
        com.github.doodler.common.hazelcast.Hazelcast.QueueRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetQueueSizeMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PUT_MAP = 0;
  private static final int METHODID_GET_MAP = 1;
  private static final int METHODID_REMOVE_MAP = 2;
  private static final int METHODID_ADD_TO_LIST = 3;
  private static final int METHODID_GET_LIST = 4;
  private static final int METHODID_REMOVE_FROM_LIST = 5;
  private static final int METHODID_ADD_TO_SET = 6;
  private static final int METHODID_GET_SET = 7;
  private static final int METHODID_REMOVE_FROM_SET = 8;
  private static final int METHODID_ENQUEUE = 9;
  private static final int METHODID_DEQUEUE = 10;
  private static final int METHODID_GET_QUEUE_SIZE = 11;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PUT_MAP:
          serviceImpl.putMap((com.github.doodler.common.hazelcast.Hazelcast.MapEntry) request,
              (io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>) responseObserver);
          break;
        case METHODID_GET_MAP:
          serviceImpl.getMap((com.github.doodler.common.hazelcast.Hazelcast.KeyRequest) request,
              (io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.ValueResponse>) responseObserver);
          break;
        case METHODID_REMOVE_MAP:
          serviceImpl.removeMap((com.github.doodler.common.hazelcast.Hazelcast.KeyRequest) request,
              (io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>) responseObserver);
          break;
        case METHODID_ADD_TO_LIST:
          serviceImpl.addToList((com.github.doodler.common.hazelcast.Hazelcast.ListEntry) request,
              (io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>) responseObserver);
          break;
        case METHODID_GET_LIST:
          serviceImpl.getList((com.github.doodler.common.hazelcast.Hazelcast.ListRequest) request,
              (io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.ListResponse>) responseObserver);
          break;
        case METHODID_REMOVE_FROM_LIST:
          serviceImpl.removeFromList((com.github.doodler.common.hazelcast.Hazelcast.ListEntry) request,
              (io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>) responseObserver);
          break;
        case METHODID_ADD_TO_SET:
          serviceImpl.addToSet((com.github.doodler.common.hazelcast.Hazelcast.SetEntry) request,
              (io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>) responseObserver);
          break;
        case METHODID_GET_SET:
          serviceImpl.getSet((com.github.doodler.common.hazelcast.Hazelcast.SetRequest) request,
              (io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.SetResponse>) responseObserver);
          break;
        case METHODID_REMOVE_FROM_SET:
          serviceImpl.removeFromSet((com.github.doodler.common.hazelcast.Hazelcast.SetEntry) request,
              (io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>) responseObserver);
          break;
        case METHODID_ENQUEUE:
          serviceImpl.enqueue((com.github.doodler.common.hazelcast.Hazelcast.QueueEntry) request,
              (io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>) responseObserver);
          break;
        case METHODID_DEQUEUE:
          serviceImpl.dequeue((com.github.doodler.common.hazelcast.Hazelcast.QueueRequest) request,
              (io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.ValueResponse>) responseObserver);
          break;
        case METHODID_GET_QUEUE_SIZE:
          serviceImpl.getQueueSize((com.github.doodler.common.hazelcast.Hazelcast.QueueRequest) request,
              (io.grpc.stub.StreamObserver<com.github.doodler.common.hazelcast.Hazelcast.SizeResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getPutMapMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.github.doodler.common.hazelcast.Hazelcast.MapEntry,
              com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>(
                service, METHODID_PUT_MAP)))
        .addMethod(
          getGetMapMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.github.doodler.common.hazelcast.Hazelcast.KeyRequest,
              com.github.doodler.common.hazelcast.Hazelcast.ValueResponse>(
                service, METHODID_GET_MAP)))
        .addMethod(
          getRemoveMapMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.github.doodler.common.hazelcast.Hazelcast.KeyRequest,
              com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>(
                service, METHODID_REMOVE_MAP)))
        .addMethod(
          getAddToListMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.github.doodler.common.hazelcast.Hazelcast.ListEntry,
              com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>(
                service, METHODID_ADD_TO_LIST)))
        .addMethod(
          getGetListMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.github.doodler.common.hazelcast.Hazelcast.ListRequest,
              com.github.doodler.common.hazelcast.Hazelcast.ListResponse>(
                service, METHODID_GET_LIST)))
        .addMethod(
          getRemoveFromListMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.github.doodler.common.hazelcast.Hazelcast.ListEntry,
              com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>(
                service, METHODID_REMOVE_FROM_LIST)))
        .addMethod(
          getAddToSetMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.github.doodler.common.hazelcast.Hazelcast.SetEntry,
              com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>(
                service, METHODID_ADD_TO_SET)))
        .addMethod(
          getGetSetMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.github.doodler.common.hazelcast.Hazelcast.SetRequest,
              com.github.doodler.common.hazelcast.Hazelcast.SetResponse>(
                service, METHODID_GET_SET)))
        .addMethod(
          getRemoveFromSetMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.github.doodler.common.hazelcast.Hazelcast.SetEntry,
              com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>(
                service, METHODID_REMOVE_FROM_SET)))
        .addMethod(
          getEnqueueMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.github.doodler.common.hazelcast.Hazelcast.QueueEntry,
              com.github.doodler.common.hazelcast.Hazelcast.OperationResponse>(
                service, METHODID_ENQUEUE)))
        .addMethod(
          getDequeueMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.github.doodler.common.hazelcast.Hazelcast.QueueRequest,
              com.github.doodler.common.hazelcast.Hazelcast.ValueResponse>(
                service, METHODID_DEQUEUE)))
        .addMethod(
          getGetQueueSizeMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.github.doodler.common.hazelcast.Hazelcast.QueueRequest,
              com.github.doodler.common.hazelcast.Hazelcast.SizeResponse>(
                service, METHODID_GET_QUEUE_SIZE)))
        .build();
  }

  private static abstract class HazelcastServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    HazelcastServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.github.doodler.common.hazelcast.Hazelcast.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("HazelcastService");
    }
  }

  private static final class HazelcastServiceFileDescriptorSupplier
      extends HazelcastServiceBaseDescriptorSupplier {
    HazelcastServiceFileDescriptorSupplier() {}
  }

  private static final class HazelcastServiceMethodDescriptorSupplier
      extends HazelcastServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    HazelcastServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (HazelcastServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new HazelcastServiceFileDescriptorSupplier())
              .addMethod(getPutMapMethod())
              .addMethod(getGetMapMethod())
              .addMethod(getRemoveMapMethod())
              .addMethod(getAddToListMethod())
              .addMethod(getGetListMethod())
              .addMethod(getRemoveFromListMethod())
              .addMethod(getAddToSetMethod())
              .addMethod(getGetSetMethod())
              .addMethod(getRemoveFromSetMethod())
              .addMethod(getEnqueueMethod())
              .addMethod(getDequeueMethod())
              .addMethod(getGetQueueSizeMethod())
              .build();
        }
      }
    }
    return result;
  }
}
