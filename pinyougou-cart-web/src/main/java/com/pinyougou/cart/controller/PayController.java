package com.pinyougou.cart.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 支付控制层
 * @author csl
 *
 */

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.utils.IdWorker;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;

import entity.Result;
@RestController
@RequestMapping("/pay")
public class PayController {

	@Reference
	private WeixinPayService weixinService;
	@Reference
	private RedisTemplate redisTemplate;
	@Reference
	private OrderService orderService;
	
	/**
	 * 生成二维码
	 * @return
	 */
	@RequestMapping("/createNative")
	public Map createNative() {
		//1.获取登录用户
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		//2.查询支付日志
		TbPayLog payLog = orderService.searchPayLogFromRedis(userId);
		if(payLog!=null) { 
			//3.返回生成的二维码
			return weixinService.createNative(payLog.getOutTradeNo()+"", payLog.getTotalFee()+"");
		}
		return  new HashMap();
	}
	
	/**
	 * 查询交易状态
	 * @param out_trade_no
	 * @return
	 */
	@RequestMapping("/queryPayStatus")
	public Result queryPayStatus(String out_trade_no) {
		Result result=null; //标记结果
		int x = 0; //标记时间
		while(true) {
			Map map = weixinService.queryPayStatus(out_trade_no);
			
			//交易失败
			if(map==null) {
				result = new Result(false, "交易失败");
				break;
			}
			
			//交易成功
			if(map.get("trade_state").equals("SUCCESS")) {
				result = new Result(true,"交易成功");
				//修改订单状态
				orderService.updateOrderStatus(out_trade_no,  map.get("transaction_id")+"");

				break;
			}
			
			try {
				Thread.sleep(3000);  //每隔3秒执行一次
				x++;
				if(x>100) {
					result = new Result(true,"交易超时");
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
