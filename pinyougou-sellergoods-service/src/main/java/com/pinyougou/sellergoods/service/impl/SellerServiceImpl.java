package com.pinyougou.sellergoods.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojo.TbSellerExample;
import com.pinyougou.pojo.TbSellerExample.Criteria;
import com.pinyougou.sellergoods.service.SellerService;

import entity.PageResult;
@Service
public class SellerServiceImpl implements SellerService {
	
	@Autowired
	private TbSellerMapper tbSellerMapper;

	@Override
	public void add(TbSeller tbSeller) {
		
		tbSeller.setStatus("0");
		tbSeller.setCreateTime(new Date());
		tbSellerMapper.insert(tbSeller);
	}

	@Override
	public PageResult pageSearch(int page, int rows, TbSeller seller) {
		
		PageHelper.startPage(page, rows);
		
		//条件
		TbSellerExample example = new TbSellerExample();
		Criteria criteria = example.createCriteria();
		//用户名
		if(seller.getSellerId()!=null&&seller.getSellerId().length()!=0) {
			criteria.andSellerIdEqualTo(seller.getSellerId());
		}
		//公司名
		if(seller.getName()!=null&&seller.getName().length()!=0) {
			criteria.andNameLike("%"+seller.getName()+"%");
		}
		//店铺名
		if(seller.getNickName()!=null&&seller.getNickName().length()!=0) {
			criteria.andNickNameLike("%"+seller.getNickName()+"%");
		}
		//状态
		if(seller.getStatus()!=null&&seller.getStatus().length()!=0) {
			criteria.andStatusEqualTo(seller.getStatus());
		}
		
		//分页
		Page<TbSeller> p = (Page<TbSeller>) tbSellerMapper.selectByExample(example );
		return new PageResult(p.getTotal(), p.getResult());
	}

	@Override
	public TbSeller findOne(String sellerId) {
		return tbSellerMapper.selectByPrimaryKey(sellerId);
	}

	@Override
	public void check(String sellerId,String status) {
		TbSeller seller = new TbSeller();
		seller.setStatus(status);
		seller.setSellerId(sellerId);
		tbSellerMapper.updateStatus(seller);
	}

	@Override
	public TbSeller findSeller(String username) {
		
		return tbSellerMapper.selectByPrimaryKey(username);
	}

}
