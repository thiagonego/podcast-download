<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<aside class="four columns alpha sidebar">

	<div class="widget group">
		<h3 class="widget-title">Resumo</h3>
		<ul class="tour-dates">
				<li class="group">						
					<c:forEach items="${festas}" var="festa">
							<p class="tour-date">
								<span class="day">
									<fmt:formatDate pattern="dd" value="${festa.dataInicio}" />
								</span>
									<fmt:formatDate type="both" pattern="MMMM"   value="${festa.dataInicio}" />
								<span class="year">
									<fmt:formatDate pattern="yyyy" value="${festa.dataInicio}" />
								</span></p>
							<div class="tour-place">
								<span class="sub-head truncate">
									<a href="${pageContext.request.contextPath}/f/${festa.key}">
										${festa.nome}
									</a>
								</span>
								<span class="main-head truncate">${festa.local}</span>							
							</div>
					</c:forEach>							
				</li>
		</ul><!-- /tour-dates -->
	</div><!-- /widget -->
	
</aside><!-- /sidebar -->	