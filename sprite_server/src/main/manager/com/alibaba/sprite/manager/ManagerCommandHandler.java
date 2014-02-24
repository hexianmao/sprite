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
package com.alibaba.sprite.manager;

import com.alibaba.sprite.config.ErrorCode;
import com.alibaba.sprite.net.handler.NIOHandler;
import com.alibaba.sprite.net.packet.AbstractPacket;
import com.alibaba.sprite.statistic.CommandCount;

/**
 * 前端命令处理器
 * 
 * @author xianmao.hexm
 */
public class ManagerCommandHandler implements NIOHandler {

    protected final ManagerConnection source;
    protected final CommandCount commands;

    public ManagerCommandHandler(ManagerConnection source) {
        this.source = source;
        this.commands = source.getProcessor().getCommands();
    }

    @Override
    public void handle(byte[] data) {
        switch (data[4]) {
        case AbstractPacket.COM_INIT_DB:
            commands.doInitDB();
            source.initDB(data);
            break;
        case AbstractPacket.COM_QUERY:
            commands.doQuery();
            source.query(data);
            break;
        case AbstractPacket.COM_PING:
            commands.doPing();
            source.ping();
            break;
        case AbstractPacket.COM_QUIT:
            commands.doQuit();
            source.close();
            break;
        case AbstractPacket.COM_PROCESS_KILL:
            commands.doKill();
            source.kill(data);
            break;
        case AbstractPacket.COM_STMT_PREPARE:
            commands.doStmtPrepare();
            source.stmtPrepare(data);
            break;
        case AbstractPacket.COM_STMT_EXECUTE:
            commands.doStmtExecute();
            source.stmtExecute(data);
            break;
        case AbstractPacket.COM_STMT_CLOSE:
            commands.doStmtClose();
            source.stmtClose(data);
            break;
        case AbstractPacket.COM_HEARTBEAT:
            commands.doHeartbeat();
            source.heartbeat(data);
            break;
        default:
            commands.doOther();
            source.writeErrMessage(ErrorCode.ER_UNKNOWN_COM_ERROR, "Unknown command");
        }
    }

}
