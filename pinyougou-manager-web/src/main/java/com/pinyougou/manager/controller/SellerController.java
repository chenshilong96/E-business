package com.pinyougou.manager.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/seller")
public class SellerController {
	
	@Reference
	private SellerService sellerService;

	/**
	 * 分页查询
	 * @param page
	 * @param rows
	 * @param seller
	 * @return
	 */
	@RequestMapping("/pageSearch")
	public PageResult pageSearch(int page,int rows,@RequestBody TbSeller seller) {
		
		return sellerService.pageSearch(page,rows,seller);
	}
	
	/**
	 * 根据ID查询
	 * @param sellerId
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbSeller findOne(String sellerId) {
		return sellerService.findOne(sellerId);
	}
	
	/**
	 * 商家审核
	 * @param sellerId
	 * @param status
	 * @return
	 */
	@RequestMapping("/check")
	public Result check(String sellerId,String status) {
		try {
			sellerService.check(sellerId,status);
			return new Result(true,"成功");
		}catch(Exception e) {
			e.printStackTrace();
			return new Result(false,"后台出错，请尽快联系管理人员");
		}
	}
}
