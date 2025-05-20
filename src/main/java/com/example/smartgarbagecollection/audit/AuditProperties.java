package com.example.smartgarbagecollection.audit;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "audit")
public class AuditProperties {
    private Map<String, String> templates;
}
