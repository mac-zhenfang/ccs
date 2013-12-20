// declare a module
var Css = angular.module('Css', []);
// global value
// FIXME services
var relationUri = window.location.toString();
relationUri = relationUri.substring(0, relationUri.lastIndexOf("/"))
		+ "/v1/relations/";
var queryUri = window.location.toString();
var queryUri = queryUri.substring(0, queryUri.lastIndexOf("/")) + "/v1/query/";
var personUri = window.location.toString();
var personUri = personUri.substring(0, personUri.lastIndexOf("/"))
		+ "/v1/persons/";
// declare UserService

var RelationCtrl = function($scope, $location, $window) {

	$scope.findPerson = function(userName) {
		var callUri = personUri + userName;
		var html = "";
		$.getJSON(callUri, {
			param : "test"
		}, function(data) {
			console.log(JSON.stringify(data));
			$.each(data, function(idx, item) {
				html = html + "<div class ='span2'> User Name : "
						+ item.userName + "</div>";
				if (item.pictureUrl != null) {
					html = html + "<div><img src=" + item.pictureUrl
							+ "></img>";
				}
				html = html + "<div class='span2'>ID : " + item.id + "</div>";
			});
			console.log(html);
			document.getElementById("pie").innerHTML = html;
		});
	};
	$scope.findRelatedPerson = function(queryStr) {
		var callUri = queryUri + queryStr;

		$.getJSON(callUri, {
			param : "test"
		}, function(data) {
			console.log(JSON.stringify(data));
			var html = "<div class ='span2'> User Name : " + data[0].userName
					+ "</div><div><img src=" + data[0].pictureUrl + "></img>"
					+ "<div class='span2'>ID : " + data[0].id + "</div>";
			console.log(html);
			document.getElementById("pie").innerHTML = html;
		});
	};
	$scope.findRelation = function(id) {
		var callUri = relationUri + id;
		console.info(callUri);
		var width = 1024, height = 968;

		var color = d3.scale.category20();

		var force = d3.layout.force().charge(-120).linkDistance(300).size(
				[ width, height ]);
		$("#pie").innerHTML = "";
		document.getElementById("pie").innerHTML = "";
		var svg = d3.select("#pie").append("svg").attr("width", width).attr(
				"height", height);
		var graph = {};
		graph["nodes"] = [];
		graph["links"] = [];
		var thingMap = {};
		var nodeIdx = 0;
		var linkIdx = 0;
		$.getJSON(callUri, {
			param : "test"
		}, function(data) {
			$.each(data, function(idx, item) {
				var node = {};
				// console.log("idx_"+idx);
				node["name"] = item.person.userName + "_" + nodeIdx;
				if (id == item.person.id) {
					node["group"] = 4;
				} else {
					node["group"] = 3;
				}

				graph["nodes"].push(node);
				// linkIdx=0;
				var nodeTarget = nodeIdx;
				// console.log("nodeTarget: " + nodeTarget + " Node Name: "
				// + node["name"]);
				nodeIdx++;

				linkIdx = nodeIdx;
				$.each(item.things, function(thingId, thing) {
					if (thingMap[thingId] == null
							|| typeof thingMap[thingId] == undefined) {
						var thingNode = {};
						thingNode["name"] = thing.thingDisplayName + "_"
								+ thing.id + "_" + nodeIdx;
						thingNode["group"] = 2;
						graph["nodes"].push(thingNode);
						thingMap[thingId] = linkIdx;
						var link = {};
						if (linkIdx != nodeTarget) {
							link["source"] = linkIdx;
							link["target"] = nodeTarget;
							link["value"] = 1;
							// console.log(link);
							graph["links"][graph["links"].length] = link;
						}
						linkIdx++;
					} else {
						var link = {};
						if (linkIdx != nodeTarget) {
							link["source"] = thingMap[thingId];
							link["target"] = nodeTarget;
							link["value"] = 1;
							// console.log(link);
							graph["links"][graph["links"].length] = link;
						}
						// console.log(" Find same " + thingId);
					}
				});
				nodeIdx = linkIdx;
			});
			// console.log(JSON.stringify(thingMap));
			// console.log(JSON.stringify(graph))

			/**/
			force.nodes(graph.nodes).links(graph.links).start();

			var link = svg.selectAll(".link").data(graph.links).enter().append(
					"line").attr("class", "link").style("stroke-width",
					function(d) {
						return Math.sqrt(d.value);
					});

			var node = svg.selectAll(".node").data(graph.nodes).enter().append(
					"circle").attr("class", "node").attr("r", 5).style("fill",
					function(d) {
						return color(d.group);
					}).call(force.drag);

			node.append("title").text(function(d) {
				return d.name;
			});

			force.on("tick", function() {
				link.attr("x1", function(d) {
					return d.source.x;
				}).attr("y1", function(d) {
					return d.source.y;
				}).attr("x2", function(d) {
					return d.target.x;
				}).attr("y2", function(d) {
					return d.target.y;
				});

				node.attr("cx", function(d) {
					return d.x;
				}).attr("cy", function(d) {
					return d.y;
				});
			});

		});
	};

};

Css.controller("RelationCtrl", RelationCtrl);
