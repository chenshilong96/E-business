app.service("specificationService",function($http){
	
	//查询所有
	this.findAll = function(){
		return $http.get('../specification/findAll.do');
	}
	
	//分页查询
	this.search = function(page,rows){
		return $http.post('../specification/findPage.do?page='+page+'&rows='+rows);
	}
	//根据ID查询
	this.findOne = function(id){
		return $http.get('../specification/findOne.do?id='+id);
	}
	//添加
	this.add = function(entity){
		return $http.post('../specification/add.do',entity);
	}
	//修改
	this.update = function(entity){
		return $http.post('../specification/update.do',entity);
	}
	//删除
	this.delete = function(selectedIds){
		return $http.get('../specification/delete.do?ids='+selectedIds);
	}
});