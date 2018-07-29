package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbSeller;

import entity.PageResult;

public interface SellerService {

	/**
	 * 商家注册
	 * @param tbSeller
	 */
	void add(TbSeller tbSeller);
	/**
	 * 分页条件查询
	 * @param page
	 * @param rows
	 * @param seller
	 * @return
	 */
	PageResult pageSearch(int page,int rows,TbSeller seller);
	
	/**
	 * 根据用户名查找对应的商家
	 * @param sellerId
	 * @return
	 */
	TbSeller findOne(String sellerId);
	
	/**
	 * 商家审核
	 * @param status
	 */
	void check(String sellerId,String status);
	
	/**
	 * 根据用户名查找商家
	 * @param username
	 * @return
	 */
	TbSeller findSeller(String username);
}
