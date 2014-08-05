<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
	<title>Podcast - Download</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta charset="utf-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/bootstrap-theme.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/font-awesome.min.css">
    <style type="text/css">
      body{
        background-color: #fff;        
        padding-top: 60px;
      }
      .row{
        margin-bottom: 20px;
      }
      .table i{
        padding: 0.2em 0.25em 0.15em;
      }
      .table i:hover{
        color: #F4586F;
        cursor: pointer;
      }
      .truncate {
        margin-right: 0;
        max-width: 275px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
	  .table thead tr td{
		font-weight: bold;
	  }
    </style>    
</head>

	<body>
	
	    <div class="container">
	      <form role="form">    
	        <div class="row">
	          <div class="col-sm-12">
	            <table class="table table-striped- table-bordered table-hover">
					<thead class="bg-primary">
						<tr>
							<td class="text-center ">FEED</td>
							<td class="text-center ">EMAILS</td>
							<td class="text-center ">ULTIMO</td>
							<td class="text-center ">OPTS</td>
						</tr>
					</thead>
					<tbody id="table-body">
						<c:forEach var="pod" items="${podCasts}">
						    <tr>
						    	<td>${pod.feed}</td>
						    	<td>${pod.emails}</td>
						    	<td>${pod.ultimo}</td>
						    	<td class='text-center'><a href='#' data-row='${pod.row}' class='run-feed'><i class='fa fa-cog'></i></a></td>
						    </tr>
						</c:forEach>					
					</tbody>
	            </table>
	          </div>
	        </div>
	      </form>
	    </div>  
		
	    <script type="text/javascript" src="/assets/js/jsapi.js"></script>
	    <script type="text/javascript" src="/assets/js/jquery.min.js"></script>
	    <script type="text/javascript">
	       
			var linhas = null
	        jQuery(document).ready(function(){
	        	jQuery.each(linhas, function(i,v){

	        		if(i != '1'){

		        		var tr = "<tr>";
		        		tr +="<td class='text-center'><a href='#'>"+ v['1']+"</a></td>";
		        		tr +="<td class='text-center'>"+ v['2']+"</td>";
		        		tr +="<td class='text-center'>"+ v['3']+"</td>";
		        		tr +="<td class='text-center'><a href='#' data-url='"+ v['1']+"' data-ultimo='"+ v['3']+"' class='run-feed'><i class='fa fa-cog'></i></a></td>";
		        		tr +="</tr>";

		        		jQuery('#table-body').append(tr);

	        		}

	        	});
	        	
       			binds();
	        });
		        
			function binds(){
			  jQuery('.run-feed').on('click',function(e){
				var i = jQuery(this).find('i');
				i.toggleClass('fa-spin');
				jQuery.get('run/' + jQuery(this).data('row')), function(data){
					console.log(JSON.stringify(data));
				}).complete(function(){
					i.toggleClass('fa-spin');
				});
				e.preventDefault();
			  });			
			}

	    </script>    		
	
	</body>
</html>	