package org.slieb.closure.dependencies;

import org.junit.Before;
import org.junit.Test;
import slieb.kute.resources.Resources;

import java.io.InputStream;

import static org.junit.Assert.*;


public class GoogDependencyParserTest {

    GoogDependencyParser parser;

    @Before
    public void setup() {
        parser = new GoogDependencyParser();
    }

    @Test
    public void parseBaseFindsNoRequiresOrProvides() throws Throwable {
        try (InputStream inputStream = getClass().getResourceAsStream("/closure-library/closure/goog/base.js")) {
            GoogDependencyNode node = parser.parse(Resources.inputStreamResource("base.js", () -> inputStream));
            assertTrue(node.isBaseFile());
            assertTrue(node.getRequires().isEmpty());
            assertTrue(node.getProvides().isEmpty());
        }
    }

    @Test
    public void parseGoogArrayFindsNoRequiresOrProvides() throws Throwable {
        try (InputStream inputStream = getClass().getResourceAsStream("/closure-library/closure/goog/array/array.js")) {
            GoogDependencyNode node = parser.parse(Resources.inputStreamResource("base.js", () -> inputStream));
            assertFalse(node.isBaseFile());
            assertFalse(node.getRequires().isEmpty());
            assertEquals(1, node.getRequires().size());
            assertTrue(node.getRequires().contains("goog.asserts"));
            assertFalse(node.getProvides().isEmpty());
            assertEquals(2, node.getProvides().size());
            assertTrue(node.getProvides().contains("goog.array"));
            assertTrue(node.getProvides().contains("goog.array.ArrayLike"));
        }
    }


    @Test
    public void parseIfConstructs() throws Throwable {
        String code = "if(COMPILED) { goog.require('x'); } else { goog.require('y'); }";
        GoogDependencyNode node = parser.parse(Resources.stringResource("/inline.js", code));
        assertTrue(node.getProvides().isEmpty());
        assertFalse(node.getRequires().isEmpty());
        assertEquals(2, node.getRequires().size());
        assertTrue(node.getRequires().contains("x"));
        assertTrue(node.getRequires().contains("y"));
    }

    @Test
    public void parseDefineTest() throws Throwable {
        try (InputStream inputStream = getClass().getResourceAsStream("/closure-library/closure/goog/defineclass_test.js")) {
            assertNotNull(inputStream);
            GoogDependencyNode node = parser.parse(Resources.inputStreamResource("defineclass_test.js", () -> inputStream));
            assertTrue(node.getProvides().contains("goog.defineClassTest"));
            assertTrue(node.getRequires().contains("goog.testing.jsunit"));
        }
    }
}
