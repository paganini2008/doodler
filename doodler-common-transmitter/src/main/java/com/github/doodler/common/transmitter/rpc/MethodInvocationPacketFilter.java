package com.github.doodler.common.transmitter.rpc;

import com.github.doodler.common.context.BeanReflectionService;
import com.github.doodler.common.transmitter.Packet;
import com.github.doodler.common.transmitter.PacketFilter;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: MethodInvocationPacketReader
 * @Author: Fred Feng
 * @Date: 03/01/2025
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class MethodInvocationPacketFilter implements PacketFilter {

    private final BeanReflectionService beanReflectionService;

    @Override
    public Object doFilter(Packet packet) {
        String className = packet.getStringField("className");
        String beanName = packet.getStringField("beanName");
        String methodName = packet.getStringField("methodName");
        Object[] arguments = (Object[]) packet.getField("arguments");
        return beanReflectionService.invokeTargetMethod(className, beanName, methodName, arguments);
    }



}