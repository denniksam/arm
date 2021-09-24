package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Сервлет для обслуживания административных операций с пользователями
 * @author samoylenko_d
 */
@WebServlet( "/admin/user" ) 
public class AdminUserServlet extends HttpServlet {

    // Удаление пользователя
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = req.getParameter( "uid" ) ;
        String answer ;
        if( uid == null || uid.length() < 32 ) {
            answer = "Incorrect value for id" ;
        } else {
            if( services.Db.deleteUser( uid ) ) {
                answer = "Delete OK" ;
            } else {
                answer = "Delete Error" ;
            }
        }
        resp.getWriter().print( answer ) ;
    }
    
    // Восстановление пользователя
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = req.getParameter( "uid" ) ;
        String answer ;
        if( uid == null || uid.length() < 32 ) {
            answer = "Incorrect value for id" ;
        } else {
            if( services.Db.restoreUser( uid ) ) {
                answer = "Restore OK" ;
            } else {
                answer = "Restore Error" ;
            }
        }
        resp.getWriter().print( answer ) ;
    }
    
    
}

/*
    1. Переместить все удаленные задачи вниз страницы
    2. Для обычных пользователей реализовать возможность
        скрыть/отобразить удаленные задачи
    3. Для администратора и для автора задачи добавить кнопку
        "восстановить" на каждой удаленной задаче,
        реализовать ее работу
*/