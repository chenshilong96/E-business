app.service('cartService',function($http){
	
	this.findCartList = function(){
		return $http.get('cart/findCartList.do');
	}
	
	this.addGoodsToCartList = function(itemId,num){
		return $http.get('cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);
	}
	
	//获取地址列表
	this.findAddressList=function(){
		return $http.get('address/findAddressListByUserId.do');	
	}
	
	//提交并保存订单
	this.submitOrder = function(order){
		return $http.post('order/add.do',order);
	}

	//增加新收件人信息
	this.addNewAddress = function(newAddress){
		return $http.get('address/addNewAddress.do',newAddress);	
	}
	
	
	this.sum = function(cartList){
		
		var totalValue = {totalNum:0,totalMoney:0.00};
		
		for(var i=0;i<cartList.length;i++){
			
			var cart = cartList[i];
			
			for(var j=0;j<cart.orderItemList.length;j++){
				
				var orderItem = cart.orderItemList[j];
				
				totalValue.totalNum += orderItem.num;
				totalValue.totalMoney += orderItem.totalFee;
			}
		}
		
		return totalValue;
	}
	
});