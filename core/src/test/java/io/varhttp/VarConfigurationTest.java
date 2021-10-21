package io.varhttp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class VarConfigurationTest {
    @InjectMocks
    VarConfiguration varConfiguration;

    @Mock
    ControllerMapper controllerMapper;

    @Test
    public void addControllerPackage() {
        varConfiguration.addControllerPackage(getClass().getPackage());
        varConfiguration.applyMappings();

        verify(controllerMapper).map(any(), eq( "io.varhttp."));
    }
}