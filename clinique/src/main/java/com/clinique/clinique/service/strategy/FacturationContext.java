package com.clinique.clinique.service.strategy;


import com.clinique.clinique.model.TypeFacturation;
import org.springframework.stereotype.Component;
import java.util.EnumMap;
import java.util.Map;

@Component
public class FacturationContext {  // PATTERN STRATEGY - contexte
    
    private final Map<TypeFacturation, StrategieFacturation> strategies = new EnumMap<>(TypeFacturation.class);
    
    public FacturationContext() {
        strategies.put(TypeFacturation.CNAM, new CnamStrategy());
        strategies.put(TypeFacturation.PRIVE, new PriveStrategy());
        strategies.put(TypeFacturation.ASSURANCE, new AssuranceStrategy());
    }
    
    public StrategieFacturation getStrategy(TypeFacturation type) {
        StrategieFacturation strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Stratégie non trouvée pour: " + type);
        }
        return strategy;
    }
}