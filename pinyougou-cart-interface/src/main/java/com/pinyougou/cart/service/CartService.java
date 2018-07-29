package com.pinyougou.cart.service;

import java.util.List;

import com.pinyougou.pojogroup.Cart;

/**
 * 购物车接口
 * @author csl
 *
 */
public interface CartService {

	/**
	 * 将商品添加到购物车中
	 * @param ItemId
	 * @param num
	 * @param cartList
	 * @return
	 */
	public List<Cart> addGoodsToCartList(Long ItemId,Integer num,List<Cart> cartList);
	
	
	/**
	 * 从redis中查询购物车
	 * @param username
	 * @return
	 */
	public List<Cart> findCartListFromRedis(String username);
	
	/**
	 * 将购物车保存到redis
	 * @param username
	 * @param cartList
	 */
	public void saveCartListToRedis(String username,List<Cart> cartList);

	/**
	 * 合并购物车
	 * @param cartList1
	 * @param cartList2
	 * @return
	 */
	public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);

}
