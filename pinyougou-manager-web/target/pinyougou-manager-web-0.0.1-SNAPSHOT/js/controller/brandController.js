app.controller('brandController', function($scope, $http,brandService) {

		//查询所有
		$scope.findAll = function() {
			brandService.findAll().success(function(response) {
				$scope.list = response;
			});
		};
		
		//分页查询
		$scope.paginationConf = {
				 currentPage: 1,
				 totalItems: 5,
				 itemsPerPage: 5,
				 perPageOptions: [10, 20, 30, 40, 50],
				 onChange: function(){
					 $scope.reloadList();
				 }
		}; 
		
		$scope.reloadList = function(){
			$scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
		}
		
		$scope.searchEntity = {};
		//状态改变要执行的函数
		$scope.search = function(page,rows){
			brandService.search(page,rows,$scope.searchEntity).success(
				
					function(response){
						$scope.paginationConf.totalItems = response.total;
						$scope.list = response.rows;
					}
				
			);
		}
		
		//保存或更新商品信息
		$scope.save = function(){
			
			
			
			var object = null;
			if($scope.entity.id!=null){
				object = brandService.update($scope.entity);
			}else{
				object = brandService.add($scope.entity);
			}
			
			object.success(
				function(response){
					if(response.success){
						$scope.reloadList();
					}else{
						alert(response.message);
					}
				}		
			);
		}

		//根据ID查询
		$scope.findOne = function(id){
			
			brandService.findOne(id).success(
					function(response){
						$scope.entity = response;
						
					}
			);
		}
		
		//获取要删除的商品ID
		$scope.selectedIds = [];
		$scope.updateSelection = function($event,id){
			if($event.target.checked){
				//选中的ID
				$scope.selectedIds.push(id);
			}else{
				//取消选中的ID
				var index = $scope.selectedIds.indexOf(id);
				$scope.selectedIds.splice(index,1);
			}
		}
		
		//删除对应ID商品
		$scope.delete = function(){
			
			//提示是否确认删除
			if(window.confirm("您确定要删除吗?")){
				brandService.delete($scope.selectedIds).success(
					function(response){
						if(response.success){
							$scope.reloadList();
						}else{
							alert(response.message);
						}
					}		
					
				);
			}
			
			
		}
		
	});