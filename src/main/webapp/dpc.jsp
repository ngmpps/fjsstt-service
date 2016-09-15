<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="bootstrap.min.css" >
<link rel="stylesheet" href="dashboard.css" >
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Welcome Page</title>
</head>
<body>
	<div class="container-fluid">
      <div class="row">
        <div class="col-sm-3 col-md-2 sidebar">
          <ul class="nav nav-sidebar">
            <li ><a href="/fjsstt-service">Start</a></li>
            <li><a href="/fjsstt-service/rest/myresource">Jersey resource</a></li>
            <li><a href="/fjsstt-service/rest/myservice/method1?uri=asdf">Jersey service</a></li>
            <li><a href="http://jersey.java.net">Project Jersey</a></li>
          </ul>
          <ul class="nav nav-sidebar">
            <li ></li>
            <li><a href="#" class="active">DPC</a></li>
          </ul>
        </div>
      </div>
 	</div>
 	<div class="col-sm-9 offset-sm-3 col-md-10 offset-md-2 main">
 		
 		<h1> Distributed Production Control </h1>
 	    <h2>Post Content</h2>
 	
		<a class="btn btn-secondary" href="#" role="button" id="post" > POST </a><br/>
		
		<span class="text-info" id="output" ></span><br/>
		
		<span class="text-success" id="successstatus" ></span><br/>

		<span class="text-danger" id="errorstatus" ></span><br/>
			

<form>

<div class="form-group">
<label for="fjs">fjs</label><br />
<textarea class="form-control"  id="fjs" >
15 28
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
</textarea>
</div>


<div class="form-group">
<label for="transport">transport</label><br/>
<textarea class="form-control" id="transport">
0;0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;11;4;11;5;22
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
</textarea>
</div>


<div class="form-group">
<label for="properties">properties</label><br/>
<textarea class="form-control" id="properties">
SimpleSearch.Alpha=2.0
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
SubgradientSearch.SearchType=Both
SubproblemSolver.type=VariableNeighbourhoodSearch
SimpleSearch.IterationsUntilHalvingAlpha=20
SubproblemSolver.LS_altMachine_tries=1
SurrogateSearch.NrRuns=10
SubproblemSolver.MinMaxShiftDistance=50
SimpleSearch.IterationsUntilFeasibilityRepair=2
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
</script>

</body>
</html>
