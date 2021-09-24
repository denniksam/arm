package filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CheckDbFilter implements Filter {

    private FilterConfig filterConfig ;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig ;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding( "UTF-8" ) ;
        response.setCharacterEncoding( "UTF-8" ) ;
        
        if( services.Db.getConnection() == null ) {
            // System.out.println( "CheckDbFilter ----" ) ;
            response.getWriter().print( "No Db connection" ) ;
        } else {
            // System.out.println( "CheckDbFilter ++++" ) ;
            // services.Db.createTaskTable() ;
            // services.Db.makeTasks() ;
            chain.doFilter( request, response ) ;
        }        
    }

    @Override
    public void destroy() {
        this.filterConfig = null ;
    }
    
}
