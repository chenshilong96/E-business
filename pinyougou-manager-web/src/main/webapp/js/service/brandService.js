app.service("brandService",function($http){
		
		//查询所有品牌
		this.findAll = function(){
			return $http.get('../brand/findAll.do');
		}
		
		//分页查询
		this.search = function(page,rows,searchEntity){
			return $http.post('../brand/search.do?page='+page+'&rows='+rows,searchEntity);
		}
		
		//添加品牌
		this.add = function(entity){
			return $http.post('../brand/addBrand.do',entity);
		}
		
		//更新品牌
		this.update = function(entity){
			return $http.post('../brand/updateBrand.do',entity);
		}
		
		//根据ID查询品牌
		this.findOne = function(id){
			return $http.get('../brand/findById.do?id='+id);
		}
		
		//删除品牌
		this.delete = function(ids){
			return $http.get('../brand/deleteBrand.do?ids='+ids);
		}
	});