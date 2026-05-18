<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${totalPages > 1}">
    <nav aria-label="Page navigation">
        <ul class="pagination justify-content-end mt-4 mr-4">

            <!-- First page « -->
            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                <a class="page-link" href="?page=1&size=${size}&keyword=${keyword}&status=${status}&sortBy=${sortBy}&sortDir=${sortDir}">
                    <i class="fas fa-angle-double-left"></i>
                </a>
            </li>

            <!-- Prev < -->
            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                <a class="page-link" href="?page=${currentPage - 1}&size=${size}&keyword=${keyword}&status=${status}&sortBy=${sortBy}&sortDir=${sortDir}">
                    <i class="fas fa-angle-left"></i>
                </a>
            </li>

            <!-- ... before range -->
            <c:if test="${totalPages > 3 && currentPage > 3}">
                <li class="page-item disabled"><span class="page-link">...</span></li>
            </c:if>

            <!-- Center pages (max 3) -->
            <c:set var="startPage" value="${currentPage <= 2 ? 1 : (currentPage >= totalPages - 1 ? totalPages - 2 : currentPage - 1)}" />
			<c:set var="endPage" value="${currentPage <= 2 ? 3 : (currentPage >= totalPages - 1 ? totalPages : currentPage + 1)}" />
			
			<!-- Center pages (max 3) -->
			<c:forEach var="i" begin="${startPage}" end="${endPage}">
			    <c:if test="${i >= 1 && i <= totalPages}">
			        <li class="page-item ${i == currentPage ? 'active' : ''}">
			            <a class="page-link" href="?page=${i}&size=${size}&keyword=${keyword}&status=${status}&sortBy=${sortBy}&sortDir=${sortDir}">${i}</a>
			        </li>
			    </c:if>
			</c:forEach>

            <!-- ... after range -->
            <c:if test="${totalPages > 3 && currentPage < totalPages - 2}">
                <li class="page-item disabled"><span class="page-link">...</span></li>
            </c:if>

            <!-- Next > -->
            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                <a class="page-link" href="?page=${currentPage + 1}&size=${size}&keyword=${keyword}&status=${status}&sortBy=${sortBy}&sortDir=${sortDir}">
                    <i class="fas fa-angle-right"></i>
                </a>
            </li>

            <!-- Last page » -->
            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                <a class="page-link" href="?page=${totalPages}&size=${size}&keyword=${keyword}&status=${status}&sortBy=${sortBy}&sortDir=${sortDir}">
                    <i class="fas fa-angle-double-right"></i>
                </a>
            </li>

        </ul>
    </nav>
</c:if>


