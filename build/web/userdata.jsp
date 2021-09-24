<%-- 
    Document   : userdata
    Cтраница регистрации нового пользователя / личный кабинет
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String domain = request.getContextPath() ;
    orm.User user = services.Auth.getUser() ;
    String pageTitle = 
            ( user == null ) 
            ? "Регистрация" 
            : "Кабинет" ;
    String userLogin = ( user == null ) ? "" : user.getLogin() ;
    String userName  = ( user == null ) ? "" : user.getName();
    String regMessage = (String) session.getAttribute( "regMessage" ) ;
    if( regMessage != null ) session.removeAttribute( "regMessage" ) ;
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><%= pageTitle %></title>
    </head>
    <body>
        <h1><%= pageTitle %></h1>
        <form method="post" enctype="multipart/form-data" >
            Логин: <input name="userLogin" value="<%= userLogin %>" /><br/>
            Пароль: <input type="password" name="pass1" /><br/>
            Повтор: <input type="password" name="pass2" /><br/>
            Имя: <input type="text" name="userName" value="<%= userName %>" /><br/>
            Аватарка: <input type="file" name="userAvatar" /><br/>
            <% if( user != null ) { %>
                <img src="<%= domain %>/ava/<%= user.getAvatar() %>" /><br/>
            <% } %>
            <input type="submit" value="Сохранить" />
        </form>
            <br/>
            <b><%= regMessage %></b>
    </body>
</html>
