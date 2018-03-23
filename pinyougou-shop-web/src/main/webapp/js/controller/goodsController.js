 //控制层 
app.controller('goodsController' ,function($scope,$controller ,$location  ,goodsService,uploadService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}

	$scope.returnhtml=function(id){
        location.href="../admin/goods_edit.html#?id="+id;
		}





//查询实体
    $scope.findOne1=function(){
		var id=$location.search()['id']
		if (id==null){
			return;
		}
        goodsService.findOne1(id).success(
            function(response){
                $scope.entity= response;
                //回显富文本
                editor.html($scope.entity.tbGoodsDesc.introduction)
				//回显图片
				$scope.entity.tbGoodsDesc.itemImages=JSON.parse($scope.entity.tbGoodsDesc.itemImages)
				$scope.entity.tbGoodsDesc.specificationItems=JSON.parse($scope.entity.tbGoodsDesc.specificationItems)
                $scope.entity.tbGoodsDesc.customAttributeItems=JSON.parse($scope.entity.tbGoodsDesc.customAttributeItems)
         		    //SKU
				for (var i=0;i<$scope.entity.itemList.length;i++){
                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec)
				}
            }
        );
    }


    //回显选中规格
	$scope.checkAttributeValue=function (specName,optionName) {
		var item=$scope.entity.tbGoodsDesc.specificationItems;
        var object = $scope.searchObjectByKey(item,"attributeName",specName)
		if (object == null){
			return false
		}else {
			if (object.attributeValue.indexOf(optionName)>=0){
				return true
			}else {
				return false
			}
		}
    }


	//保存 
	$scope.save=function() {
        var serviceObject;//服务层对象
       $scope.entity.tbGoodsDesc.introduction=editor.html();

        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {

            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {

                    alert("保存成功")
					editor.html("")
					$scope.entity={}
                } else {
                    alert(response.message);
                }
            }
        );
    }





    $scope.uploadFile=function(){
    		uploadService.uploadFile().success(
    			function(response){
                    if (response.success) {
                       $scope.image_entity.url=response.message
                    } else {
                        alert(response.message);
                    }
    			}
    		).error(function () {
				alert("上传发生错误")
            });
    	}


    $scope.entity={tbGoodsDesc:{itemImages:[],specificationItems:[]}}
//添加图片
    	$scope.add_image_entity=function(){
    			$scope.entity.tbGoodsDesc.itemImages.push($scope.image_entity)
    		}

    		//删除图片
	$scope.remive_image_entity=function(index){
        $scope.entity.tbGoodsDesc.itemImages.splice(index,1)

		}
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}


    $scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态

    $scope.itemCatList=[];//商品分类列表

	$scope.selectItemCatList=function(){
			itemCatService.findAll().success(
				function(response){
					for (var  i=0;i<response.length;i++){
                        $scope.itemCatList[response[i].id]=response[i].name;
					}
				}
			);
		}





//读取一级分类
	$scope.selectItemCat1List=function(){
        itemCatService.findByParentId(0).success(
				function(response){
			$scope.ItemCat1List=response
				}
			);
		}

//读取二级分类
	$scope.$watch('entity.goods.category1Id',function (newValue) {
        itemCatService.findByParentId(newValue).success(
            function(response){
                $scope.ItemCat2List=response
                $scope.ItemCat3List={}
            }
        );
    })



    //读取三级分类
    $scope.$watch('entity.goods.category2Id',function (newValue) {
        itemCatService.findByParentId(newValue).success(
            function(response){
                $scope.ItemCat3List=response
            }
        );
    })

//读取三级分类，模板ID
    $scope.$watch('entity.goods.category3Id',function (newValue) {
        itemCatService.findOne(newValue).success(
            function(response){
                $scope.entity.goods.typeTemplateId=response.typeId
            }
        );
    })
	
	//品牌下拉列表
	$scope.$watch('entity.goods.typeTemplateId',function(typeId){
			typeTemplateService.findOne(typeId).success(
				function(response){
					$scope.typeTemplateId=response
                    $scope.typeTemplateId.brandIds=JSON.parse( $scope.typeTemplateId.brandIds)
					if ($location.search()['id']==null){
                        $scope.entity.tbGoodsDesc.customAttributeItems=JSON.parse($scope.typeTemplateId.customAttributeItems)
					}

				}
			);
		})

	//规格复选框
	$scope.$watch('entity.goods.typeTemplateId',function(id){
			typeTemplateService.findSpecList(id).success(
				function(response){
					$scope.list=response
				}
			);
		})


	$scope.searchObjectByKey=function (list,key,value) {
		for (var i=0;i<list.length;i++){
			if (list[i][key]==value){
				return list[i]
			}
		}
		return null
    }





    $scope.updateSpecAttribute=function ($event,name,value) {
		var obj=$scope.searchObjectByKey(
            $scope.entity.tbGoodsDesc.specificationItems,"attributeName",name
		);

		if(obj!=null){
				if ($event.target.checked){//数组中已存在该对象，添加时向数组中添加规格选项即可
                    obj.attributeValue.push(value)
				}else{
                   var index= obj.attributeValue.indexOf(value)
                    obj.attributeValue.splice(index,1)
					if(obj.attributeValue.length==0){
                        $scope.entity.tbGoodsDesc.specificationItems.splice(
                            $scope.entity.tbGoodsDesc.specificationItems.indexOf(obj),1
						)
					}
				}
		}else{
            $scope.entity.tbGoodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]})
		}
    }


    $scope.createItemList=function(){
        var specItems = $scope.entity.tbGoodsDesc.specificationItems;//选中的数组

        $scope.entity.itemList=[{spec:{},price:0,num:99999,status:1,isDefault:0}];//初始化一条数据
        for(var i=0;i<specItems.length;i++){//循环的是规格

            $scope.entity.itemList=addColumn($scope.entity.itemList,specItems[i].attributeName,specItems[i].attributeValue);

        }

    }
    //1.上次生成的list；2本次规格的规格名称；3本次规格的规格选项
    addColumn=function(list,attributeName,attributeValue){

        var newList=[];
        for(var i=0;i<list.length;i++){
            var oldRow=list[i];
            for(var j=0;j<attributeValue.length;j++){
                var newRow=JSON.parse(JSON.stringify(oldRow));
                newRow.spec[attributeName]=attributeValue[j];

                newList.push(newRow);
            }
        }


        return newList;
    }


});



