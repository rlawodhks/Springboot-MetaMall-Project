package shop.mtcoding.metamall.core.filter;


import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import shop.mtcoding.metamall.core.exception.Exception400;
import shop.mtcoding.metamall.core.jwt.JwtProvider;
import shop.mtcoding.metamall.core.session.SessionUser;
import shop.mtcoding.metamall.core.util.MyFilterResponseUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

// 인증 체크
public class JwtVerifyFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("디버그 : JwtVerifyFilter 동작함");
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String prefixJwt = req.getHeader(JwtProvider.HEADER);

        if (prefixJwt == null) {
            MyFilterResponseUtil.bedRequest(resp, new Exception400("authorization", "토큰이 전달되지 않았습니다"));
            return;
        }

        String jwt = prefixJwt.replace(JwtProvider.TOKEN_PREFIX, "");
        try {
            DecodedJWT decodedJWT = JwtProvider.verify(jwt);
            Long id = decodedJWT.getClaim("id").asLong();
            String role = decodedJWT.getClaim("role").asString();

            // 세션을 사용하는 이유는 권한처리를 하기 위해서이다.
            HttpSession session = req.getSession();
            SessionUser sessionUser = SessionUser.builder().id(id).role(role).build();
            session.setAttribute("sessionUser", sessionUser);
            chain.doFilter(req, resp);
        } catch (SignatureVerificationException sve) {
            MyFilterResponseUtil.unAuthorized(resp, (sve));
        } catch (TokenExpiredException tee) {
            MyFilterResponseUtil.unAuthorized(resp, (tee));
        }
    }
}
