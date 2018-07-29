app.controller('specificationController',function($scope,$http,specificationService){
		    	// 查询全部规格
		    	$scope.findAll = function(){
		    		specificationService.findAll().success(
	    				function(response){
		    				alert(response);
		    				$scope.list = response;
		    			}
		    		);
		    	}
		    			    	
		    	// 分页条件查询规格
		    	$scope.paginationConf = {
						 currentPage: 1,
						 totalItems: 5,
						 itemsPerPage: 3,
						 perPageOptions: [10, 20, 30, 40, 50],
						 onChange: function(){
							 $scope.reloadList();
						 }
				};
		    	$scope.reloadList = function(){
		    		$scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
		    	}
		    	
		    	$scope.search = function(page,rows){
		    		specificationService.search(page,rows).success(
		    			function(response){
		    				$scope.paginationConf.totalItems = response.total;
		    				$scope.list = response.rows;
		    			}
		    		);
		    	}
		    	
		    	$scope.entity = {specification:{},specificationOptionList:[]};
		    	// 新增规格选项行
		    	$scope.addTableRow = function(){
		    		$scope.entity.specificationOptionList.push({});
		    	}
		    	// 删除规格选项行
		    	$scope.deleteTableRow = function(index){
		    		$scope.entity.specificationOptionList.splice(index,1);
		    	}
		    	
		    	// 根据ID查询商品
		    	
		    	$scope.findOne = function(id){
		    		specificationService.findOne(id).success(
	    				function(response){
	    					$scope.entity.specification = response.specification;
	    					$scope.entity.specificationOptionList = response.specificationOptionList;
	    				}	
		    		);
		    	}
		   
		    	// 添加或修改规格
		    	var object = null;
		    	$scope.save = function(){
		    		
		    		if($scope.entity.specification.id!=null){
		    			// 修改商品
		    			object = specificationService.update($scope.entity);
		    		}else{
		    			// 添加商品
		    			object = specificationService.add($scope.entity);
		    		}
		    		
		    		object.success(
		   				function(response){
		    				if(response.success==true){
		    					$scope.reloadList();
		    				}else{
		    					alert(response.message);
		    				}
		    		});
		    		$scope.entity = {specification:{},specificationOptionList:[]};
		    	}
		    	
		    	// 拼接要删除规格的ID
		    	$scope.selectedIds = [];
		    	$scope.updateSelection = function($event,id){
	    			if($event.target.checked){
	    				$scope.selectedIds.push(id);
	    			}else{
	    				var index = $scope.selectedIds.indexOf(id);
	    				$scope.selectedIds.splice(index,1);
	    			}
	    		}
		    	// 根据ID删除规格
		    	$scope.delete = function(){
		    		specificationService.delete($scope.selectedIds).success(
		    			function(response){
		    				if(response.success){
		    					$scope.reloadList();
		    				}else{
		    					alert(response.message);
		    				}
		    				$scope.selectedIds = [];
		    			}
		    		);
		    	}
		    	
		    });