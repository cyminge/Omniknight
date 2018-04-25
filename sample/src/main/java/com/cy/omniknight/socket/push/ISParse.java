package com.cy.omniknight.socket.push;

import android.content.Context;

import com.cy.omniknight.socket.message.MEndPoint;

/**
 * Created by zhanmin on 18-4-10.
 */

public interface ISParse {

    /**
     * 将二进制内容解析为一个消息实体，若内容和端点不符合该解析实例，返回null
     *
     * @param id 消息id
     * @param obj 文本消息内容
     * @param endPoint 产生消息的端点
     * @return 解析失败时，返回null
     */
    <T> T parseBinary(Context context, int id, Object obj, MEndPoint endPoint);

}
