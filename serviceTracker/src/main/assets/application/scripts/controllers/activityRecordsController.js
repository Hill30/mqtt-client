angular.module('application').controller('activityRecordsController', [
    '$scope', '$log', '$resource', '$location', function($scope, console, $resource, $location) {
        var Records = $resource('api/activityRecords');

        $scope.records = Records.query();

        $scope.currentActivityRecord = function(id) {
                $location.path("activitiy/" + id);
            }
}])
