/**
 * Created by a2363196581 on 2018/3/8.
 */
app.controller('baseController',function($scope){
    // 分页控件配置
    $scope.paginationConf = {
        currentPage : 1,
        totalItems : 10,
        itemsPerPage : 10,
        perPageOptions : [ 10, 20, 30, 40, 50 ],
        onChange : function() {
            $scope.reloadList();
        }
    };

    // 刷新页面
    $scope.reloadList = function() {
        $scope.search($scope.paginationConf.currentPage,
            $scope.paginationConf.itemsPerPage);
    }

    $scope.selectIds = [];// 选中ID集合

    // 操作id集合
    $scope.updateSelection = function($event, id) {
        if ($event.target.checked) {
            $scope.selectIds.push(id);
        } else {
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index, 1);
        }

    }

    $scope.jsonToString=function(jsonString,key){
        var json = JSON.parse(jsonString);
        var value= '';
        for(var i=0;i<json.length;i++){
            if(i>0){
                value+=',';
            }
            value+=json[i][key]
        }


        return value;
    }



})