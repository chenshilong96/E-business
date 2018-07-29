package com.pinyougou.cart.controller;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.user.service.AddressService;

import entity.Result;

@RestController
@RequestMapping("/address")
public class AddressController {

	@Reference
	private AddressService addressService;
	
	/**
	 * 根据用户名查找地址列表
	 * @return
	 */
	@RequestMapping("/findAddressListByUserId")
	public List<TbAddress> findAddressListByUserId(){
		
		//获取登录用户的用户名
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		return addressService.findAddressListByUser(userId);
	}
	
	/**
	 * 添加地址
	 * @param address
	 * @return
	 */
	@RequestMapping("/addNewAddress")
	public Result addNewAddress(TbAddress address) {
		try {
			//获取登录用户的用户名
			String userId = SecurityContextHolder.getContext().getAuthentication().getName();
			address.setUserId(userId);
			addressService.addAddress(address);
			return new Result(false, "增加成功");
		}catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
}
