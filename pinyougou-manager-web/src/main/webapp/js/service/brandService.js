//定义服务
app.service('brandService',
    function($http) {
        // 查询品牌列表
        this.findAll = function() {
            return $http.get('../brand/findAll.do');
        }
        // 分页查询
        this.findPage = function(page, rows) {
            return $http.get('../brand/findPage.do?page=' + page + '&rows='
                + rows);
        }
        // 条件分页查询
        this.search = function(page, rows, entity) {
            return $http.post('../brand/search.do?page=' + page + '&rows='
                + rows, entity)
        }
        // 查询某个品牌
        this.findOne = function(id) {
            return $http.get('../brand/findOne.do?id=' + id);
        }

        // 批量删除
        this.dele = function(ids) {
            return $http.get('../brand/delete.do?ids=' + ids);
        }
        // 添加品牌
        this.add = function(entity) {
            return $http.post('../brand/add.do', entity);
        }
        // 修改品牌
        this.update = function(entity) {
            alert(2);
            return $http.post('../brand/update.do', entity);

        }
        //查询品牌下拉列表
        this.findBrandList=function(){
            return $http.get('../brand/findBrandList.do');
        }

        this.brandService=function () {
            return $http.get('../brand/selectOptionList.do');
        }
    })