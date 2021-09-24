<%-- 
    Document   : tasklist
    Фрагмент, отображающий список задач
--%>

<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% 
    String domain = request.getContextPath() ;
    orm.User user = services.Auth.getUser() ;
    // Переданные данные для фильтра
    // По исполнителю
    boolean showChief   = ( request.getParameter( "filterChief" ) != null ) ;
    boolean showManager = ( request.getParameter( "filterManager" ) != null ) ;
    // Если все фильтры отключены - показываем все
    if( ! ( showChief || showManager ) ) 
        showChief = showManager = true ;

    // По срокам
    boolean showNormal = ( request.getParameter( "filterNormal" ) != null ) ;
    boolean showHot    = ( request.getParameter( "filterHot" ) != null ) ;
    boolean showFail   = ( request.getParameter( "filterFail" ) != null ) ;
    // Если все фильтры отключены - показываем все
    if( ! ( showNormal || showHot || showFail ) ) 
        showNormal = showHot = showFail = true ;
    ArrayList<orm.Task> tasks = services.Db.getTaksLits( user ) ;
%>
<div class="task-container">
    <% if( tasks == null ) { %>
    Нет задач
    <% } else 
        for( orm.Task task : tasks ) {
        if( task.getChiefId().equals( user.getId() ) && showChief
         || task.getManagerId().equals( user.getId() ) && showManager ) 
        {
            int days = task.daysLeft() ;
            if( showNormal && ( days > 3 )
             || showHot    && ( 0 <= days && days <= 3 )
             || showFail   && ( days < 0 ) )
            {    
                String dateClass;
                if( days > 3 ) dateClass = "date-normal" ;
                else if( days >= 0 ) dateClass = "date-hot" ;
                else dateClass = "date-fail" ;
                // является ли данный пользователем постановщиком задачи
                boolean isChief = task.getChiefId().equals( user.getId() ) ;
                // стиль в зависимости от статуса задачи
                String statusClass = "" ;
                int status = task.getStatus() ;
                if( status == -1 ) statusClass = "task-del" ;
                if( status == 0  ) statusClass = "task-work" ;
                if( status == 1  ) statusClass = "task-accept" ;
                if( status == 2  ) statusClass = "task-reject" ;
    %>
    <div class="task-item <%= statusClass %> <%= ( isChief ) ? "task-chief" : "task-manager" %>" 
             title="Задача № <%= task.getId() %>">
            <b><%= task.getTitle() %></b>
            <div style="clear: left"></div>
            <% if( isChief ) { /* "Инструмент" редактирования описания задачи*/ %>
               <div class="description-edit" onclick="descriptionEditClick('<%= task.getId() %>')"></div>
            <% } %>
            <p><%= task.getDescription() %></p>
            <img src="<%= domain %>/img/calendar.png" 
                 <% if( isChief ) { %> onclick="calendarClick('<%= task.getId() %>')" <% } %>
                 />
            <i class="<%= dateClass %>"><%= task.getFinishDate() %></i>
            <tt><%= task.daysLeft() %>, <%= task.getStatus() %></tt>
            <% if( isChief ) { /*Блок управления задачей - только для постановщика*/ %>
                <div class="task-controls">
                    <input type="button" value="Удалить" onclick="location='<%= domain %>/taskcontrol/<%= task.getId() %>?action=del'" />
                    <input type="button" value="Отклонить" onclick="location='<%= domain %>/taskcontrol/<%= task.getId() %>?action=reject'" />
                    <input type="button" value="Принять" onclick="location='<%= domain %>/taskcontrol/<%= task.getId() %>?action=accept'" />
                </div>
            <% } %>
        </div>
    <% } } } %>
</div>
    <script src="<%= domain %>/js/tasklist.js"></script>

<!-- Блок-фильтр, выбор отображаемых задач -->
<div class="task-filter">
    <form>
        <label>
            <input type="checkbox" name="filterChief" <%= (showChief) ? "checked" : "" %> />
            Поставленные
        </label>
        <label>
            <input type="checkbox" name="filterManager" <%= (showManager) ? "checked" : "" %> />
            Исполняемые
        </label>
         
        <hr/>   
            
        <label>
            <input type="checkbox" name="filterFail" <%= (showFail) ? "checked" : "" %> />
            Просроченные
        </label>
        <label>
            <input type="checkbox" name="filterHot" <%= (showHot) ? "checked" : "" %> />
            Срочные
        </label>
        <label>
            <input type="checkbox" name="filterNormal" <%= (showNormal) ? "checked" : "" %> />
            Несрочные
        </label>
            
        <hr/>
            
        <input type="submit" value="Применить"/>
    </form>
</div>
            
<jsp:include page="/fragment/maketask.jsp" />

<%-- Блок управления пользователями (для администратора) --%>
<% if( services.Auth.isUserAdmin() ) { %>
<jsp:include page="/fragment/adminuser.jsp" />
<% } %>
