angular.module('application', ['ngResource'])
.config(['$provide', function($provide){
    if (window.WebApi)
        $provide.provider('$httpBackend',
            function() {
                this.$get = ['$browser', '$log', function($browser) {
                    return function(method, url, post, callback, headers, timeout, withCredentials) {
                        try {
                            callback(200, window.WebApi.get(url));
                        } catch (exception) {
                            console.log("exception: " + exception.toString());
                            // todo: extract more info from the exception object
                            callback(500, exception);
                        }
                    }
                }]
            });
}])
.run(['$rootScope', '$log', '$resource',
    function($rootScope, console, $resource) {

        var List = $resource('webapi/list/:name', {name:'branches'});
        $rootScope.list = List.query();

    }]);
