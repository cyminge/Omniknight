package com.cy.omniknight.socket.push;

import com.cy.omniknight.socket.message.MMessage;
import com.cy.omniknight.socket.transport.SProxy;

/**
 * Created by zhanmin on 18-4-12.
 */

public class DispatchCenter implements SProxy.ACK {


    /**
     * 这里是否需要通过Handler发送消息排队处理？？？
     * @param message
     * @param errorCode
     */
    @Override
    public void handleACK(MMessage message, int errorCode) {

    }
}
