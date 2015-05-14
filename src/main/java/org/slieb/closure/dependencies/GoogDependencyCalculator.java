package org.slieb.closure.dependencies;

import org.slieb.dependencies.DependenciesHelper;
import org.slieb.dependencies.DependencyCalculator;
import org.slieb.dependencies.DependencyParser;
import slieb.kute.api.Resource;


public class GoogDependencyCalculator extends DependencyCalculator<Resource.Readable, GoogDependencyNode> {

    public static final GoogDependencyHelper HELPER = new GoogDependencyHelper();

    public static final GoogDependencyParser PARSER = new GoogDependencyParser();

    public GoogDependencyCalculator(Iterable<Resource.Readable> resources, DependencyParser<Resource.Readable, GoogDependencyNode> parser, DependenciesHelper<GoogDependencyNode> helper) {
        super(resources, parser, helper);
    }

    public GoogDependencyCalculator(Iterable<Resource.Readable> resources, DependencyParser<Resource.Readable, GoogDependencyNode> parser) {
        this(resources, parser, HELPER);
    }

    public GoogDependencyCalculator(Iterable<Resource.Readable> resources) {
        this(resources, PARSER, HELPER);
    }

}
