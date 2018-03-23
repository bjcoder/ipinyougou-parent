app.controller("searchController",function ($scope,searchService,$location) {
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':20};
    $scope.resultMap={categoryList:[]};



    $scope.loadkeywords=function(){
        $scope.searchMap.keywords= $location.search()['keywords']
        $scope.search();
    }

    $scope.search=function(){
        searchService.search( $scope.searchMap ).success(
            function(response){
                $scope.resultMap=response;//搜索返回的结果
                $scope.buildPageLabel()
            }
        );
    }


    $scope.keywordsIsBrand=function () {
        for (var i=0;i<$scope.resultMap.brandList;i++){
            if($scope.resultMap.brandList[i].text.indexOf($scope.searchMap.keywords)>0){
                alert(true)
                return true
            }
        }
        alert(false)
        return false
    }


    $scope.addSearchItem=function (key,value) {

        if (key=="category"||key=="brand"||key=='price'){
            $scope.searchMap[key]=value;

        }else {
            $scope.searchMap.spec[key]=value;
        }
        $scope.search()
    }

    $scope.removeSearchItem=function (key) {
        if (key=="category"||key=="brand"||key=='price'){
            $scope.searchMap[key]='';

        }else {
            delete $scope.searchMap.spec[key]
        }
        $scope.search()
    }
   
    
    $scope.buildPageLabel=function () {
        $scope.pageLabel=[];//构建工具条的页码数组
        var totalPages=$scope.resultMap.totalPages;
        var firstPage=1;
        var lastPage=totalPages;
        if (totalPages>5){
            if ($scope.searchMap.pageNo<3){
                lastPage=5
            }else if ($scope.searchMap.pageNo>totalPages-2){
                firstPage=totalPages-4
            }else {
                firstPage=$scope.searchMap.pageNo-2
                lastPage=$scope.searchMap.pageNo+2
            }
        }

        for(var i=firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i)
        }


    }


    $scope.queryByPage=function (pageNo) {
        if (pageNo<1||pageNo>$scope.resultMap.totalPages){
            return
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search()
    }

    $scope.returnPath=function (goodsId) {
        location.href="http://localhost:9106/"+goodsId+".html";
    }
})