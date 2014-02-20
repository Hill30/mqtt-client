angular.module('application').controller('activityRecordEditorController', [
    '$scope', '$log', '$resource', '$location', function($scope, console, $resource, $location) {

        var id = $location.path().substring(10);

        var Record = $resource('api/activity/:id');
        $scope.record = Record.get({'id':id});

        $scope.cancel = function() {
                $location.path("activities");
            }

        $scope.update = function() {
                $scope.record.$save();
                $location.path("activities");
            }
}])
