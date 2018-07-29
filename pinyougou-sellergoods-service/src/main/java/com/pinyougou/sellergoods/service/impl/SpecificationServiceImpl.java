package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojo.TbSpecificationOptionExample.Criteria;
import com.pinyougou.pojogroup.Specification;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;

@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;

	@Override
	public List<TbSpecification> findAll() {

		return specificationMapper.selectByExample(null);
	}

	@Override
	public PageResult findPage(int page, int rows) {
		PageHelper.startPage(page, rows);
		Page<TbSpecification> p = (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(p.getTotal(), p.getResult());
	}

	@Override
	public void add(Specification specification) {

		// 获取并添加规格名称
		TbSpecification tbSpecification = specification.getSpecification();
		specificationMapper.insert(tbSpecification);

		// 获取规格列表
		List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
		for (TbSpecificationOption option : specificationOptionList) {
			option.setSpecId(tbSpecification.getId());
			specificationOptionMapper.insert(option);
		}
	}

	@Override
	public Specification findOne(Long id) {
		if (id != null) {
			// 查询规格名称
			TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);

			// 查询规格信息
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(id);
			List<TbSpecificationOption> specificationOptionList = specificationOptionMapper.selectByExample(example);

			// 封装规格
			Specification specification = new Specification();
			specification.setSpecification(tbSpecification);
			specification.setSpecificationOptionList(specificationOptionList);
			return specification;
		}
		return null;
	}

	@Override
	public void update(Specification specification) {
		// 获取并添加规格名称
		TbSpecification tbSpecification = specification.getSpecification();
		specificationMapper.updateByPrimaryKey(tbSpecification);
		
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(tbSpecification.getId());
		specificationOptionMapper.deleteByExample(example);
		// 获取规格列表
		List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
		for (TbSpecificationOption option : specificationOptionList) {
			option.setSpecId(tbSpecification.getId());
			specificationOptionMapper.insert(option);
		}
	}

	@Override
	public void delete(String[] ids) {
		for(String i:ids) {
			Long id = Long.parseLong(i);
			//删除规格选项
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(id);
			specificationOptionMapper.deleteByExample(example);
			//删除规格
			specificationMapper.deleteByPrimaryKey(id);
		}
		
		
		
	}

	@Override
	public List<Map> findOptionList() {
		return specificationMapper.findOptionList();
	}

}
