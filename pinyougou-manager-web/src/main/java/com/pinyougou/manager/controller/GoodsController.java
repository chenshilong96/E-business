package com.pinyougou.manager.controller;

import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodSerice;
	//@Reference
	//private ItemSearchService itemSearchService;
	
	@Autowired
	private Destination queueSolrDestination;
	
	@Autowired
	private Destination queueDeleteSolrDestination;
	
	@Autowired
	private Destination topicPageDestination;
	
	@Autowired
	private Destination topicPageDeleteDestination;
	@Autowired
	private JmsTemplate jsmTemplate;
	
	/**
	 * 分页查询
	 * @param page
	 * @param rows
	 * @param tbGoods
	 * @return
	 */
	@RequestMapping("/searchByPage")
	public PageResult findByPage(int page, int rows, @RequestBody TbGoods tbGoods) {
		return goodSerice.findByPage(page, rows, tbGoods);
	}
	
	/**
	 * 修改状态
	 * @param ids
	 * @param status
	 * @return
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids ,String status) {
		try {
			goodSerice.updateStatus(ids,status);
			
			System.out.println(ids+"------"+status);
			
			if(status.equals("2")) {
				
				//将审核通过的商品生成静态页
				for(final Long id:ids) {
					//itemPageService.genItemHtml(id);
					jsmTemplate.send(topicPageDestination, new MessageCreator() {
						
						@Override
						public Message createMessage(Session session) throws JMSException {
						
							return session.createTextMessage(id+"");
						}
					});
				}
				
				//将审核通过的商品信息导入到solr
				List<TbItem> itemList = goodSerice.findItemListByGoodsIdandStatus(ids, "1");
				if(itemList.size()>0) {
					//itemSearchService.importList(itemList);
					final String jsonString = JSON.toJSONString(itemList);
					jsmTemplate.send(queueSolrDestination, new MessageCreator() {
						
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(jsonString);
						}
					});
					
					
					System.out.println("导入数据成功");
				}else {
					System.out.println("没有要导入的数据");
				}
			}
			
			return new Result(true, "成功");
		}catch(Exception e) {
			e.printStackTrace();
			return new Result(false, "失败");
		}
	}
	
	/**
	 * 删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long[] ids ) {
		try {
			goodSerice.delete(ids);
			//itemSearchService.deleteList(ids);
			jsmTemplate.send(queueDeleteSolrDestination, new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					//根据商品ID删除solr中商品索引
					return session.createObjectMessage(ids);
				}
			});
			
			jsmTemplate.send(topicPageDeleteDestination, new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					//根据商品ID删除solr中商品索引
					return session.createObjectMessage(ids);
				}
			});
			
			return new Result(true, "删除成功");
		}catch(Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	//@Reference
	//private ItemPageService itemPageService;

	
	@RequestMapping("/genHtml")
	public void genHtml(Long goodsId) {
		
		//itemPageService.genItemHtml(goodsId);
		System.out.println("完成.......");
	}
}
