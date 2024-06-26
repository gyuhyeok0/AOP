package com.ohgiraffers.section01.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Map;

@Component
@Aspect
public class LoggingAspect {

    /* 필기.
     *  @Pointcut : 여러 조인 포인트(실제 타겟)를 매치하기 위해 지정한 표현식
     *  execution([수식어] 리턴타입 [클래스 이름].메소드(파라미터))
     *  public private default protected 접근제어자
     *  *Service.*(..) : 매개변수가 0개 이상인 모든 메소드 지칭
     *  *Service.*(*) : 매개변수가 1개인 모든 메소드 지칭
     *  *Service.*(*, ..) : 매개변수가 2개인 모든 메소드 지칭
     */


    @Pointcut("execution(* com.ohgiraffers.section01.aop.*Service.*(..))")
    public void logPointcut() {}

    /* 필기.
    *   advice 의 종류
    *   @Before
    *   @After
    *   @AfterReturning
    *   @AfterThrowing
    *   @Around
    *  */

    /* 필기.
    *   JoinPoint : 포인트 컷으로 패치한 실행 지점(타겟)
    *   이렇게 매치된 조인 포인트에서 해야 할 일을 지정하는 것이 Advice 라고 한다.
    *   매개변수로 전달한 JoinPoint 는 현재 조인 포인트의 메소드명, 인수값 등의
    *   정보들을 가져올 수 있다.
    *  */
    @Before("LoggingAspect.logPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Before joinPoint.getTarget() : " + joinPoint.getTarget());
        System.out.println("Before joinPoint.getSignature() : " + joinPoint.getSignature());
        if(joinPoint.getArgs().length > 0) {
            System.out.println("Before joinPoint.getArgs()[0] : " + joinPoint.getArgs()[0]);
        }
    }

    /* 필기. After Advice 는 예외 발생여부와 관계없이 공통내용을 수행한다.*/
    @After("logPointcut()")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("After joinPoint.getTarget() : " + joinPoint.getTarget());
        System.out.println("After joinPoint.getSignature() : " + joinPoint.getSignature());
        if(joinPoint.getArgs().length > 0) {
            System.out.println("After joinPoint.getArgs()[0] : " + joinPoint.getArgs()[0]);
        }
    }

    /*필기.
        returning 속성은 리턴값으로 받아 올 오브젝트의 매개변수 이름과 동일해야 한다.
        또한 JoinPoint 는 반드시 첫 번째 매개변수로 선언을 해야 한다.
     */

    @AfterReturning(pointcut = "logPointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("After Returning result : " + result);
        /* result 라는 공간에 반환값이 들어 있다. 따라서 결과값을 변경 할 수도 있다. */
        if(result != null && result instanceof Map) {
            ((Map<Long, MemberDTO>) result).put(50L, new MemberDTO(50L, "반형해보기"));
        }
    }

    /* 필기. 대상 메소드(Joinpoint) 를 실행하는 도중 예외가 발생하는 경우 공통의 기능 수행
        throwing 매개변수의 이름과 동일해야 한다.
     */
    @AfterThrowing(pointcut = "logPointcut()", throwing = "exception")
    public void logAfterThrowing(Throwable exception) {
        System.out.println("After Throwing exception : " + exception);
        
    }

    /*필기. Around Adive
        Around 는 JoinPoint 의 앞 과 뒤를 모두 장악한다.
        원본 JoinPoint(타겟메소드)를 언제 실행 할지, 실행하지 않을 지
        계속 실행 할지 결정한다.
        Around Advice 는 ProceedingJoinPoint 를 매개변수로 받는다.
     */
    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        System.out.println("Around Before : " + joinPoint.getSignature().getName());


        Object result = joinPoint.proceed();     // 굉장히 강한

        System.out.println("Around After : " + joinPoint.getSignature().getName());
        stopWatch.stop();
        System.out.println("메소드 실행 시간" + stopWatch.getTotalTimeNanos() + "(ns)");
        return result;
    }

    

}
