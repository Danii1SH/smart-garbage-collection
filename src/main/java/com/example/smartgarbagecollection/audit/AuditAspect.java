package com.example.smartgarbagecollection.audit;

import com.example.smartgarbagecollection.dto.RegisterRequest;
import com.example.smartgarbagecollection.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogService auditLogService;
    private final AuditProperties auditProperties;

    @AfterReturning("@annotation(com.example.smartgarbagecollection.audit.Audit)")
    public void logAudit(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Audit auditAnnotation = method.getAnnotation(Audit.class);

        String action = auditAnnotation.action();
        String fallbackTemplate = auditAnnotation.detailTemplate();
        String template = auditProperties.getTemplates().getOrDefault(action, fallbackTemplate);

        Map<String, Object> context = new HashMap<>();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();

        for (int i = 0; i < args.length; i++) {
            context.put(paramNames[i], args[i]);
        }

        String detail = resolveTemplate(template, context);
        auditLogService.log(action, detail);
    }

    private String resolveTemplate(String template, Map<String, Object> context) {
        if (template == null) return "";

        Matcher matcher = Pattern.compile("\\$\\{([^}]+)}").matcher(template);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String expression = matcher.group(1);
            Object value = resolveValue(expression, context);
            matcher.appendReplacement(sb, value != null ? Matcher.quoteReplacement(value.toString()) : "null");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private Object resolveValue(String path, Map<String, Object> context) {
        String[] parts = path.split("\\.");
        Object current = context.get(parts[0]);

        for (int i = 1; i < parts.length && current != null; i++) {
            try {
                Field field = current.getClass().getDeclaredField(parts[i]);
                field.setAccessible(true);
                current = field.get(current);
            } catch (Exception e) {
                return null;
            }
        }
        return current;
    }
}


