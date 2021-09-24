<%-- 
    Document   : adminuser
    Админ-панель для управления пользователями для администраторов (фрагмент)
--%>

<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String domain = request.getContextPath() ;
    ArrayList<orm.User> users = services.Db.getUsersListFull() ;
%>
<div class="admin-user">
    Пользователь:
    <select name="userId">
        <% if( users != null ) for( orm.User user : users ) { %>
        <option value="<%= user.getId() %>" del="<%= user.getIsDeleted() %>" >
            <%= user.getName() %>
        </option>
        <% } %>
    </select>
    <br/>
    <input type="button" id="del-user-button" value="Удалить" />
</div>
<script src="<%= domain %>/js/adminuser.js"></script>