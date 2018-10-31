package com.cy.omniknight.socket.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class MCache {
	private Vector<MCacheFilter> filters = new Vector<MCacheFilter>(); // 过滤器
	private Map<String, MMessage> mapMsgCache = new HashMap<String, MMessage>(); // 消息缓存

	static int msgId = 0;
	
	/**
	 * 添加一个过滤器，过滤器包括对发送数据和接收数据过滤
	 */
	public void addFilter(MCacheFilter filter) {
		filters.add(filter);
	}
	
	public Collection<MMessage> listMessage()  {
		synchronized (mapMsgCache) {
			return Collections.unmodifiableCollection(new ArrayList<MMessage>(mapMsgCache.values()));
		}
	}
	
	public void clearCache() {
		synchronized (mapMsgCache) {
			mapMsgCache.clear();
		}
	}
	
	
	public int inCache(MMessage msg) {
		/* Iterates all filters, if one return navigation, return the retcode */
		for (MCacheFilter filter : filters) {
			int ret = filter.sendFilter(this, msg);
			if(ret < 0) {
				return ret;
			}
		}
		
		String key = String.valueOf(++msgId);
		
		if(msgId == 0) {
			msgId = 1;
		}
		
		msg.setMsgId(key);
//		String key = msg.getMsgId();
		synchronized (mapMsgCache) {
			mapMsgCache.put(key, msg);
		}

		int msgId = StringUtil.parseIntNotEmpty(key, 0);
		return msgId;
	}

	public int getCount() {
		return mapMsgCache.size();
	}

	public MMessage outCache(String key) {
		synchronized (mapMsgCache) {
			if(key != null) {
				return mapMsgCache.remove(key);
			} else {
				return null;
			}
		}
	}

	public MMessage getValue(String key) {
		synchronized (mapMsgCache) {
			if(key != null) {
				return mapMsgCache.get(key);
			} else {
				return null;
			}
		}
	}

	/**
	 * 获取通过指定节点发送的消息列表
	 * 
	 * @param transName
	 * @return
	 */
	public List<MMessage> getSpecTransList(String transName) {
		List<MMessage> lstMsg = new ArrayList<MMessage>();

		if (transName != null && transName.length() > 0) {
			Iterator<MMessage> iterator = mapMsgCache.values().iterator();
			while (iterator.hasNext()) {
				MMessage msg = iterator.next();

				if (msg == null) {
					continue;
				}

				if (msg.getEndPoint() == null) {
					continue;
				}

				if (msg.getEndPoint().getTransType() == null) {
					continue;
				}

				if (msg.getEndPoint().getTransType().equals(transName)) {
					lstMsg.add(msg);
				}
			}
		}

		return lstMsg;
	}
}
