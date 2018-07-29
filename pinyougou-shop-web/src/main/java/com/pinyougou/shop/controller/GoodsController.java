package com.pinyougou.shop.controller;

import java.util.List;

import org.springframework.beans.factory.support.SecurityContextProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodSerice;

	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods) {
		try {
			// 获取商家ID
			goods.getGoods().setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
			goodSerice.add(goods);
			return new Result(true, "添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "添加失败");
		}
	}

	@RequestMapping("/searchByPage")
	public PageResult findByPage(int page, int rows, @RequestBody TbGoods tbGoods) {

		// 获取商家ID
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		tbGoods.setSellerId(sellerId);

		return goodSerice.findByPage(page, rows, tbGoods);
	}

	@RequestMapping("/findOne")
	public Goods findOne(Long id) {
		return goodSerice.findOne(id);
	}

	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods) {
		// 检查是否是商家ID
		Goods goods2 = goodSerice.findOne(goods.getGoods().getId());
		// 获取当前登录的商家ID
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		// 如果传递过来的商家ID并不是当前登录的用户的ID,则属于非法操作
		if (!goods2.getGoods().getSellerId().equals(sellerId) || !goods.getGoods().getSellerId().equals(sellerId)) {
			return new Result(false, "操作非法");
		}
		try {
			goodSerice.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}

	}
}
