<!DOCTYPE html>
<html>
<head>
   <meta charset="utf-8">
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="stylesheet" href="bootstrap.min.css" >
	<link rel="stylesheet" href="dashboard.css" >
	<title>Distributed Production Control</title>
</head>
<body>
	<nav class="navbar navbar-fixed-top">
      <div class="container-fluid">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="icon-bar" > </span>
            <span class="icon-bar" > </span>
            <span class="icon-bar" > </span>
          </button>
          <a class="navbar-brand" href="index.jsp"> NgMPPS </a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
          <ul class="nav navbar-nav navbar-left">
            <li><a href="index.jsp">Start</a></li>
            <li><a href="dpc.jsp">DPC</a></li>
          </ul>
        </div>
      </div>
    </nav>
    
	<div class="container-fluid">
      <div class="row">
        <div class="col-sm-4 col-md-2 sidebar">
          <ul class="nav nav-sidebar">
            <li ><a href="/fjsstt-service">Start</a></li>
            <li><a href="/fjsstt-service/rest/myresource">Jersey resource</a></li>
            <li><a href="/fjsstt-service/rest/myservice/method1?uri=asdf">Jersey service</a></li>
            <li><a href="http://jersey.java.net">Project Jersey</a></li>
          </ul>
          <ul class="nav nav-sidebar">
            <li ></li>
            <li class="active"><a href="#" >DPC</a></li>
          </ul>
        </div>
      </div>
 	</div>
 	<div class="col-sm-8 offset-sm-2 col-md-10 offset-md-2 main">
	<div  class="container-fluid">
	<img src="salzburgresearch.png" style="height:60px;float:left;" /><img src="PROFACTOR_logo_weiss.jpg" style="v-align:bottom;height:30px;float:right;" />
   </div>
 		<h1> Distributed Production Control </h1>
 	    <h2>Select Scenario</h2>		
		<a class="btn btn-secondary" href="#" role="button" id="sbrg" > SBRG Scenario </a>
		<a class="btn btn-secondary" href="#" role="button" id="wt3a" > WT3A Scenario </a>
 	   <h2>Optimise Productionplan</h2>
		<a class="btn btn-secondary" href="#" role="button" id="post" > Start Optimisation </a>
		<a class="btn btn-secondary" href="#" role="button" id="image_reload" > Reload Images </a><br/>
		
		<span class="text-info" id="output" ></span><br/>
		
		<span class="text-success" id="successstatus" ></span><br/>

		<span class="text-danger" id="errorstatus" ></span><br/>
			

<form class="form-inline form-horizontal">

<div class="form-group-lg">
<label for="fjs">fjs</label><br />
<textarea class="form-control"  id="fjs" >

</textarea>
</div>


<div class="form-group-lg">
<label for="transport">transport</label><br/>
<textarea class="form-control" id="transport">
</textarea>
</div>


<div class="form-group-lg">
<label for="properties">properties</label><br/>
<textarea class="form-control" id="properties">
</textarea>
</div>
</form>

</div>
 <!-- Placed at the end of the document so the pages load faster -->
 <script src="tether.min.js" ></script>
 <script src="jquery.min.js" ></script>
 <script>window.jQuery || document.write('<script src="jquery.min.js"><\/script>')</script>
 <script src="bootstrap.min.js" ></script>

