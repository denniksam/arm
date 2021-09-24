package filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Фильтр проверяет есть ли в сессии сохраненный id пользователя и
 * восстанавливает все данные о нем из БД
 */
public class CheckAuthFilter implements Filter {

    private FilterConfig filterConfig ;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig ;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        
        // Проверить наличие в сессии атрибута authUserId
        // если есть, запросить из БД данные и сохранить "в общедоступном месте"
        HttpSession session = ( (HttpServletRequest) request ).getSession() ;
        
        String userId   = (String) session.getAttribute( "authUserId" ) ;                
        String authTime = (String) session.getAttribute( "authUserTime" ) ;            
        if( userId != null ) {
            services.Auth.setUser(
                    services.Db.getUserById( userId )
            );
            if( authTime != null ) {
                services.Auth.setLoginTime( Long.parseLong( authTime ) );
            }
            // Проверяем продолжительность сессии (авторизации)
            if( services.Auth.getLoginDuration() > 3000000 /* 3000 сек = 50 мин */ ) {
                // сбрасываем авторизацию - переходим на /auth (как и кнопка "выход")
                request.getRequestDispatcher( "/auth" )
                       .forward( request, response ) ;
                return ;
            }
        } else {
            services.Auth.setUser( null ) ;
        }
        chain.doFilter( request, response ) ;
    }

    @Override
    public void destroy() {
        this.filterConfig = null ;
    }
    
}
