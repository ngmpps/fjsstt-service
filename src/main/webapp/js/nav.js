var debug = false;
var loaded = false;
var last_load = "";

window.onhashchange = function () {
	if (debug && window.console && window.console.log)
		console.log("onhashchange detected, loaded: "+loaded);
	init(true);
}

function init(flag) {
	loaded = true;
	var site = window.location.href.split("#")[1];
	if (debug && window.console && window.console.log)
		console.log("init with site "+site+" and flag "+flag);
	if (site && flag == false) {}
	else {
		if (site) {
			show(site);
		}
		else
			show('dashboard'); // This is the "Landing-Page"
	}
}

function show(page,id) {
	if (debug && window.console && window.console.log)
		console.log("Trying to show "+page);
	if (page != last_load) {
		if (id == null) {
			last_load = page;
			if (debug && window.console && window.console.log)
				console.log("Last load set to "+page);
			id = "#content";
			window.location.href = window.location.href.split("#")[0]+"#"+page;
		}
		$.ajax({
			method: "GET",
			url: "pages/"+page+".html",
			cache: false,
			dataType: "html",
			success: function(response,status,xhr) {
				if (status == "error") {
					$(id).html("<div style='color:red'>Invalid Page</div>");
				}
				else {
					$(id).html(response);
					performAction(page);
				}
			},
			error: function(xhr,status,error) {
				$(id).html("<div style='color:red'>Invalid Page</div>");
			}
		});
	}
}

function performAction(page) {
    $("#"+page).siblings().removeClass("active");
	$("#"+page).addClass("active");

	switch (page) {
	    // optional: do something on page load
	    case "dpc-testing":
			initSolver();
			break;
		case "dpc-visualization":
			initSolver3();
			break;
	    case "dpc-testing2":
			initSolver2();
			break;
		default:
			break;
	}
}

$(document).ready(function(){
   init(true);
});
