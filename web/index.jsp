<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String domain = request.getContextPath() ;
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>АРМ</title>
        <link rel="stylesheet" href="<%= domain %>/css/main.css" />
    </head>
    <body>
    <main>
        <!-- Панель авторизации -->
        <jsp:include page="fragment/authbar.jsp"/>
        
        <!-- Основной контент -->
        <!-- Разделяем по условию авторизации -->
        <% if( services.Auth.getUser() == null ) { %>
            <jsp:include page="fragment/guest.jsp"/>
        <% } else { %>
            <jsp:include page="fragment/tasklist.jsp"/>
        <% } %>
        
        <script>
            function showMessage(txt) {
                const div = document.createElement('div');
                div.className = "msg-block";
                div.innerHTML = txt;
                setTimeout(()=>{ document.body.appendChild(div); }, 100);
                for(let i = 0; i < 10; ++i)
                    setTimeout(()=>{ div.style.opacity = "." + (9-i); }, 1100 + i*100);
                setTimeout(()=>{ document.body.removeChild(div); }, 2100);
            }
        </script>
        
        <%
            // Проверка сообщений в сессии
            String taskControlMessage = (String) // Сообщения по управлению задачами
                    session.getAttribute( "TaskControlMessage" ) ;
            if( taskControlMessage != null ) {
                session.removeAttribute( "TaskControlMessage" ) ; 
                %><script>showMessage( '<%= taskControlMessage %>' ) ; </script><%
                
            }
        %>
    </main>
    </body>
</html>
