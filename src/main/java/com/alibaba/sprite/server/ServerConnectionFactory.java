/*
 * Copyright 1999-2014 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.sprite.server;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import com.alibaba.sprite.SpriteServer;
import com.alibaba.sprite.core.BufferQueue;
import com.alibaba.sprite.core.net.Connection;
import com.alibaba.sprite.core.net.ConnectionFactory;
import com.alibaba.sprite.core.util.UUIDUtil;

/**
 * @author xianmao.hexm
 */
public final class ServerConnectionFactory implements ConnectionFactory {

    protected int socketRecvBuffer = 8 * 1024;
    protected int socketSendBuffer = 8 * 1024;
    protected int packetHeaderSize = 4;
    protected int maxPacketSize = 16 * 1024 * 1024;
    protected int writeQueueCapcity = 16;
    protected long idleTimeout = 3600 * 1000L;
    protected String charset = "utf8";

    public Connection make(SocketChannel channel) throws IOException {
        Socket socket = channel.socket();
        socket.setReceiveBufferSize(socketRecvBuffer);
        socket.setSendBufferSize(socketSendBuffer);
        socket.setTcpNoDelay(true);
        socket.setKeepAlive(true);
        ServerConnection c = new ServerConnection(channel);
        c.setId(UUIDUtil.newKey());
        c.setHost(socket.getInetAddress().getHostAddress());
        c.setPort(socket.getPort());
        c.setLocalPort(socket.getLocalPort());
        c.setPacketHeaderSize(packetHeaderSize);
        c.setMaxPacketSize(maxPacketSize);
        c.setWriteQueue(new BufferQueue(writeQueueCapcity));
        c.setIdleTimeout(idleTimeout);
        c.setCharset(charset);
        c.setHandler(new AuthHandler(c));

        // 加入到连接集合中
        SpriteServer.getInstance().getConnections().put(c.getId(), c);

        return c;
    }

}
