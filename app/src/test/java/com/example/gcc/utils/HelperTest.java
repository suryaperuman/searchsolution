package com.example.gcc.utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class HelperTest {
    private String username;
    private String password;
    private String expectedResult;
    private Helper helper;

    public HelperTest(String username, String password, String expectedResult) {
        super();
        this.username = username;
        this.password = password;
        this.expectedResult = expectedResult;
    }

    @Before
    public void initialize() {
        helper = new Helper();
    }

    @Parameterized.Parameters
    public static Collection input() {
        Object[][] testCases = new Object[3][3];
        testCases[0] = new Object[]{"Te", "Test@1", "Username must be at least 4 characters long"};
        testCases[1] = new Object[]{"Test$", "Test@1", "Username can only include letters, numbers, periods, & underscores"};
        testCases[2] = new Object[]{"Test", "Test", "Make sure password meets all requirements"};
        return Arrays.asList(testCases);
    }

    @Test
    public void testHelper() {
        System.out.println(expectedResult);
        assertEquals(expectedResult, helper.validateFields(username, password));
    }
}