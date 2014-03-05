if (!window.WebApi)
    window.WebApi = {
        get: function(url) {
            switch (url) {
                case "activities":
                    return [{id:0, payload: {clientName:'John Smith', date: '02/10/2014', started: '12:00 pm', ended: '13:00 pm', distance: '20'}}];
                default:
                    return {id:0, payload:{clientName:'John Smith', date: '02/10/2014', started: '12:00 pm', ended: '13:00 pm', distance: '20'}};
            }
        }
    }

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
                                    switch (method.toLowerCase()) {
                                        case "post" :
                                            callback(200, window.WebApi.post(url.substring(4), post));
                                            break;
                                        default:
                                            callback(200, window.WebApi.get(url.substring(4)));
                                            break;
                                    }
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
}]);

angular.module('application')
.service('NotificationService', ['$rootScope', '$log', function($rootScope, $log) {
    var service = {
        newRecord:
            function(record) {
                $rootScope.$broadcast('newRecord', record);
                }
        };
    window.WebApi["NotificationService"] = service;
    return service;
}]);