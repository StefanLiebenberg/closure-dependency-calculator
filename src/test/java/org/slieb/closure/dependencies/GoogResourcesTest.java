package org.slieb.closure.dependencies;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.SourceFile;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.Resources;

import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static slieb.kute.resources.Resources.providerOf;

public class GoogResourcesTest {

    Resource.Readable readableA, readableB, readableC;

    ResourceProvider<Resource.Readable> readables;

    @Before
    public void setup() {
        readableA = Resources.stringResource("/path/a", "goog.base = function () {};");
        readableB = Resources.stringResource("/path/b", "goog.provide('b');");
        readableC = Resources.stringResource("/path/c", "goog.provide('c');");
        readables = providerOf(readableA, readableB, readableC);
    }

    @Test
    public void testGetCalculator() throws Exception {
        GoogDependencyCalculator calculator = GoogResources.getCalculator(readables);
        assertNotNull(calculator);
        Assert.assertEquals(ImmutableList.of(readableA, readableC),
                calculator.getResourcesFor(readableC));
        Assert.assertEquals(ImmutableList.of(readableA, readableB),
                calculator.getResourcesFor(readableB));
    }


    @Test(expected = RuntimeException.class)

    public void testCalculatorProducesRuntimeError() throws Exception {

        Resource.Readable readable = new Resource.Readable() {
            @Override
            public Reader getReader() throws IOException {
                throw new IOException("fake io");
            }

            @Override
            public String getPath() {
                return "/path";
            }
        };

        GoogResources.getCalculator(providerOf(readable)).getDependencyNodes();
    }

    @Test
    public void testGetSourceFileFromResource() throws Exception {
        String content = "var x = y;", path = "/path.js";
        Resource.Readable resource = Resources.stringResource(path, content);
        SourceFile sourceFile = GoogResources.getSourceFileFromResource(resource);
        assertNotNull(sourceFile);
        assertEquals(content, sourceFile.getCode());
        assertEquals(path, sourceFile.getOriginalPath());

    }
}