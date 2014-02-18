angular.module('application', ['ngResource'])
.config(['$provide', function($provide){
    if (window.WebApi)
        $provide.provider('$httpBackend',
            function() {
                this.$get = ['$browser', '$log', function($browser) {
                    return function(method, url, post, callback, headers, timeout, withCredentials) {
                        callback(200, window.WebApi.get(url));
                    }
                }]
            });
}])
.run(['$rootScope', '$log', '$resource',
    function($rootScope, console, $resource) {

        var List = $resource('webapi/list/:name', {name:'branches'});
        $rootScope.list = List.query();

    }]);
