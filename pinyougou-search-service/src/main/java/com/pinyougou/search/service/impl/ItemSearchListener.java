package com.pinyougou.search.service.impl;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Component
public class ItemSearchListener implements MessageListener {

	@Autowired
	private ItemSearchService itemSearchService;
	
	@Override
	public void onMessage(Message message) {
		System.out.println("监听到消息");
		
		TextMessage textMessage = (TextMessage)message; //将监听到的消息转换为文本类型
		try {
			String text = textMessage.getText(); //获取监听到的消息
			List<TbItem> itemList = JSON.parseArray(text,TbItem.class);
			
			//将TbItem中的spec转换为map类型 并存储到specMap中 构建索引
			for(TbItem item:itemList) {
				System.out.println(item.getId()+" "+item.getTitle());
				String spec = item.getSpec();
				Map specMap = JSON.parseObject(spec,Map.class);
				item.setSpecMap(specMap);
			}
			//将审核通过的商品导入到索引库
			itemSearchService.importList(itemList);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
