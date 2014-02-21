angular.module('application').controller('activityRecordsController', [
    '$scope', '$log', '$resource', '$location', 'NotificationService', function($scope, console, $resource, $location) {
        var Records = $resource('api/activities');

        $scope.records = Records.query();

        $scope.select = function(id) {
                $location.path("activity/" + id);
            };

        $scope.$on('newRecord', function(event, record) {
                console.log(record);
                $scope.records.push(angular.fromJson(record));
                $scope.$apply();
            }
        );
}])
