angular.module('application').controller('activityRecordsController', [
    '$scope', '$log', '$resource', '$location', function($scope, console, $resource, $location) {
        var Records = $resource('api/activities');

        $scope.records = Records.query();

        $scope.select = function(id) {
                $location.path("activity/" + id);
            }
}])
