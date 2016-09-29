function initSolver()  {
    $("[id^='post']").click(function(event) {
        var targetId = event.target.id.split("_")[1]
        var fjsData = $("#fjs_"+targetId)[0].innerText;
        var transportData = $("#transport_"+targetId)[0].innerText;
        var propertiesData = $("#properties_"+targetId)[0].innerText;

        $.ajax({
              type: "POST",
              url: "rest/solver/solution",
              data: JSON.stringify({ fjs: fjsData, transport: transportData, properties: propertiesData }),
              success: function(data,status,xhr){
                $("#output_"+targetId).text(JSON.stringify(data.solution,null, 2));
                // text is required if return values contain no proper html
                $("#status_"+targetId).text(status+ ": " + data.name);
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