package com.simplecrm.unit;

import com.simplecrm.Application.Application;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ApplicationTest {

    private MockedStatic<SpringApplication> mockedSpringApplication;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        mockedSpringApplication = Mockito.mockStatic(SpringApplication.class);
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        mockedSpringApplication.close();
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void defaultConstructor_createsInstance() {
        Application application = new Application();
        assertNotNull(application);
    }

    @Test
    void main_successfulLaunch_startsApplication() {
        String[] args = {"--param1=value1", "--param2=value2"};
        
        Application.main(args);

        mockedSpringApplication.verify(() -> SpringApplication.run(Application.class, args));
        assertTrue(outContent.toString().isEmpty(), "System.out should be empty on successful launch");
        assertTrue(errContent.toString().isEmpty(), "System.err should be empty on successful launch");
    }

    @Test
    void main_exceptionThrown_printsStackTrace() {
        String[] args = {};
        RuntimeException testException = new RuntimeException("Test exception");
        mockedSpringApplication.when(() -> SpringApplication.run(Application.class, args))
                .thenThrow(testException);

        Application.main(args);

        mockedSpringApplication.verify(() -> SpringApplication.run(Application.class, args));
        assertFalse(errContent.toString().isEmpty(), "System.err should contain stack trace");
        assertTrue(errContent.toString().contains("Test exception"), "Stack trace should contain exception message");
        assertTrue(outContent.toString().isEmpty(), "System.out should be empty when exception occurs");
    }

    @Test
    void main_emptyArgsArray_startsApplication() {
        String[] args = {};
        
        Application.main(args);

        mockedSpringApplication.verify(() -> SpringApplication.run(Application.class, args));
        assertTrue(outContent.toString().isEmpty(), "System.out should be empty on successful launch");
        assertTrue(errContent.toString().isEmpty(), "System.err should be empty on successful launch");
    }

    @Test
    void main_nullArgsArray_startsApplication() {
        String[] args = null;

        Application.main(args);

        mockedSpringApplication.verify(() -> SpringApplication.run(Application.class, args));
        assertTrue(outContent.toString().isEmpty(), "System.out should be empty on successful launch");
        assertTrue(errContent.toString().isEmpty(), "System.err should be empty on successful launch");
    }

    @Test
    void main_throwableThrown_printsStackTrace() {
        String[] args = {};
        Error testError = new Error("Test error");
        mockedSpringApplication.when(() -> SpringApplication.run(Application.class, args))
                .thenThrow(testError);
        
        Application.main(args);
        
        mockedSpringApplication.verify(() -> SpringApplication.run(Application.class, args));
        assertFalse(errContent.toString().isEmpty(), "System.err should contain stack trace");
        assertTrue(errContent.toString().contains("Test error"), "Stack trace should contain error message");
        assertTrue(outContent.toString().isEmpty(), "System.out should be empty when error occurs");
    }
}