<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
	
	<title>Gofest - ${festa.nome} - ${festa.local}</title>
	<jsp:include page="_assets.jsp"/>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/scripts.js"></script>		
	
</head>

	<body>
		
		<div id="wrap">
			<div class="container">
				
				<jsp:include page="_header.jsp"/>
				
				<h3 class="section-title truncate">${festa.nome}</h3>
				
				<div class="row">
					<div class="sixteen columns">				
						
						<div class="twelve columns content alpha">
						
							<article class="post single">
													
								<div class="post-intro">
									<img src="${festa.imagem}" alt="" class="scale-with-grid-show" />
									<h2><a href="#">${festa.local}</a></h2>
								</div><!-- /intro -->
								
								<div class="post-body">
									<p class="meta">
										<time class="post-date" datetime="${festa.dataInicio}">
											<fmt:formatDate pattern="EEEE, dd 'de' MMMM 'de' yyyy" value="${festa.dataInicio}" />
										</time> 
										<span class="bull">&bull;</span> <a href="#">${festa.local}</a></p>
										<p>${festa.descricao}
									</p>
								</div>
								
								<div id="disqus_thread"></div>
							    <script type="text/javascript">
							        /* * * CONFIGURATION VARIABLES: EDIT BEFORE PASTING INTO YOUR WEBPAGE * * */
							        var disqus_shortname = 'gofest'; // required: replace example with your forum shortname
							        var disqus_identifier = '${festa.key}';
							
							        /* * * DON'T EDIT BELOW THIS LINE * * */
							        (function() {
							            var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;
							            dsq.src = '//' + disqus_shortname + '.disqus.com/embed.js';
							            (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);
							        })();
							    </script>
							    <noscript>Please enable JavaScript to view the <a href="http://disqus.com/?ref_noscript">comments powered by Disqus.</a></noscript>
							    <a href="http://disqus.com" class="dsq-brlink">comments powered by <span class="logo-disqus">Disqus</span></a>
    								
							
							</article><!-- /post -->
							
						</div><!-- /content -->
						
						<jsp:include page="_sidebar_recentes.jsp"/>	
														
					</div><!-- /sixteen columns -->
				</div><!-- /row -->
			
			</div><!-- /container -->
			
			<jsp:include page="_footer.jsp"/>
			
			<!-- AddThis Smart Layers BEGIN -->
			<!-- Go to http://www.addthis.com/get/smart-layers to customize -->
			<script type="text/javascript" src="//s7.addthis.com/js/300/addthis_widget.js#pubid=thiagonego"></script>
			<script type="text/javascript">
			  addthis.layers({
			    'theme' : 'dark',
			    'share' : {
			      'position' : 'left',
			      'numPreferredServices' : 4
			    }, 
			    'follow' : {
			      'services' : [
			        {'service': 'facebook', 'id': 'appgofest'},
			        {'service': 'twitter', 'id': 'appgofest'},
			        {'service': 'google_follow', 'id': 'appgofesst'}
			      ]
			    }   
			  });
			</script>
			<!-- AddThis Smart Layers END -->			
					
		</div><!-- /wrap -->
		
	</body>
</html>	