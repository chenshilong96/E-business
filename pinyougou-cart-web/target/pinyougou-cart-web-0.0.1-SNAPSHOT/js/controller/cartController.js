app.controller('cartController',function($scope,cartService){
	
	$scope.newAddress = {};
	
	$scope.order = {paymentType:'1'};

	$scope.findCartList = function(){
		
		cartService.findCartList().success(
				function(response){
					$scope.cartList = response;
					$scope.totalValue = cartService.sum($scope.cartList);
				}
		)
	}
	
	$scope.addGoodsToCartList = function(itemId,num){
		cartService.addGoodsToCartList(itemId,num).success(
				function(response){
					if(response.success){
						$scope.findCartList();
					}else{
						alert(response.message);
					}
				}
		)
	}
	
	
	//获取地址列表
	$scope.findAddressList=function(){
		cartService.findAddressList().success(
			function(response){
				$scope.addressList=response;
				for(var i=0;i<$scope.addressList.length;i++){
					if($scope.addressList[i].isDefault=='1'){
						$scope.address = $scope.addressList[i];
					}
				}
			}		
		);		
	}
	
	//选择地址
	$scope.selectAddress = function(address){
		$scope.address = address;
	}
	
	//判断是否是当前选中的地址
	$scope.isSelectedAddress = function(address){
		if($scope.address == address){
			return true;
		}else{
			return false;
		}
	}
	
	//增加新收件人信息
	$scope.addNewAddress = function(){
		cartService.addNewAddress($scope.newAddress).success(
				function(response){
					if(response.success){
						alert(response.message);
					}else{
						alert(response.message);
					}
				}
		);
	}
	
	//选择支付方式
	$scope.selectPayType = function(type){
		$scope.order.paymentType = type;
	}
	
	//是否选择该支付方式
	$scope.isSelectedPayType = function(type){
		if($scope.order.paymentType==type){
			return true;
		}else{
			return false;
		}
	}
	
	//提交并保存订单
	$scope.submitOrder=function(){
		$scope.order.receiverAreaName=$scope.address.address;//地址
		$scope.order.receiverMobile=$scope.address.mobile;//手机
		$scope.order.receiver=$scope.address.contact;//联系人
		cartService.submitOrder( $scope.order ).success(
				function(response){
					if(response.success){
						//页面跳转
						if($scope.order.paymentType=='1'){//如果是微信支付，跳转到支付页面
							location.href="pay.html";
						}else{//如果货到付款，跳转到提示页面
							location.href="paysuccess.html";
						}					
					}else{
						alert(response.message);	//也可以跳转到提示页面				
					}				
				}				
			);

	}
	
});