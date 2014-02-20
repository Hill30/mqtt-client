angular.module('application', ['ngResource', 'ngRoute'])
.config(['$provide', '$routeProvider', '$httpBackendProvider',
    function($provide, $routeProvider, $httpBackendProvider){
        if (window.WebApi) {
            var originalProvider = $httpBackendProvider;
            $provide.provider('$httpBackend',
                function() {
                    this.$get = ['$browser', '$log', '$injector', function($browser, console, $injector) {
                        var originalBackend = $injector.invoke(originalProvider.$get);
                        return function(method, url, post, callback, headers, timeout, withCredentials) {
                            if (url.substring(0, 4) == "api/")
                                try {
                                    callback(200, window.WebApi[method.toLowerCase()](url, post));
                                } catch (exception) {
                                    // todo: extract more info from the exception object
                                    callback(500, exception);
                                }
                            else
                                originalBackend(method, url, post, callback, timeout, withCredentials);
                        }
                    }]
                });
            }
    $routeProvider
        .when("/activities",
            {
                templateUrl: "views/activityRecords.html"
            })
        .when("/activities/:id",
            {
                templateUrl: "views/activityRecords.html"
            })
        .otherwise(
            {
                redirectTo: "/activities"
            });
}])
.controller('activityRecordsController', [
    '$log', function(console) {
        console.log();
}])
.run(['$rootScope', '$log', '$resource',
    function($rootScope, console, $resource) {

//        var List = $resource('webapi/list/:name', {name:'branches'});
//        $rootScope.list = List.query();

    }]);
