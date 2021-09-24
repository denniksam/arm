<%-- 
    Document   : maketask
    Фрагмент для постановки задачи
--%>

<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String domain = request.getContextPath() ;
    ArrayList<orm.User> users = services.Db.getUsersList() ;
    
    // Извлекаем из сессии сохраненные данные формы (если есть)
    String managerId = (String) session.getAttribute( "taskManagerId" ) ;
    session.removeAttribute( "taskManagerId" ) ;
    
    String taskTitle = (String) session.getAttribute( "taskTaskTitle" ) ;
    if( taskTitle == null ) taskTitle = "" ;
    session.removeAttribute( "taskTaskTitle" ) ;
    
    String taskDescription = (String) session.getAttribute( "taskTaskDescription" ) ;
    if( taskDescription == null ) taskDescription = "" ;
    session.removeAttribute( "taskTaskDescription" ) ;
    
    String taskDeadline = (String) session.getAttribute( "taskTaskDeadline" ) ;
    if( taskDeadline == null ) taskDeadline = "" ;
    session.removeAttribute( "taskTaskDeadline" ) ;
%>
<div class="add-task">
    <h3>Поставить задачу</h3>
    <form action="<%= domain %>/task" method="post">
        Исполнитель: 
        <select name="managerId">
            <% if( users != null ) for( orm.User user : users ) { %>
            <option value="<%= user.getId() %>" 
                    <%= ( user.getId().equals( managerId ) ) ? "selected" : "" %> >
                <%= user.getName() %>
            </option>
            <% } %>
        </select>
        <br/>
        Название: <input type="text" name="taskTitle" value="<%= taskTitle %>" />
        <br/>
        Описание: <textarea name="taskDescription" ><%= taskDescription %></textarea>
        <br/>
        Срок: <input type="date" name="taskDeadline"  value="<%= taskDeadline %>"  />
        <br/>
        <input type="submit" value="Поставить задачу" />
    </form>
    <%  String message = (String) session.getAttribute( "taskMessage" ) ;
    if( message != null ) {
        session.removeAttribute( "taskMessage" ) ; %>
        <i><%= message %></i>
    <% } %>
</div>
