angular.module('application').controller('activityRecordsController', [
    '$scope', '$log', '$resource', '$location', function($scope, console, $resource, $location) {
        var Record = $resource('api/activityRecords/:id');

        $scope.record = Records.get({id:1});

        $scope.cancel = function() {
                $location.path("activities");
            }

        $scope.update = function() {
                $location.path("activities");
            }
}])
