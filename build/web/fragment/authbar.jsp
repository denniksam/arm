<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String domain = request.getContextPath() ;
    String message = (String) session.getAttribute( "authMessage" ) ;
    if( message != null ) {
        session.removeAttribute( "authMessage" ) ;
    }
    orm.User user = services.Auth.getUser() ;
%>
<div class="auth-bar">
    <% if( user != null ) { %>
        <img onclick="window.location='<%= domain %>/account'"
             src="<%= domain %>/ava/<%= user.getAvatar() %>" />
        Приветствие, <strong><%= user.getName() %></strong>
        <a href="<%= domain %>/auth">Выход</a>
        <%= services.Auth.getLoginDuration() %>
    <% } else { %>    
        <form method="post" action="<%= domain %>/auth" >
            Логин  <input name="authlogin" />
            Пароль <input name="authpassw" type="password" />
            <input type="submit" value="Вход" />
            <a href="<%= domain %>/register">Регистрация</a>
        </form>
    <% } %>
    
    <% if( message != null ) { %>
        <strong><%= message %></strong>
    <% } %>
    
</div>