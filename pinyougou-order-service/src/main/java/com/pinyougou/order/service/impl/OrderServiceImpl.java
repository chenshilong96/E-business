package com.pinyougou.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.common.utils.IdWorker;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojogroup.Cart;

import entity.PageResult;

/**
 * 服务实现层
 * 
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private IdWorker idWorker;
	@Autowired
	private TbPayLogMapper payLogMapper;

	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {
		// 获取购物车数据
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cart").get(order.getUserId());
		List<String> orderIdList = new ArrayList();// 订单ID列表
		double total_money = 0;// 总金额 （元

		for (Cart cart : cartList) {
			long orderId = idWorker.nextId();
			System.out.println("sellerId:" + cart.getSellerId());
			TbOrder tborder = new TbOrder();// 新创建订单对象
			tborder.setOrderId(orderId); // 订单ID
			tborder.setPaymentType(order.getPaymentType());
			tborder.setStatus("1"); // 订单状态 1未付款 2已付款
			tborder.setCreateTime(new Date()); // 订单创建时间
			tborder.setUpdateTime(new Date()); // 订单更新时间
			tborder.setUserId(order.getUserId()); // 用户ID
			tborder.setReceiverAreaName(order.getReceiverAreaName()); // 收获地址
			tborder.setReceiverMobile(order.getReceiverMobile()); // 联系人电话
			tborder.setReceiver(order.getReceiver()); // 收件人
			tborder.setSourceType(order.getSourceType()); // 订单来源 2 pc端 1app端
			tborder.setSellerId(cart.getSellerId()); // 商家id

			double money = 0;
			// 循环购物车明细
			for (TbOrderItem orderItem : cart.getOrderItemList()) {
				orderItem.setId(idWorker.nextId());
				orderItem.setOrderId(orderId); // 订单ID
				orderItem.setSellerId(cart.getSellerId()); // 商家ID
				money += orderItem.getTotalFee().doubleValue(); // 金额累加 求总花费金额
				// 保存购物车明细
				orderItemMapper.insert(orderItem);
			}
			orderIdList.add(orderId + "");// 添加到订单列表
			total_money += money; // 累加到总金额
			tborder.setPayment(new BigDecimal(money)); // 订单金额
			// 保存订单
			orderMapper.insert(tborder);
		}

		// 如果是微信支付
		if ("1".equals(order.getPaymentType())) {
			TbPayLog payLog = new TbPayLog();
			String outTradeNo = idWorker.nextId() + "";// 支付订单号
			payLog.setOutTradeNo(outTradeNo);// 支付订单号
			payLog.setCreateTime(new Date());// 创建时间
			// 订单号列表，逗号分隔
			String ids = orderIdList.toString().replace("[", "").replace("]", "").replace(" ", "");
			payLog.setOrderList(ids);// 订单号列表，逗号分隔
			payLog.setPayType("1");// 支付类型
			payLog.setTotalFee((long) (total_money * 100));// 总金额(分)
			payLog.setTradeState("0");// 支付状态
			payLog.setUserId(order.getUserId());// 用户ID
			payLogMapper.insert(payLog);// 插入到支付日志表
			redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);// 放入缓存

		}

		// 清楚redis缓存
		redisTemplate.boundHashOps("cart").delete(order.getUserId());

	}

	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order) {
		orderMapper.updateByPrimaryKey(order);
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id) {
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			orderMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbOrderExample example = new TbOrderExample();
		Criteria criteria = example.createCriteria();

		if (order != null) {
			if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
				criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
			}
			if (order.getPostFee() != null && order.getPostFee().length() > 0) {
				criteria.andPostFeeLike("%" + order.getPostFee() + "%");
			}
			if (order.getStatus() != null && order.getStatus().length() > 0) {
				criteria.andStatusLike("%" + order.getStatus() + "%");
			}
			if (order.getShippingName() != null && order.getShippingName().length() > 0) {
				criteria.andShippingNameLike("%" + order.getShippingName() + "%");
			}
			if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
				criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
			}
			if (order.getUserId() != null && order.getUserId().length() > 0) {
				criteria.andUserIdLike("%" + order.getUserId() + "%");
			}
			if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
				criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
			}
			if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
				criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
			}
			if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
				criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
			}
			if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
				criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
			}
			if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
				criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
			}
			if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
				criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
			}
			if (order.getReceiver() != null && order.getReceiver().length() > 0) {
				criteria.andReceiverLike("%" + order.getReceiver() + "%");
			}
			if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
				criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
			}
			if (order.getSourceType() != null && order.getSourceType().length() > 0) {
				criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
			}
			if (order.getSellerId() != null && order.getSellerId().length() > 0) {
				criteria.andSellerIdLike("%" + order.getSellerId() + "%");
			}

		}

		Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public TbPayLog searchPayLogFromRedis(String userId) {

		return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
	}

	@Override
	public void updateOrderStatus(String out_trade_no, String transaction_id) {
		// 1.修改支付日志状态
		TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
		payLog.setPayTime(new Date());
		payLog.setTradeState("1");// 已支付
		payLog.setTransactionId(transaction_id);// 交易号
		payLogMapper.updateByPrimaryKey(payLog);
		// 2.修改订单状态
		String orderList = payLog.getOrderList();// 获取订单号列表
		String[] orderIds = orderList.split(",");// 获取订单号数组

		for (String orderId : orderIds) {
			TbOrder order = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
			if (order != null) {
				order.setStatus("2");// 已付款
				orderMapper.updateByPrimaryKey(order);
			}
		}
		
		//清除redis缓存数据		
		redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());


	}

}
