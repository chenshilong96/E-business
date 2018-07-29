package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;

@Service
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper brandMapper;

	@Override
	public List<TbBrand> findAll() {
		
		return brandMapper.selectByExample(null);
	}

	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand>  page = (Page<TbBrand>) brandMapper.selectByExample(null);
		return new PageResult(page.getTotal(),page.getResult());
	}

	@Override
	public void addBrand(TbBrand brand) {
		brandMapper.insert(brand);
	}

	@Override
	public TbBrand findOne(long id) {
		return brandMapper.selectByPrimaryKey(id);
	}

	@Override
	public void updateBrand(TbBrand brand) {
		brandMapper.updateByPrimaryKey(brand);
	}

	@Override
	public void deleteBrands(long[] ids) {
		for(long id:ids) {
			brandMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
		
		
		//条件查询
		TbBrandExample example = new TbBrandExample();
		Criteria criteria = example.createCriteria();
		
		//判断用户输入的条件是否为空
		if(brand!=null) {
			//品牌名为空
			if(brand.getName()!=null&&brand.getName().length()>0) {
				criteria.andNameLike("%"+brand.getName()+"%");
			}
			//首字母为空
			if(brand.getFirstChar()!=null&&brand.getFirstChar().length()>0) {
				criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
			}
		}
		
		//根据条件进行分页查询
		PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);
		//封装查询结果
		PageResult result = new PageResult(page.getTotal(), page.getResult());
		
		//返回查询结果
		return result ;
		
	
		
	}

	@Override
	public List<Map> findOptionList() {
		return brandMapper.findOptionList();
	}
	
	
}
