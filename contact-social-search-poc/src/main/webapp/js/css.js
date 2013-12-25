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
	$scope.findAllRelations = function() {
		var callUri = relationUri;
		$scope.internalFindRelation(callUri, null);
	};

	$scope.findRelation2 = function(from, to) {
		var callUri = relationUri + "from/" + from + "/to/" + to;
		$scope.internalFindRelation(callUri, [ from, to ]);
	};

	$scope.findRelatedPerson = function(queryStr) {

		var callUri = queryUri;
		if (null == queryStr || queryStr == "" || typeof queryStr == undefined) {

		} else {
			callUri = queryUri + queryStr;
		}

		$.getJSON(callUri, {
			param : "test"
		}, function(data) {
			//console.log(JSON.stringify(data));
			if (data.length > 0) {
				var html = "";
				$.each(data, function(idx, item) {

					html += "<div class='span12'><div class='span2'> User Name : "
							+ item.userName + "</div><div class='span2'><img src="
							+ item.pictureUrl + "></img></div>"
							+ "<div class='span4'>ID : " + item.id + "</div></div>";
					
				});
				console.log(html);
				document.getElementById("pie").innerHTML = html;
			} else {
				document.getElementById("pie").innerHTML = "no such person";
			}
		});
	};

	$scope.internalFindRelation = function(callUri, ids) {
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
				node["name"] = item.person.userName;
				if (ids != null) {
					// console.log(ids.length);
					for (var k = 0; k < ids.length; k++) {
						// console.log(ids[k]);
						var id = ids[k];
						if (id == item.person.id) {
							node["group"] = 4;
						} else {
							node["group"] = 3;
						}
					}
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
								+ thing.id;
						thingNode["group"] = 2;
						graph["nodes"].push(thingNode);
						thingMap[thingId] = linkIdx;
						var link = {};
						if (linkIdx != nodeTarget) {
							link["source"] = linkIdx;
							link["target"] = nodeTarget;
							link["value"] = 5;
							// console.log(link);
							graph["links"][graph["links"].length] = link;
						}
						linkIdx++;
					} else {
						var link = {};
						if (linkIdx != nodeTarget) {
							link["source"] = thingMap[thingId];
							link["target"] = nodeTarget;
							link["value"] = 5;
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
					"circle").attr("class", "node").attr("r", 10).style("fill",
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
	$scope.findRelation = function(id) {

		var callUri = relationUri + id;
		console.info(callUri);
		$scope.internalFindRelation(callUri, [ id ]);
	};
};

Css.controller("RelationCtrl", RelationCtrl);
