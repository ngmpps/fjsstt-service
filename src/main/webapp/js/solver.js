function initSolver()  {
    $("[id^='post']").click(function(event) {
        var targetId = event.target.id.split("_")[1];
        var fjsData = $("#fjs_"+targetId)[0].innerText;
        if ($("#fjs_"+targetId)[0].type === "textarea") {
            fjsData += $("#fjs_"+targetId)[0].value;
        }
        var transportData = $("#transport_"+targetId)[0].innerText;
        if ($("#transport_"+targetId)[0].type === "textarea") {
            transportData += $("#transport_"+targetId)[0].value;
        }
        var propertiesData = $("#properties_"+targetId)[0].innerText;
        if ($("#properties_"+targetId)[0].type === "textarea") {
            propertiesData += $("#properties_"+targetId)[0].value;
        }

        $.ajax({
              type: "POST",
              url: "rest/solver/solution",
              data: JSON.stringify({ fjs: fjsData, transport: transportData, properties: propertiesData }),
              success: function(data,status,xhr){
                $("#output_"+targetId).text(JSON.stringify(data.solution,null, 2));
                // text is required if return values contain no proper html
                $("#status_"+targetId).text(status);
                $("#name_"+targetId).text(data.name);
                $("#problem_"+targetId).text(data.problemId);
                $("#bounds_"+targetId).text("[ " + data.minUpperBoundSolution + " : " + data.maxLowerBoundSolution + " ]");
              },
              error: function (jqXHR, status, errorThrown){
                // text is required if return values contain no proper html
                $("#output_"+targetId).text("");
                // text is required if return values contain no proper html
                $("#status_"+targetId).text(status + ": " + errorThrown);
              },
              contentType: "application/json"
            });
    });
}

function initSolver2()  {
    $("#get_running").click(function(event) {

        $.ajax({
              type: "GET",
              url: "rest/solver/statusrunning",
              success: function(data,status,xhr){
            	  //var json = $.parseJSON(data);
            	  var html = "<td colspan='2'>"
            	  for (var i=0; i<data.length; i++) {
                    html += data[i].name + " (" +  data[i].problemId + ") ["
                    html += data[i].maxLowerBoundSolution+";"+data[i].minUpperBoundSolution+"]<br/>"
            	  }
            	  html += "<td>"
            	  $("#results_running").html(html);
              },
              error: function (jqXHR, status, errorThrown){
                // text is required if return values contain no proper html
                $("#results_running").text(status + ": " + errorThrown);
              },
              contentType: "application/json"
            });
    });
    $("#get_finished").click(function(event) {

        $.ajax({
              type: "GET",
              url: "rest/solver/statusfinished",
              success: function(data,status,xhr){
            	  //var json = $.parseJSON(data);
                  var html = "<td colspan='2'>"
                  for (var i=0; i<data.length; i++) {
                    html += data[i].name + " (" +  data[i].problemId + ") ["
                    html += data[i].maxLowerBoundSolution+";"+data[i].minUpperBoundSolution+"]<br/>"
                  }
                  html += "<td>"
            	  $("#results_finished").html(html);
              },
              error: function (jqXHR, status, errorThrown){
                // text is required if return values contain no proper html
                $("#results_finished").text(status + ": " + errorThrown);
              },
              contentType: "application/json"
            });
    });
}

function initSolver3() {
	$("[id^='post']").click(function(event) {
        var targetId = event.target.id.split("_")[1];
        var fjsData = $("#fjs_"+targetId)[0].innerText;
        if ($("#fjs_"+targetId)[0].type === "textarea") {
            fjsData += $("#fjs_"+targetId)[0].value;
        }
        var transportData = $("#transport_"+targetId)[0].innerText;
        if ($("#transport_"+targetId)[0].type === "textarea") {
            transportData += $("#transport_"+targetId)[0].value;
        }
        var propertiesData = $("#properties_"+targetId)[0].innerText;
        if ($("#properties_"+targetId)[0].type === "textarea") {
            propertiesData += $("#properties_"+targetId)[0].value;
        }
		var solution = {};
        $.ajax({
			type: "POST",
			url: "rest/solver/solution",
			data: JSON.stringify({ fjs: fjsData, transport: transportData, properties: propertiesData }),
			success: function(data,status,xhr){
				solution = data.solution;
				visualizeSolution(solution);
			},
			error: function (jqXHR, status, errorThrown){
				solution = {};
				visualizeSolution(solution);
			},
			contentType: "application/json"
		});
    });
}

function visualizeSolution(solution) {
	var html = "";
	var time_max = 0;
	var jobs = {};
	for (key in solution) {
		for (var i=0; i<solution[key].length; i++) {
			var end = parseInt(solution[key][i].endTime);
			if (end > time_max)
				time_max = end;
		}
	}
	for (key in solution) {
		for (var i=0; i<solution[key].length; i++) {
			if (!jobs.hasOwnProperty(solution[key][i].jobId))
				jobs[solution[key][i].jobId]={};
			if (!jobs[solution[key][i].jobId].hasOwnProperty(solution[key][i].machineId))
				jobs[solution[key][i].jobId][solution[key][i].machineId]=[];
			jobs[solution[key][i].jobId][solution[key][i].machineId].push({
				startTime:solution[key][i].startTime,
				endTime:solution[key][i].endTime,
				operationId:solution[key][i].operationId
			});
		}
	}
	html += "<div class='table-responsive'>";
	html += "<table class='table'>";
	html += "<thead>";
	html += "<tr>";
	html += "<th style='width:100px;'>Job ID</th>";
	html += "<th>Process Visualization (Global End Time: "+time_max+")</th>";
	html += "</tr>";
	html += "</thead>";
	html += "<tbody>";
	if ($.isEmptyObject(jobs))
		html += "<tr><td colspan='2'>No result</td></tr>";
	else {
		for (key in jobs) {
			html += "<tr>";
			html += "<td style='width:100px;'>"+key+"</td>";
			html += "<td>";
			for (key2 in jobs[key]) {
				html += "<div title='Machine ID: "+key2+"' class='progress' style='height:10px; margin-bottom:3px;'>";
				var last_end = 0;
				for (var i=0; i<jobs[key][key2].length; i++) {
					var start = parseInt(jobs[key][key2][i].startTime);
					var end = parseInt(jobs[key][key2][i].endTime);
					if (last_end < start)
						html += "<div style='width:"+Math.round(((start-last_end)/time_max)*100)+"%;float:left;height:100%;'></div>";
					var style;
					if (i%2 == 0)
						style = "primary";
					else {
						if (last_end == start)
							style = "info";
					}
					last_end = end;
					html += "<div title='Machine ID: "+key2+"\nOperation ID: "+jobs[key][key2][i].operationId+"\nStart Time: "+start+"\nEnd Time: "+end+"' class='progress-bar progress-bar-"+style+"' style='width:"+Math.round(((end-start)/time_max)*100)+"%'></div>";
				}
				html += "</div>";
			}
			html += "</td>";
			html += "</tr>";
		}
	}
	html += "</tbody>";
	html += "</table>";
	html += "</div>";
	$("#vis").html(html);
	$('html, body').animate({
        scrollTop: $("#vis").offset().top-50
    }, 1000);
}