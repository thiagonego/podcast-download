<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
	<title>Podcast - Download</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta charset="utf-8">
	<meta http-equiv="refresh" content="60" >
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
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.min.js"></script>
   	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
    <script type="text/javascript">
       	
		var linhas = null
        jQuery(document).ready(function(){
	        
		  jQuery('.run-feed').on('click',function(e){

				var item = jQuery(this);
				var i = item.find('i');
				i.toggleClass('fa-spin');

				jQuery.ajax({
				    url: 'run/' + item.data('row'),
				    success: function(data, textStatus, jqXHR){
						console.log(data, textStatus, jqXHR);
						alert(data, 'alert-success');
				    },
				    error: function(jqXHR, textStatus, errorThrown){
					    console.log(jqXHR, textStatus, errorThrown);
					    alert(jqXHR.responseText, 'alert-danger');
					},
					complete: function(data){
						i.toggleClass('fa-spin');
					}
				});
			
			e.preventDefault();
			
		  });	

		  jQuery('.btn-enviar-email').on('click',function(){
			  
			  	var item = jQuery(this).data('pod-id');
			  	location.href = 

				jQuery.ajax({
				    url: 'email/' + item.data('row'),
				    success: function(data, textStatus, jqXHR){
						console.log(data, textStatus, jqXHR);
						alert(data, 'alert-success');
				    },
				    error: function(jqXHR, textStatus, errorThrown){
					    console.log(jqXHR, textStatus, errorThrown);
					    alert(jqXHR.responseText, 'alert-danger');
					},
					complete: function(data){
						i.toggleClass('fa-spin');
					}
				});
							  
		  });
		  		
        });
	        
		function alert(msg, klasse){
			var div = 
			"<div id='alert' class='alert alert-dismissible "+ klasse + "' role='alert'> "+
			  "<button type='button' class='close' data-dismiss='alert'><span aria-hidden='true'>&times;</span><span class='sr-only'>Close</span></button>"+
			  msg +
			"</div>";
			jQuery('#alert').html(div);				
		}
    </script> 	    
</head>

	<body>
	
	    <div class="container">
	    	
			<div id="alert"></div>
			
			<div class="row">
				<div class="col-sm-12">
			        <ul class="nav nav-pills pull-right">
			          <li class="active-">
			          	<a href="${pageContext.request.contextPath}/refresh">
			          		<i class="fa fa-refresh"></i>
			          		Recarregar
			          	</a>
			          </li>
			        </ul>
			        <h3 class="text-muted">Podcast</h3>
			        <hr/>
		        </div>
	      	</div>	    
	    
		      <form role="form">    
		        <div class="row">
		          <div class="col-sm-12">
		            <table class="table table-striped- table-bordered table-hover">
						<thead class="bg-primary">
							<tr>
								<td class="text-center ">ID</td>
								<td class="text-center ">NOME</td>
								<td class="text-center ">FEED</td>
								<td class="text-center ">EMAILS</td>
								<td class="text-center ">ULTIMO</td>
								<td class="text-center ">OPTS</td>
							</tr>
						</thead>
						<tbody id="table-body">
							<c:forEach var="pod" items="${podCasts}">
							    <tr>
							    	<td class="text-center">${pod.row}</td>
							    	<td class="text-center">${pod.nome}</td>
							    	<td>${pod.feed}</td>
							    	<td>${pod.emails}</td>
							    	<td class="text-center">${pod.ultimo}</td>
							    	<td class='text-center'>
							    		<a href='#' data-row='${pod.row}' class='run-feed'><i class='fa fa-cog'></i></a>
							    		<a href='${pod.link}'><i class='fa fa-cloud-download'></i></a>
							    		
							    		<a href='#' data-toggle="modal" data-target="#modalEmail${pod.row}" ><i class='fa fa-envelope-o'></i></a>
							    		
										<div class="modal fade" id="modalEmail${pod.row}" tabindex="-1" role="dialog" aria-labelledby="modalEmailLabel" aria-hidden="true">
										  <div class="modal-dialog">
										    <div class="modal-content">
										      <div class="modal-header">
										        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
										        <h4 class="modal-title" id="modalEmailLabel">Enviar email?</h4>
										      </div>
										      <div class="modal-body text-left">
										       Deseja mesmo enviar emails para: <br/> ${pod.emails} ?
										      </div>
										      <div class="modal-footer">
										        <button type="button" class="btn btn-default" data-dismiss="modal">Não</button>
										        <a href="email/${pod.row}" class="btn btn-primary">Sim</a>
										      </div>
										    </div>
										  </div>
										</div>							    		
							    			    		
							    	</td>
							    </tr>
							</c:forEach>					
						</tbody>
		            </table>
		          </div>
		        </div>
		      </form>
	    </div>  
	     
	 
		
		   
	</body>
</html>	