package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;

@Service
public class CartServiceImpl implements CartService {
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public List<Cart> addGoodsToCartList(Long itemId, Integer num, List<Cart> cartList) {
		
		//1.根据商品SKU ID查询商品信息
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		if(item==null) {
			throw new RuntimeException("该商品不存在!");
		}
		
		if(item.getStatus().equals("2")) {
			throw new RuntimeException("该商品无效!");
		}
		//2.获取商家ID
		String sellerId = item.getSellerId();
		//3.根据商家ID判断购物车列表中是否存在该商家购物车
		Cart cart = searchCartBySellerId(sellerId, cartList);
		
		
		if(cart==null) {//4如果购物车列表中不存在该商家购物车
			
			//4.1新建购物车对象
			cart = new Cart();
			cart.setSellerId(sellerId); //商家ID
			cart.setSellerName(item.getSeller()); //商家名称
			TbOrderItem orderItem = createOrderItem(num,item); //创建购物车明细
			List<TbOrderItem> orderItemList = new ArrayList(); //创建购物车明细列表
			orderItemList.add(orderItem); //将购物车明细添加到明细列表
			
			cart.setOrderItemList(orderItemList);
			
			//4.2将新建的购物车对象添加到购物车列表
			cartList.add(cart);
		}else { //5如果购物车列表中存在该商家购物车
			
			//5.1 查询购物车明细列表中是否存在该商品
			TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(),itemId);
			
			if(orderItem==null) {//5.2如果购物车明细列表中不存在该商品，直接加入该商家购物车明细列表中
				orderItem = createOrderItem(num,item);
				cart.getOrderItemList().add(orderItem); 
			}else { //5.3如果购物车明细列表中存在该商品，该商品数量增加,更改金额
				orderItem.setNum(orderItem.getNum()+num);
				orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));
				
				//如果数量操作后<=0,则移除
				if(orderItem.getNum()<=0) {
					cart.getOrderItemList().remove(orderItem);
				}
				
				//如果购物车明细列表为空，则移除该商家购物车
				if(cart.getOrderItemList().size()<=0) {
					cartList.remove(cart);
				}
				
			}
			
			
		}
		
		return cartList;
	}
	
	/**
	 * 根据商品ID查询商品明细列表
	 * @param orderItemList
	 * @param ItemId
	 * @return
	 */
	private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long ItemId) {
		for(TbOrderItem orderItem:orderItemList) {
			if(orderItem.getItemId().longValue()==ItemId.longValue()) {
				return orderItem;
			}
		}
		return null;
	}
	
	/**
	 * 创建订单明细
	 * @param num
	 * @param item
	 * @return
	 */
	private TbOrderItem createOrderItem(Integer num,TbItem item) {

		if(num<0) {
			throw new RuntimeException("商品数量非法！");
		}
		
		TbOrderItem orderItem = new TbOrderItem();
		orderItem.setGoodsId(item.getGoodsId()); //商品ID
		orderItem.setItemId(item.getId()); //SKU ID
		orderItem.setNum(num); //数量
		orderItem.setPicPath(item.getImage());//商品图片
		orderItem.setPrice(item.getPrice()); //商品价格
		orderItem.setSellerId(item.getSellerId()); //商家ID
		orderItem.setTitle(item.getTitle());//商品标题
		orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*num) ); //总金额
		
		return orderItem;
	}
	
	/**
	 * 根据商家ID判断购物车列表中是否存在该商家购物车
	 * @param sellerId
	 * @param cartList
	 * @return
	 */
	private Cart searchCartBySellerId(String sellerId,List<Cart> cartList) {
		for(Cart cart:cartList) {
			if(cart.getSellerId().equals(sellerId)) {
				return cart;
			}
		}
		
		return null;
	}

	@Override
	public List<Cart> findCartListFromRedis(String username) {
		System.out.println("从redis中提取购物车数据....."+username);
		
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cart").get(username);
		if(cartList==null) {
			cartList = new ArrayList();
		}
		
		return cartList;
	}

	@Override
	public void saveCartListToRedis(String username, List<Cart> cartList) {
		System.out.println("向redis存入购物车数据....."+username);
		redisTemplate.boundHashOps("cart").put(username, cartList);
		
	}

	@Override
	public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
		System.out.println("合并购物车");
		for(Cart cart:cartList2) {
			for(TbOrderItem orderItem:cart.getOrderItemList()) {
				cartList1 = addGoodsToCartList(orderItem.getItemId(), orderItem.getNum(), cartList1);
			}
		}
		
		return cartList1;
	}

}
