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
            <li class="active"><a href="#">Start <span class="sr-only">(current)</span></a></li>
            <li><a href="rest/myresource">Jersey resource</a></li>
            <li><a href="rest/myservice/method1?uri=asdf">Jersey service</a></li>
            <li><a href="http://jersey.java.net">Project Jersey</a></li>
          </ul>
          <ul class="nav nav-sidebar">
            <li ></li>
            <li><a href="dpc.jsp">DPC</a></li>
          </ul>
        </div>
      </div>
 	</div>
 	<div class="col-sm-9 offset-sm-3 col-md-10 offset-md-2 main">
 	
 	
 	    <h1>Jersey RESTful Web Application!</h1>
 	
    <p>Test Async Service with test problem: <br/>
    
    <pre>curl -d @testproblem.json -H "Content-Type: application/json" http://localhost:8080/fjsstt-service/rest/asyncresource/problem</pre>
    </p>


	</div>
 <!-- Placed at the end of the document so the pages load faster -->
 <script src="tether.min.js" ></script>
 <script src="jquery.min.js" ></script>
 <script>window.jQuery || document.write('<script src="jquery.min.js"><\/script>')</script>
 <script src="bootstrap.min.js" ></script>

</body>
</html>
