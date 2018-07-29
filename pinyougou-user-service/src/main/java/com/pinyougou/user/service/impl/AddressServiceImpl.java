package com.pinyougou.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbAddressMapper;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbAddressExample;
import com.pinyougou.pojo.TbAddressExample.Criteria;
import com.pinyougou.user.service.AddressService;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	private TbAddressMapper addressMapper;

	/**
	 * 根据登录用户查询用户地址列表
	 * 
	 * @param userId
	 * @return
	 */
	@Override
	public List<TbAddress> findAddressListByUser(String userId) {

		TbAddressExample example = new TbAddressExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(userId);
		return addressMapper.selectByExample(example);

	}

	@Override
	public void addAddress(TbAddress address) {
		addressMapper.insert(address);
	}

}
