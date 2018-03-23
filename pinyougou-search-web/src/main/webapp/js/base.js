/**
 * Created by a2363196581 on 2018/3/8.
 */
var app=angular.module('pinyougou',[]);
app.filter('trustHtml',['$sce',function($sce){
    return function(data){
        return $sce.trustAsHtml(data);
    }
}]);