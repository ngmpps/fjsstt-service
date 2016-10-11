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
            	  $("#results_running").html("<td colspan='2'>" + data + "</td>");
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
            	  $("#results_finished").html("<td colspan='2'>" + data + "</td>");
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

var jobs = [];
var machines = [];
function initGenerator() {
	jobs = [];
	machines = [];
	$.ajax({
		method: "GET",
		url: "data/machines.json",
		cache: false,
		dataType: "json",
		success: function(response,status,xhr) {
			machines = response.machines;
		}
	});
}

function addJob() {
	var startTime = 0;
	var dueDate = 168;
	var weighting = 1;
	if (!isNaN(parseInt($("#jobStartTime").val())))
		startTime = parseInt($("#jobStartTime").val());
	if (!isNaN(parseInt($("#jobDueDate").val())))
		dueDate = parseInt($("#jobDueDate").val());
	if (!isNaN(parseInt($("#jobWeighting").val())))
		weighting = parseInt($("#jobWeighting").val());
	jobs.push({
		"id":Date.now(),
		"startTime":startTime,
		"dueDate":dueDate,
		"weighting":weighting,
		"operations":[]
	});
	showJobList();
}

function deleteJob(id) {
	for (var i=0; i<jobs.length; i++) {
		if (jobs[i].id == id)
			jobs.splice(i,1);
	}
	showJobList();
}

var job_id = 0;
function addOperation() {
	var operationId = Date.now();
	var requires = [];
	if (!isNaN(parseInt($("#operationId").val())))
		operationId = parseInt($("#operationId").val());
	if (document.getElementById('req_3d').checked)
		requires.push($("#req_3d").val());
	if (document.getElementById('req_assem').checked)
		requires.push($("#req_assem").val());
	if (document.getElementById('req_cbp').checked)
		requires.push($("#req_cbp").val());
	if (document.getElementById('req_fdm').checked)
		requires.push($("#req_fdm").val());
	if (document.getElementById('req_mill').checked)
		requires.push($("#req_mill").val());
	if (document.getElementById('req_pcbm').checked)
		requires.push($("#req_pcbm").val());
	for (var i=0; i<jobs.length; i++) {
		if (jobs[i].id == job_id) {
			jobs[i].operations.push({
				"operationId":operationId,
				"file": "path/to/object.3mf",
				"requires":requires
			});
			jobs[i].operations.sort(function(a,b){
				return a.operationId-b.operationId;
			});
		}
	}
	showJobList();
}

function deleteOperations(id) {
	for (var i=0; i<jobs.length; i++) {
		if (jobs[i].id == id)
			jobs[i].operations=[];
	}
	showJobList();
}

function showJobList() {
	var html = "";
	for (var i=0; i<jobs.length; i++) {
		html += "<tr>";
		html += "<td>ID: "+jobs[i].id+"<br/>Start Time: "+jobs[i].startTime+"<br/>Due Date: "+jobs[i].dueDate+"<br/>Weighting: "+jobs[i].weighting+"</td>";
		html += "<td>"
		for (var j=0; j<jobs[i].operations.length; j++) {
			html += "ID: "+jobs[i].operations[j].operationId+", Requires: ["+jobs[i].operations[j].requires.length+"]";
			if (j < jobs[i].operations.length-1)
				html += "<br/>";
		}
		html += "</td>";
		html += "<td><button class='btn btn-sm btn-primary space' onclick='job_id="+jobs[i].id+";$(\"#operationModal\").modal();'>Add Operation</button><br/><button class='btn btn-sm btn-danger space' onclick='deleteOperations("+jobs[i].id+")'>Delete Operations</button><br/><button class='btn btn-sm btn-danger' onclick='deleteJob("+jobs[i].id+")'>Delete Job</button></td>";
		html += "</tr>";
	}
	$("#job-list").html(html);
}

function generateFJSS() {
	var fjss = "";
	fjss += jobs.length + " " + machines.length + "\n";
	for (var i=0; i<jobs.length; i++) {
		fjss += jobs[i].operations.length + " ";
		for (var j=0; j<jobs[i].operations.length; j++) {
			var choices = [];
			for (var k=0; k<jobs[i].operations[j].requires.length; k++) {
				for (var l=0; l<machines.length; l++) {
					for (var m=0; m<machines[l]["@type"].length; m++) {
						if (machines[l]["@type"][m] == jobs[i].operations[j].requires[k])
							choices.push([machines[l].id,getRandomTime()]);
					}
				}
			}
			fjss += choices.length + " ";
			for (var k=0; k<choices.length; k++) {
				fjss += choices[k][0] + " " + choices[k][1] + " ";
			}
		}
		fjss += jobs[i].startTime + " " + jobs[i].dueDate + " " + jobs[i].weighting;
		fjss += "\n";
	}
	var html = "<textarea style='width:100%; min-height:300px; resize: vertical;'>"+fjss+"</textarea>";
	$("#fjss").html(html);
}

function getRandomTime() {
	var min = 7;
	var max = 15;
	return Math.floor(Math.random() * (max - min + 1)) + min;
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