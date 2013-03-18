	var generalStats;
	var dailyStats = new Array();
	
	
	function updateStatistics(){
		$.getJSON('../rest/results/statistics/general', function (data) {
			generalStats = createStatistics(data);
			createPieChart();
		});

		$.getJSON('../rest/results/statistics/daily', function (data) {
			len = data.length;
			for (i=0;i<len;i++){
				dailyStats[i] = {
						date : data[i]['stat-daily']['@date'],
						successes : data[i]['stat-daily']['@successCount'],
						errors : data[i]['stat-daily']['@errorCount']
						repairs : data[i]['stat-daily']['@repairCount']
				}
			}
			createLineChart();
		});
	}

	function updateTable(){
		$.getJSON('../rest/results', function(data) {
			var aaData = new Array();
			counter = 0;
			$.each(data,function() {
				var row = new Array();
				row[0] = this['fixity-result']['@record-id'];
				row[1] = this["fixity-result"]['@pid'];
				row[2] = this['fixity-result']['@timestamp'];
				row[3] = (this["fixity-result"]['@success'] == "true") ? "Success" : (this["fixity-result"]['@repaired'] == "true") ? "Repaired" : "Error";
				aaData[counter++] = row;
			});

			$('#results').dataTable({
				aaData : aaData,
				iDisplayLength: 25,
				bJQueryUI : true,
				aoColumns : [ {sWwidth : "5%"},
				              {sWidth : "40%"},
				              {sWidth : "35%"},
				              {sWwidth : "15%"} ],
				fnCreatedRow : function (n_row, row_data, data_idx){
					$('td:eq(0)',n_row).parent().mouseover(function() {
						$(this).addClass('row_hover');
					});
					$('td:eq(0)',n_row).parent().mouseout(function() {
						$(this).removeClass('row_hover');
					});
				}
			});
		
			$('#results').delegate('tbody > tr > td', 'click', function () {
				record_id = $(this).parent().children()[0].innerText;
				window.location = 'details.html?id=' + record_id;
			});
		});
	}
	
	function queuePid(pid){
		var path = '../rest/results/queue';
		if (pid != "All Objects"){
			path = '../rest/results/queue?pid=' + pid;
		}
		$.post(path, function (data) {
			location.reload(true);
		});
	}
	
	function createStatistics(data) {
		generalStats = {
			numObjects : parseInt(data['general-stat']['@object-count']),
			numErrors : parseInt(data['general-stat']['@error-count']),
			numSuccesses : parseInt(data['general-stat']['@success-count'])
			numRepairs : parseInt(data['general-stat']['@repair-count'])
		}
		return generalStats;
	}

	function createLineChart() {
		$('#linechart').empty();
		r = Raphael("linechart");
		errorValues = new Array();
		successValues = new Array();
		repairValues = new Array();
		len = dailyStats.length;
		xValues = [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27];
		for (i = 0;i<len;i++){
			errorValues[i] = dailyStats[i].errors;
			successValues[i] = dailyStats[i].successes;
			repairValues[i] = dailyStats[i].repairs;
		}
		lines = r.linechart(60, 80, 500, 190 , xValues,[successValues,errorValues,repairValues], {
				axis : "0 0 1 1",
				shade: true,
				symbol: "circle",
				colors: ["#00ff00","#ff0000", '#ffff00'],
				axisxstep : 1
			});
		label = r.label(138,40,"Errors and successes per day");
	
	}
	function createPieChart() {
		$('#piechart').empty();
		var r = Raphael("piechart");
		var chart_data = new Array();
		chart_data[0] = generalStats.numSuccesses;
		if (generalStats.numErrors > 0) {
			chart_data[1] = generalStats.numErrors;
		}
		if (generalStats.numRepairs > 0) {
			chart_data[2] = generalStats.numRepairs;
		}
		pie = r.piechart(290, 190, 120, chart_data, {
			legend : [ "%%.%% - Success", "%%.%% - Error", "%%.%% - Repaired" ],
			legendpos : "east",
			colors : [ '#00ff00', '#ff0000', '#ffff00' ]
		});
		label = r.label(128,40,"Overall errors vs. successes");
	}
