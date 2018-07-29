package com.pinyougou.user.service;

import java.util.List;

import com.pinyougou.pojo.TbAddress;

public interface AddressService {

	public List<TbAddress> findAddressListByUser(String userId);
	
	public void addAddress(TbAddress address);
}
