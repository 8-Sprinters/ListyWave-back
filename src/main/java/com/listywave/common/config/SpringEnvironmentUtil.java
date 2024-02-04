package com.listywave.common.config;

import com.listywave.common.constants.EnvironmentConstants;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringEnvironmentUtil {

    private final Environment environment;

    public String getCurrentProfile() {
        return getActiveProfile()
                .filter(
                        profile ->
                                profile.equals(EnvironmentConstants.LOCAL.getValue())
                                        || profile.equals(EnvironmentConstants.DEV.getValue())
                )
                .findFirst()
                .orElse(EnvironmentConstants.LOCAL.getValue());
    }

    public Stream<String> getActiveProfile() {
        return Arrays.stream(environment.getActiveProfiles());
    }
}
