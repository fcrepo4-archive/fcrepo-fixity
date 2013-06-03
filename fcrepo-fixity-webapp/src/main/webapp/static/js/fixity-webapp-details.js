function createPieChart(numSuccesses, numErrors) {
	$('#piechart').empty();
	var r = Raphael("piechart");
	var chart_data = new Array();
	var colors = new Array();
	var legend = new Array();
	var count = 0;
	if (numSuccesses > 0) {
		chart_data[count] = numSuccesses;
		legend[count] = '%%.%% - Success';
		colors[count++] = '#00ff00';
	}
	if (numErrors > 0) {
		chart_data[count] = numErrors;
		legend[count] = '%%.%% - Error';
		colors[count++] = '#ff0000';
	}
	pie = r.piechart(290, 190, 120, chart_data, {
		legend : legend,
		legendpos : "east",
		colors : colors
	});
	label = r.label(128, 40, "Overall errors vs. successes");
}

function initDetails() {
	var numErrors = 0;
	var numSuccesses = 0;

	$.extend({
		getUrlVars : function() {
			var vars = [], hash;
			var hashes = window.location.href.slice(
					window.location.href.indexOf('?') + 1).split('&');
			for ( var i = 0; i < hashes.length; i++) {
				hash = hashes[i].split('=');
				vars.push(hash[0]);
				vars[hash[0]] = hash[1];
			}
			return vars;
		},
		getUrlVar : function(name) {
			return $.getUrlVars()[name];
		}
	});

	$('#title').html(
			'Fedora 4 - Fixity Check Details for ' + $.getUrlVar("pid"));

	$.getJSON('../fixity-results/details/' + $.getUrlVar("id"), function(data) {

		var aaData = new Array();
		counter = 0;

		if (!$.isEmptyObject(data['errors'])) {
			errorData = data['errors'];

			$.each(errorData, function(key, value) {
				var row = new Array();
				numErrors++;
				row[0] = value['resultId'];
				row[1] = value['uri'];
				row[2] = value['type'];
				aaData[counter++] = row;
			});
		}

		if (!$.isEmptyObject(data['successes'])) {
			successData = data['successes'];
				
			$.each(successData, function(key, value) {
				var row = new Array();
				console.log(value);
				numSuccesses++;
				row[0] = value['resultId'];
				row[1] = value['uri'];
				row[2] = value['type'];
				aaData[counter++] = row;
			});
		}

		$('#details').dataTable({
			aaData : aaData,
			iDisplayLength : 25,
			bJQueryUI : true,
			aoColumns : [ {
				sWwidth : "5%"
			}, {
				sWidth : "65%"
			}, {
				sWidth : "30%"
			}]
		});
		createPieChart(numSuccesses, numErrors);
	});
}