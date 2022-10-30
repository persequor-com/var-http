package io.varhttp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VarConfigurationContextTest {
    @Mock
    private VarServlet servlet;

    @Mock
    private ParameterHandler parameterHandler;

    @Mock
    private BaseVarConfigurationContext baseContext;

    @Mock
    private ControllerExecution notFoundController;

    private Method notFoundControllerMethod;

    private VarConfigurationContext varConfigurationContext;
    @Mock
    private RegisteredWebSockets registeredWebSockets;
    @Mock
    private IWebSocketProvider webSocketProvider;

    @Before
    public void setUp() throws NoSuchMethodException {
        notFoundControllerMethod = VarConfigurationContextTest.class.getMethod("notFound");

        varConfigurationContext = new VarConfigurationContext(servlet, baseContext, parameterHandler, registeredWebSockets, webSocketProvider);
    }

    @Test
    public void setNotFoundController_happyPath() {
        varConfigurationContext.setNotFoundController(VarConfigurationContextTest.class);
        varConfigurationContext.applyMappings();

        assertNotNull(varConfigurationContext.getNotFoundController());
        assertEquals(varConfigurationContext.getNotFoundController().getMethod(), notFoundControllerMethod);
    }

    @Test
    public void setNotFoundController_explicitMethod() {
        varConfigurationContext.setNotFoundController(VarConfigurationContextTest.class, notFoundControllerMethod);
        varConfigurationContext.applyMappings();
        assertNotNull(varConfigurationContext.getNotFoundController());
        assertEquals(varConfigurationContext.getNotFoundController().getMethod(), notFoundControllerMethod);
    }

    @Test
    public void setNotFoundController_definedInBaseController() {
        when(baseContext.getNotFoundController()).thenReturn(notFoundController);

        assertNotNull(varConfigurationContext.getNotFoundController());
        assertEquals(varConfigurationContext.getNotFoundController(), notFoundController);
    }

    @Test
    public void setNotFoundController_null() {
        assertNull(varConfigurationContext.getNotFoundController());
    }

    @NotFoundController
    public void notFound() {

    }
}
