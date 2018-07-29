package com.pinyougou.shop.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

import entity.Result;

@RestController
@RequestMapping("/seller")
public class SellerController {
	
	@Reference
	private SellerService sellerService;

	@RequestMapping("/add")
	public Result add(@RequestBody TbSeller tbSeller) {
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String password = passwordEncoder.encode(tbSeller.getPassword());
		tbSeller.setPassword(password);
		
		try {
			sellerService.add(tbSeller);
			return new Result(true,"添加成功");
		}catch(Exception e) {
			e.printStackTrace();
			return new Result(false, "添加失败") ;
		}
	}
	@RequestMapping("/showName")
	public Map showName() {
		Map map = new HashMap();
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		map.put("loginName", name);
		return map;
	}
}
