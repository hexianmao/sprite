/*
 * Copyright 1999-2012 Alibaba Group.
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
package com.alibaba.sprite.manager.packet;

import java.nio.ByteBuffer;

import com.alibaba.sprite.core.AbstractPacket;
import com.alibaba.sprite.core.net.Connection;
import com.alibaba.sprite.core.util.BufferUtil;

/**
 * From server to client in response to command, if no error and no result set.
 * 
 * <pre>
 * Bytes                       Name
 * -----                       ----
 * 1                           field_count, always = 0
 * 1-9 (Length Coded Binary)   affected_rows
 * 1-9 (Length Coded Binary)   insert_id
 * 2                           server_status
 * 2                           warning_count
 * n   (until end of packet)   message fix:(Length Coded String)
 * 
 * @see http://forge.mysql.com/wiki/MySQL_Internals_ClientServer_Protocol#OK_Packet
 * </pre>
 * 
 * @author xianmao.hexm 2010-7-16 上午10:33:50
 */
public class OkPacket extends AbstractPacket {

    public static final byte FIELD_COUNT = 0x00;
    public static final byte[] OK = new byte[] { 7, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0 };

    public byte fieldCount = FIELD_COUNT;
    public long affectedRows;
    public long insertId;
    public int serverStatus;
    public int warningCount;
    public byte[] message;

    public void write(Connection c) {
        ByteBuffer buffer = c.allocateBuffer();
        BufferUtil.writeUB3(buffer, packetSize());
        buffer.put(packetId);
        buffer.put(fieldCount);
        BufferUtil.writeLength(buffer, affectedRows);
        BufferUtil.writeLength(buffer, insertId);
        BufferUtil.writeUB2(buffer, serverStatus);
        BufferUtil.writeUB2(buffer, warningCount);
        if (message != null) {
            BufferUtil.writeWithLength(buffer, message);
        }
        c.postWrite(buffer);
    }

    @Override
    public int packetSize() {
        int i = 1;
        i += BufferUtil.getLength(affectedRows);
        i += BufferUtil.getLength(insertId);
        i += 4;
        if (message != null) {
            i += BufferUtil.getLength(message);
        }
        return i;
    }

}
