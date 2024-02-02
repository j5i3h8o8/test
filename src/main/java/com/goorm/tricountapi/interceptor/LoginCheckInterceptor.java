package com.goorm.tricountapi.interceptor;

import com.goorm.tricountapi.service.MemberService;
import com.goorm.tricountapi.util.MemberContext;
import com.goorm.tricountapi.util.TricountApiConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 구현 2번
 */
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Autowired
    private MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Cookie[] cookies = request.getCookies();

        if(!this.containsUserCookie(cookies)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        // MemberContext에 값을 set 해준다.
        for(Cookie cookie : cookies) {
            if(TricountApiConst.LOGIN_MEMBER_COOKIE.equals(cookie.getName())) {
                try {
                    // cookie에서 아이디를 꺼내고, DB에서 이 아이디에 해당하는 Member를 조회해서, set해준다.
                    MemberContext.setCurrentMember(memberService.findMemberById(Long.parseLong(cookie.getValue())));
                    break;
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "MEMBER INFO SET ERROR" + e.getMessage());
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        MemberContext.clear();
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private boolean containsUserCookie(Cookie[] cookies) {
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(TricountApiConst.LOGIN_MEMBER_COOKIE.equals(cookie.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
