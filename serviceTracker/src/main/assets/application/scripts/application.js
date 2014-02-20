angular.module('application', ['ngResource', 'ngRoute'])
.config(['$provide', '$httpBackendProvider',
    function($provide, $httpBackendProvider){
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
}])
.run(['$rootScope', '$log', '$resource',
    function($rootScope, console, $resource) {

//        var List = $resource('webapi/list/:name', {name:'branches'});
//        $rootScope.list = List.query();

    }]);
