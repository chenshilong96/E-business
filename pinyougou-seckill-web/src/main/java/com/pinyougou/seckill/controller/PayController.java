package com.pinyougou.seckill.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;

import entity.Result;

@RestController
@RequestMapping("/pay")
public class PayController {

	@Reference
	private SeckillOrderService seckillOrderService;
	
	@Reference
	private WeixinPayService weixinPayService;
	
	/**
	 * 生成二维码
	 * @return
	 */
	@RequestMapping("/createNative")
	public Map createNative() {
		//获取当前用户		
		String userId=SecurityContextHolder.getContext().getAuthentication().getName();
		//到redis查询秒杀订单
		TbSeckillOrder seckillOrder = seckillOrderService.searchSeckillOrderFromRedisByUserId(userId);
		//生成二维码
		if(seckillOrder!=null) {
			String out_trade_no = seckillOrder.getId()+""; //订单ID
			long total_fee = (long)(seckillOrder.getMoney().doubleValue()*100);
			Map map = weixinPayService.createNative(out_trade_no, total_fee+"");
			return map;
		}else {
			return new HashMap();
		}
	}
	
	/**
	 * 查询订单状态
	 * @param out_trade_no
	 * @return
	 */
	@RequestMapping("/queryPayStatus")
	public Result  queryPayStatus(String out_trade_no) {
		//获取用户ID
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		Result result = null;
		long x = 0;
		while(true) {
			Map map = weixinPayService.queryPayStatus(out_trade_no);
			if(map==null) {
				result=new  Result(false, "支付出错");
				break;
			} 
			if(map.get("trade_state").equals("SUCCESS")){ //支付成功
				result=new  Result(true, "支付成功");				
				seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id")+"");
				break;
			}
			
			try {
				Thread.sleep(3000);
				x++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(x>=100) {
				result=new  Result(true, "支付超时");	
				
				Map payResult = weixinPayService.closePay(out_trade_no);
				if(!payResult.get("result_code").equals("SUCCESS")) {
					if("ORDERPAID".equals(payResult.get("err_code"))){
						result=new Result(true, "支付成功");	
						seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id")+"");
					}	

				}
				
				if(result.isSuccess()==false){
					System.out.println("超时，取消订单");
					//2.调用删除
					seckillOrderService.deleteOrderFromRedis(userId, Long.valueOf(out_trade_no));	
				}

				
				break;
			}
			
		}
		
		return result;
	}
}
