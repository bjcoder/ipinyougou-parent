//定义控制器
app.controller('brandController', function($scope,$controller, $http, brandService) {

    $controller('baseController',{$scope:$scope});

    // 查询品牌列表
    $scope.findAll = function() {

        brandService.findAll().success(function(response) {
            $scope.list = response;

        });
    }

    // 分页查询
    $scope.findPage = function(page, rows) {
        brandService.findPage(page, rows).success(function(response) {
            $scope.list = response.rows;// 列表数据
            $scope.paginationConf.totalItems = response.total;// 总条数
        });
    }

    $scope.searchEntity = {};

    // 条件分页查询
    $scope.search = function(page, rows) {
        brandService.search(page, rows, $scope.searchEntity).success(
            function(response) {
                $scope.list = response.rows;// 列表数据
                $scope.paginationConf.totalItems = response.total;// 总条数
            });
    }



    // 查询品牌
    $scope.findOne = function(id) {
        brandService.findOne(id).success(function(response) {
            $scope.entity = response;
        });
    }

    // 新增品牌
    $scope.save = function() {
        var serviceObject;
        if ($scope.entity.id != null) {// 如果对象id不为空说明是修改操作
            serviceObject = brandService.update($scope.entity);
            alert(1);
        } else {
            serviceObject = brandService.add($scope.entity);
        }
        serviceObject.success(function(response) {
            if (response.success) {
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        });
    }



    // 批量删除
    $scope.dele = function() {
        brandService.dele($scope.selectIds).success(
            function(response) {
            if (response.success) {
                $scope.reloadList();
                $scope.selectIds = [];
            } else {
                alert(response.message);
            }
        });
    }



});