<script>
$(document).ready(function(){
    $(post).click(function(){
        var fjsData = $("#fjs").text();
        var transportData = $("#transport").text();
        var propertiesData = $("#properties").text();
        
        $.ajax({
			  type: "POST",
			  url: "rest/asyncresource/solution",
			  data: JSON.stringify({ fjs: fjsData, transport: transportData, properties: propertiesData }),
			  success: function(data,status,xhr){
            	$(output).html(data);
            	// text is requird if return values contain no proper html
            	$(successstatus).text(status);
        		},
        	  error: function (jqXHR, status, errorThrown){
        	  	// text is requird if return values contain no proper html
        	  	$(errorstatus).text(status);
        	  	$(output).text(errorThrown);
        	  },
			  dataType: "html",
			  contentType: "application/json"
			});
    });
});
$(image_reload).click(function () {
	var t = new Date().getTime();
    $("img").each(function(index){
	    var src = $(this).attr('src');
	    var i = src.indexOf('?dummy=');
	    src = i != -1 ? src.substring(0, i) : src;
	    $(this).attr('src', src + '?dummy=' + t);
    });
    var fjsData = $("#fjs").text();
     var transportData = $("#transport").text();
     var propertiesData = $("#properties").text();
    
    
    $.ajax({
			  type: "POST",
			  url: "rest/asyncresource/currentsolution",
			  data: JSON.stringify({ fjs: fjsData, transport: transportData, properties: propertiesData }),
			  success: function(data,status,xhr){
            	$(minupperbound).html("");
    				$(maxlowerbound).html("");
    				var json = $.parseJSON(data);
            	$(minupperbound).html("MinUpperBound: "+json.minUpperBoundSolution+"<br/>");
    				$(maxlowerbound).html("MaxLowerBound: "+json.maxLowerBoundSolution+"<br/>");
    				$(successstatus).text(status);
        		},
        	  error: function (jqXHR, status, errorThrown){
        	  	// text is requird if return values contain no proper html
        	  	$(errorstatus).text(status);
        	  	$(output).text(errorThrown);
        	  },
			  dataType: "html",
			  contentType: "application/json"
			});
});
$(sbrg).click(function(){
	$(fjs).text(fjs_sbrg.innerHTML);
	$(transport).text(transport_sbrg.innerHTML);
	$(properties).text(properties_sbrg.innerHTML);
});
$(wt3a).click(function(){
	$(fjs).text(fjs_WT3A.innerHTML);
	$(transport).text(transport_WT3A.innerHTML);
	$(properties).text(properties_WT3A.innerHTML);
});
</script>

