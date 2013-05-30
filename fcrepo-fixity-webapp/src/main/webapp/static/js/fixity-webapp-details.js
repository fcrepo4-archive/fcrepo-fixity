	function createPieChart(numSuccesses, numErrors) {
		$('#piechart').empty();
		var r = Raphael("piechart");
		var chart_data = new Array();
		var colors = new Array();
		var legend = new Array();
		var count = 0;
		if (numSuccesses > 0){
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
		label = r.label(128,40,"Overall errors vs. successes");
	}
