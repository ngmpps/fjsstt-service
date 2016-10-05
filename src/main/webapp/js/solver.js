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