<span id="fjs_sbrg" style="display:none;">15 28
6 2 2 3 5 5 2 14 4 15 5 3 19 5 20 6 21 8 3 22 4 23 3 24 6 3 25 5 26 4 28 6 3 19 5 20 6 21 8 0 45 1
6 3 19 5 20 6 21 8 3 25 5 26 4 28 6 3 19 5 20 6 21 8 2 14 4 15 5 3 22 4 23 3 24 6 2 2 3 5 5 0 35 2
6 2 14 4 15 5 3 25 5 26 4 28 6 3 19 5 20 6 21 8 3 19 5 20 6 21 8 2 2 3 5 5 3 22 4 23 3 24 6 0 40 3
6 2 14 4 15 5 3 19 5 20 6 21 8 2 2 3 5 5 3 19 5 20 6 21 8 3 25 5 26 4 28 6 3 22 4 23 3 24 6 0 50 2
6 3 19 5 20 6 21 8 2 2 3 5 5 3 25 5 26 4 28 6 2 14 4 15 5 3 19 5 20 6 21 8 3 22 4 23 3 24 6 0 45 3
7 5 12 3 13 4 22 6 23 4 24 5 3 25 6 26 5 28 4 2 27 3 5 4 2 1 2 6 3 2 3 6 4 7 3 7 7 8 8 9 6 2 14 5 15 4 0 50 1
7 2 1 2 6 3 5 12 3 13 4 22 6 23 4 24 5 3 25 6 26 5 28 4 2 27 3 5 4 2 3 6 4 7 3 7 7 8 8 9 6 2 14 5 15 4 0 55 2
7 2 14 5 15 4 2 3 6 4 7 2 1 2 6 3 2 27 3 5 4 5 12 3 13 4 22 6 23 4 24 5 3 25 6 26 5 28 4 3 7 7 8 8 9 6 0 55 1
7 2 14 5 15 4 2 3 6 4 7 2 1 2 6 3 3 25 6 26 5 28 4 5 12 3 13 4 22 6 23 4 24 5 2 27 3 5 4 3 7 7 8 8 9 6 0 50 3
7 2 27 3 5 4 3 25 6 26 5 28 4 5 12 3 13 4 22 6 23 4 24 5 2 1 2 6 3 2 3 6 4 7 3 7 7 8 8 9 6 2 14 5 15 4 0 50 1
5 2 10 8 11 7 2 10 9 11 8 2 10 10 11 9 3 7 5 8 4 9 8 3 19 5 20 6 21 8 0 30 1
5 3 19 5 20 6 21 8 2 10 10 11 9 2 10 8 11 7 2 10 9 11 8 3 7 5 8 4 9 8 0 25 2
5 3 19 5 20 6 21 8 3 7 5 8 4 9 8 2 10 10 11 9 2 10 8 11 7 2 10 9 11 8 0 25 3
5 2 10 10 11 9 2 10 8 11 7 2 10 9 11 8 3 7 5 8 4 9 8 3 19 5 20 6 21 8 0 30 4
5 3 7 5 8 4 9 8 2 10 8 11 7 2 10 9 11 8 2 10 10 11 9 3 19 5 20 6 21 8 0 35 4
</span>
<span id="transport_sbrg" style="display:none;">0;0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;11;4;11;5;22
0;0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;11;4;11;5;22
0;0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;11;4;11;5;22
22;22;22;0;11;0;22;22;0;18;18;6;6;7;14;11;11;15;7;7;15;6;6;14;6;14;11;0
5;5;5;11;0;11;5;5;11;8;8;4;4;3;6;0;0;6;3;3;6;4;4;6;4;6;0;11
22;22;22;0;11;0;22;22;0;18;18;6;6;7;14;11;11;15;7;7;15;6;6;14;6;14;11;0
0;0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;11;4;11;5;22
0;0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;11;4;11;5;22
22;22;22;0;11;0;22;22;0;18;18;6;6;7;14;11;11;15;7;7;15;6;6;14;6;14;11;0
8;8;8;18;8;18;8;8;18;0;0;7;7;12;21;8;8;8;12;12;8;7;7;21;7;21;8;18
8;8;8;18;8;18;8;8;18;0;0;7;7;12;21;8;8;8;12;12;8;7;7;21;7;21;8;18
4;4;4;6;4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0;0;4;0;4;4;6
4;4;4;6;4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0;0;4;0;4;4;6
13;13;13;7;3;7;13;13;7;12;12;11;11;0;24;3;3;2;0;0;2;11;11;24;11;24;3;7
11;11;11;14;6;14;11;11;14;21;21;4;4;24;0;6;6;7;24;24;7;4;4;0;4;0;6;14
5;5;5;11;0;11;5;5;11;8;8;4;4;3;6;0;0;6;3;3;6;4;4;6;4;6;0;11
5;5;5;11;0;11;5;5;11;8;8;4;4;3;6;0;0;6;3;3;6;4;4;6;4;6;0;11
7;7;7;15;6;15;7;7;15;8;8;6;6;2;7;6;6;0;2;2;0;6;6;7;6;7;6;15
13;13;13;7;3;7;13;13;7;12;12;11;11;0;24;3;3;2;0;0;2;11;11;24;11;24;3;7
13;13;13;7;3;7;13;13;7;12;12;11;11;0;24;3;3;2;0;0;2;11;11;24;11;24;3;7
7;7;7;15;6;15;7;7;15;8;8;6;6;2;7;6;6;0;2;2;0;6;6;7;6;7;6;15
4;4;4;6;4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0;0;4;0;4;4;6
4;4;4;6;4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0;0;4;0;4;4;6
11;11;11;14;6;14;11;11;14;21;21;4;4;24;0;6;6;7;24;24;7;4;4;0;4;0;6;14
4;4;4;6;4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0;0;4;0;4;4;6
11;11;11;14;6;14;11;11;14;21;21;4;4;24;0;6;6;7;24;24;7;4;4;0;4;0;6;14
5;5;5;11;0;11;5;5;11;8;8;4;4;3;6;0;0;6;3;3;6;4;4;6;4;6;0;11
22;22;22;0;11;0;22;22;0;18;18;6;6;7;14;11;11;15;7;7;15;6;6;14;6;14;11;0
</span>
<span id="properties_sbrg" style="display:none;">SimpleSearch.Alpha=2.0
#SubgradientSearch.TransportFile=
SubproblemSolver.LS_iterations=150
SubproblemSolver.MinMaxSlack=5
SimpleSearch.UpperBoundary=138
SurrogateSearch.R=0.1
SubgradientSearch.NrTimeSlots=800
SurrogateSearch.M=25
SurrogateSearch.IterationsUntilFeasibilityRepair=2
SubproblemSolver.VNSiterations=100
SubproblemSolver.MaxShakingDistance=3
SurrogateSearch.NoSubproblems=10
SurrogateSearch.FixedInitialStepsize=true
SurrogateSearch.EstimatedOptimalDualCost=-1
SurrogateSearch.InitialStepsize=0.2
SubgradientSearch.SearchType=SurrogateSubgradientSearch
SubproblemSolver.type=VariableNeighbourhoodSearch
SimpleSearch.IterationsUntilHalvingAlpha=20
SubproblemSolver.LS_altMachine_tries=1
SurrogateSearch.NrRuns=10
SubproblemSolver.MinMaxShiftDistance=50
SimpleSearch.IterationsUntilFeasibilityRepair=2
</span>
<span id="fjs_WT3A" style="display:none;">20	10
11	1	7	11	1	5	12	1	1	29	3	4	21	7	22	8	21	3	1	22	3	18	9	16	1	1	23	2	5	25	8	26	2	4	18	5	28	1	4	10	3	4	16	7	15	9	26	1	5	10	0	463	2
12	1	5	22	3	4	27	8	22	9	12	1	1	23	1	2	25	3	1	22	3	18	9	16	2	6	24	9	13	2	1	23	10	12	1	2	24	3	4	16	7	15	9	26	3	4	18	5	24	6	28	2	5	25	8	26	3	1	11	2	10	4	25	0	602	2
13	2	6	24	9	13	3	4	24	8	10	9	15	1	8	13	1	5	23	2	1	23	10	12	1	2	25	3	4	21	7	22	8	21	2	4	18	5	28	2	5	25	8	26	3	6	20	7	29	9	22	1	5	12	1	2	16	1	1	23	0	670	2
11	1	5	22	1	9	26	3	4	18	5	24	6	28	1	9	25	1	8	13	3	6	20	7	29	9	22	2	6	24	9	13	2	5	25	8	26	3	4	27	8	22	9	12	2	6	20	10	24	3	4	21	7	22	8	21	0	342	2
14	1	7	19	2	5	25	8	26	1	8	13	1	2	16	1	7	28	1	2	25	2	6	24	9	13	3	4	27	8	22	9	12	2	3	18	8	15	3	1	22	3	18	9	16	1	5	12	2	6	20	10	24	3	4	18	5	24	6	28	2	4	18	5	28	0	889	3
13	3	4	18	5	24	6	28	1	5	22	3	1	24	2	14	6	18	1	9	25	3	1	22	3	18	9	16	3	4	21	7	22	8	21	3	4	24	8	10	9	15	1	7	19	2	6	24	9	13	2	5	25	8	26	1	2	24	3	1	11	2	10	4	25	1	1	27	0	313	2
14	1	2	16	3	4	21	7	22	8	21	1	1	27	1	1	19	1	5	22	1	3	18	3	1	24	2	14	6	18	1	1	29	1	5	10	2	6	20	10	24	3	4	24	8	10	9	15	1	5	23	3	1	12	6	10	8	16	3	4	18	5	24	6	28	0	585	1
12	2	5	18	7	15	1	9	25	2	6	20	10	24	2	5	25	8	26	3	4	24	8	10	9	15	1	3	18	2	4	18	5	28	3	4	18	5	24	6	28	3	6	20	7	29	9	22	1	4	10	1	1	19	1	9	13	0	362	2
12	1	5	12	1	1	23	3	1	22	3	18	9	16	1	7	28	1	9	25	3	1	12	6	10	8	16	1	1	27	3	6	20	7	29	9	22	2	4	18	5	28	3	4	16	7	15	9	26	2	1	23	10	12	1	7	11	0	346	3
14	1	2	16	2	5	25	8	26	1	1	19	1	1	27	3	4	21	7	22	8	21	1	2	24	3	1	24	2	14	6	18	3	1	22	3	18	9	16	1	7	28	1	5	12	3	1	11	2	10	4	25	1	9	25	3	4	18	5	24	6	28	2	1	23	10	12	0	870	2
11	1	5	23	1	7	28	3	1	22	3	18	9	16	1	9	13	1	2	16	3	1	11	2	10	4	25	2	6	20	10	24	1	2	25	1	5	22	1	1	29	2	4	18	5	28	0	485	3
11	2	3	18	8	15	1	3	18	3	4	16	7	15	9	26	1	9	13	1	8	13	3	1	12	6	10	8	16	2	6	20	10	24	2	1	23	10	12	1	7	11	3	4	24	8	10	9	15	1	1	29	0	217	3
12	1	1	29	3	4	18	5	24	6	28	1	4	10	1	1	27	1	7	19	2	5	25	8	26	1	9	26	1	7	11	3	4	21	7	22	8	21	1	7	28	3	4	24	8	10	9	15	3	1	22	3	18	9	16	0	315	2
10	3	4	18	5	24	6	28	1	4	10	2	6	20	10	24	2	5	25	8	26	1	5	22	3	1	24	2	14	6	18	1	2	10	2	3	18	8	15	3	1	12	6	10	8	16	1	3	18	0	670	3
13	1	9	13	3	1	22	3	18	9	16	1	4	10	1	5	10	1	2	16	2	5	25	8	26	1	9	25	1	5	22	3	4	16	7	15	9	26	3	4	27	8	22	9	12	3	1	11	2	10	4	25	2	6	24	9	13	3	1	12	6	10	8	16	0	108	1
11	3	1	11	2	10	4	25	1	5	10	1	7	11	1	9	13	3	1	22	3	18	9	16	1	7	19	3	4	27	8	22	9	12	2	5	18	7	15	1	2	10	1	5	23	1	9	25	0	519	1
13	1	1	19	3	1	11	2	10	4	25	2	6	24	9	13	1	2	24	1	2	10	3	4	18	5	24	6	28	3	4	21	7	22	8	21	1	7	11	1	7	19	1	4	10	2	1	23	10	12	3	4	16	7	15	9	26	1	1	23	0	718	1
11	3	1	24	2	14	6	18	1	1	29	1	2	10	1	2	24	3	4	16	7	15	9	26	2	5	18	7	15	1	1	27	1	5	10	3	1	12	6	10	8	16	2	4	18	5	28	3	6	20	7	29	9	22	0	689	2
11	2	5	18	7	15	3	4	16	7	15	9	26	1	5	23	1	2	16	3	6	20	7	29	9	22	1	7	11	3	4	18	5	24	6	28	1	5	10	1	7	19	1	4	10	1	7	28	0	673	1
10	1	5	22	1	5	23	1	5	12	1	3	18	1	2	16	1	8	13	3	1	24	2	14	6	18	2	1	23	10	12	2	5	25	8	26	1	9	25	0	449	2
</span>
<span id="transport_WT3A" style="display:none;">0 27 0 27 24 24 24 27 0 24 
27 0 27 0 29 29 29 0 27 29 
0 27 0 27 24 24 24 27 0 24 
27 0 27 0 29 29 29 0 27 29 
24 29 24 29 0 0 0 29 24 0 
24 29 24 29 0 0 0 29 24 0 
24 29 24 29 0 0 0 29 24 0 
27 0 27 0 29 29 29 0 27 29 
0 27 0 27 24 24 24 27 0 24 
24 29 24 29 0 0 0 29 24 0 
</span>
<span id="properties_WT3A" style="display:none;">SubgradientSearch.NrTimeSlots = 2000
#SurrogateSubgradientSearch or SimpleSubgradientSearch or Both
SubgradientSearch.SearchType = SurrogateSubgradientSearch
SubgradientSearch.TransportFile = WT3A.transport

##configuration file for WT3 problem of NgMPPS
SimpleSearch.UpperBoundary = 4580
SimpleSearch.Alpha = 2.0
SimpleSearch.IterationsUntilHalvingAlpha = 20
SimpleSearch.IterationsUntilFeasibilityRepair = 2


SurrogateSearch.FixedInitialStepsize = true
SurrogateSearch.EstimatedOptimalDualCost = -1
SurrogateSearch.InitialStepsize = 0.2
SurrogateSearch.NoSubproblems = 20
SurrogateSearch.R = 0.1
SurrogateSearch.M = 25
SurrogateSearch.IterationsUntilFeasibilityRepair = 2
SurrogateSearch.NrRuns = 10

#Type = DynamicProgramming, VariableNeighbourhoodSearch
SubproblemSolver.type = VariableNeighbourhoodSearch
SubproblemSolver.VNSiterations = 100
SubproblemSolver.MaxShakingDistance = 3
SubproblemSolver.LS_iterations = 150
SubproblemSolver.LS_altMachine_tries = 1
SubproblemSolver.MinMaxSlack = 5
SubproblemSolver.MinMaxShiftDistance = 50
</span>
</body>
</html>
