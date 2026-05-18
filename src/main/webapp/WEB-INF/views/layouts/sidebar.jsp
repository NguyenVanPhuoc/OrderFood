<div class="bg-gray-800 text-white w-64 h-screen flex flex-col">
    <div class="flex items-center justify-center h-16 mt-1 mb-2">
        <h1 class="text-xl font-bold flex align-items-center"><img src="${pageContext.request.contextPath}/images/logo_training.jpg" alt="logo" class="w-10 rounded-full mr-2"> Admin</h1>
    </div>
    <nav class="flex-grow">
	    <ul>
	        <li class="flex items-center py-2 px-4 hover:bg-gray-700 ${currentPath == '/admin/dashboard' ? 'bg-gray-700' : ''}">
	            <i class="fas fa-tachometer-alt"></i>
	            <a href="${pageContext.request.contextPath}/admin/dashboard" class="ml-2 ${currentPath == '/admin/dashboard' ? 'text-blue-400 font-bold' : ''}">Dashboard</a>
	        </li>

	        <!-- Users (with submenu) -->
			<li class="py-2 px-4 hover:bg-gray-700">
			    <div class="flex items-center justify-between cursor-pointer" onclick="toggleSubmenu('userSubmenu')">
			        <div class="flex items-center">
			            <i class="fas fa-users"></i>
			            <span class="ml-2 ${currentPath.startsWith('/admin/users') ? 'text-blue-500 font-semibold' : ''}">
			                Users
			            </span>
			        </div>
			        <i class="fas fa-chevron-down"></i>
			    </div>
			    <ul id="userSubmenu"
			        class="ml-6 mt-2 transition-all duration-300 overflow-hidden"
			        style="${currentPath.startsWith('/admin/users') ? 'display: block;' : 'display: none;'}">
			        <li class="py-1">
			            <a href="${pageContext.request.contextPath}/admin/users"
			               class="${currentPath == '/admin/users' ? 'text-blue-400 font-bold underline' : ''}">
			                List
			            </a>
			        </li>
			        <li class="py-1">
			            <a href="${pageContext.request.contextPath}/admin/users/create"
			               class="${currentPath == '/admin/users/create' ? 'text-blue-400 font-bold underline' : ''}">
			                Create
			            </a>
			        </li>
			    </ul>
			</li>

			<!-- store (with submenu) -->
			<li class="py-2 px-4 hover:bg-gray-700">
			    <div class="flex items-center justify-between cursor-pointer" onclick="toggleSubmenu('storeSubmenu')">
			        <div class="flex items-center">
			            <i class="fas fa-store"></i>
			            <span class="ml-2 ${currentPath.startsWith('/admin/stores') ? 'text-blue-500 font-semibold' : ''}">
			                Stores
			            </span>
			        </div>
			        <i class="fas fa-chevron-down"></i>
			    </div>
			    <ul id="storeSubmenu"
			        class="ml-6 mt-2 transition-all duration-300 overflow-hidden"
			        style="${currentPath.startsWith('/admin/stores') ? 'display: block;' : 'display: none;'}">
			        <li class="py-1">
			            <a href="${pageContext.request.contextPath}/admin/stores"
			               class="${currentPath == '/admin/stores' ? 'text-blue-400 font-bold underline' : ''}">
			                List
			            </a>
			        </li>
			        <li class="py-1">
			            <a href="${pageContext.request.contextPath}/admin/stores/create"
			               class="${currentPath == '/admin/stores/create' ? 'text-blue-400 font-bold underline' : ''}">
			                Create
			            </a>
			        </li>
			    </ul>
			</li>

			<!-- order (with submenu) -->
			 <li class="flex items-center py-2 px-4 hover:bg-gray-700 ${currentPath == '/admin/orders' ? 'bg-gray-700' : ''}">
                <i class="fas fa-clipboard-list"></i>
                <a href="${pageContext.request.contextPath}/admin/orders" class="ml-2 ${currentPath == '/admin/orders' ? 'text-blue-400 font-bold' : ''}">Orders</a>
            </li>

	        <!-- Logout -->
	        <sec:authorize access="isAuthenticated()">
	            <li class="flex items-center py-2 px-4 hover:bg-gray-700">
	                <i class="fas fa-sign-out-alt"></i>
	                <a href="${pageContext.request.contextPath}/admin/logout" class="ml-2">Logout</a>
	            </li>
	        </sec:authorize>
	    </ul>
	</nav>
</div>
