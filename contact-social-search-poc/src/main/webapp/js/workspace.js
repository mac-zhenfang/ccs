/**
 * 1.ReportService 2.GroupService 3.AppService
 * 
 */

var Workspace = angular.module('Workspace', [ 'ui.bootstrap' ]);
// global value

Workspace.value("$WorkspaceHost", "http://nemo.sonarsource.org");
Workspace.value("$MaxPaneSize", 10);
Workspace.factory("$CategoryListURL", function($WorkspaceHost) {
	return $WorkspaceHost + "/api/resources";
});// Services

Workspace.factory("$AppListURL", function($WorkspaceHost) {
	return $WorkspaceHost + "/api/resources";
});// Services


// Controller
var WorkspaceController = function($scope, GroupService, AppService) {
	$scope.init = function() {
		// Get userId from cookies
		GroupService.listGroups($scope);

	};
	$scope.initPanesReports = function(group, scope) {
		scope.reports = {
			focusReport : {},
			rows : []
		};
		scope.panes = [{
			title : group.name
		}];
		var cols = [];
		for (rep in group.reports) {
			var report = group.reports[rep];
			if (report.isFocus == true) {
				scope.reports["focusReport"] = report;
			} else {
				if ((rep - 1) % 3 == 0) {
					cols = [];
					console.log(" rows " + scope.reports.rows.length);
					scope.reports.rows[scope.reports.rows.length] = {
						"cols" : cols
					};
				}
				cols[cols.length] = report;
			}
		}
	};
	$scope.initApps = function() {
		// Get userId from cookies
		AppService.listApps($scope);

	};
	$scope.panes = [];
	$scope.radioValue = [ " Last 1 day", "Last 1 Week", "Last 1 Month" ];

	// FIXME init the reports for 1 group
	// $scope.reports = {
	// focusReport : {
	// name : "Focus Report"
	// },
	// rows : [ {
	// cols : [ {
	// name : "report1"
	// }, {
	// name : "report12"
	// }, {
	// name : "report13"
	// } ]
	// }, {
	// cols : [ {
	// name : "report21"
	// }, {
	// name : "report22"
	// }, {
	// name : "report23"
	// } ]
	// }, {
	// cols : [ {
	// name : "report31"
	// }, {
	// name : "report32"
	// }, {
	// name : "report33"
	// } ]
	// } ]
	// };
	// AddPane
	$scope.addPane = function(index) {
		var group = $scope.groups[index];

		console.log(group);
		if ($scope.panes.length < 10) {
			$scope.initPanesReports(group, $scope);
			$scope.panes[$scope.panes.length - 1]["active"] = true;
		}

	};
};

Workspace.controller("WorkspaceContrl", WorkspaceController);
