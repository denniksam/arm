package servlets;

import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Сервлет авторизации
 */
public class AuthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // сюда мы попадаем по ссылке-кнопке "Выход"
        // задача: 
        // 1. Удалить из сессии данные об id и времени входа
        req.getSession().removeAttribute( "authUserId" ) ;
        req.getSession().removeAttribute( "authUserTime" ) ;
        // 2. Перенаправить на главную страницу
        resp.sendRedirect( req.getContextPath() + "/" ) ;
    }

    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Принимаем данные формы - authlogin и authpassw
        String login = req.getParameter( "authlogin" ) ;
        String passw = req.getParameter( "authpassw" ) ;
        // Проверяем. Если проверка не проходит, то:
        // закладываем в сессию сообщение и переадрессовываем на главную стр.
        String message = null ;
        String userId  = null ;
        if( login == null || passw == null ) message = "Недостаточно данных" ;
        else {
            if( login.equals( "" ) ) message = "Логин не передан" ;
            else {
                orm.User user = services.Db.authUser( login, passw ) ;
                if( user != null ) {
                    // Авторизация успешна
                    // сохраняем в сессии id пользователя (потом восстановим все)
                    userId = user.getId() ;
                } else {
                    message = "Авторизация отклонена" ;
                }
            }
        }
        // В данном месте - если есть message, то это ошибка; userId - упех
        if( userId != null ) {
            req.getSession().setAttribute( "authUserId", userId ) ;
            // сохраняем данные о времени входа (от него считаем длительность)
            req.getSession().setAttribute( "authUserTime",
                    new Date().getTime() + "" ) ;
        } else {
            req.getSession().setAttribute( "authMessage", message ) ;
        }        
        resp.sendRedirect( req.getContextPath() + "/" ) ;
        // resp.getWriter().print( "View through POST: " + message ) ;
    }
    
}
