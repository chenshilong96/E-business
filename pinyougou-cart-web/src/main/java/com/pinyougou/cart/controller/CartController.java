package com.pinyougou.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.utils.CookieUtil;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojogroup.Cart;

import entity.Result;

@RestController
@RequestMapping("/cart")
public class CartController {

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpServletResponse response;
	@Reference(timeout=6000)
	private CartService cartService;
	
	
	/**
	 * 从cookie中查找购物车
	 * 
	 * @return
	 */
	@RequestMapping("/findCartList")
	public List<Cart> findCartList() {
		String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
		if (cartListString == null || cartListString.equals("")) {
			cartListString = "[]";
		}
		List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
		
		// 1.判断用户是否已经登录
				String username = SecurityContextHolder.getContext().getAuthentication().getName();
		if(username.equals("anonymousUser")) { //2.用户未登录 从cookie中提取
			return cartList_cookie;
		}else {  //3.用户已登录 从redis中取
			
			List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
			if(cartList_cookie.size()>0) {
				cartList_redis = cartService.mergeCartList(cartList_redis, cartList_cookie); //合并购物车
				CookieUtil.deleteCookie(request, response, "cartList"); //清楚cookie中的数据
				cartService.saveCartListToRedis(username, cartList_redis); //将合并后的数据存入redis
			}
			
			return cartList_redis;
		}

	}

	/**
	 * 添加商品到购物车
	 * 
	 * @param ItemId
	 * @param num
	 * @return
	 */
	@RequestMapping("/addGoodsToCartList")
	public Result addGoodsToCartList(Long itemId, Integer num) {
		
		
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		//获取登录用户名
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		try {
			List<Cart> cartList = findCartList(); // 获取购物车列表
			cartList = cartService.addGoodsToCartList(itemId, num, cartList); // 操作购物车列表
			
			if(username.equals("anonymousUser")){ //如果未登录 保存到购物车列表
				CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24, "UTF-8");
			}else {  //如果已登录  保存到redis
				cartService.saveCartListToRedis(username, cartList);
			}
			
			return new Result(true, "添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(true, "添加失败");
		}

	}

}
