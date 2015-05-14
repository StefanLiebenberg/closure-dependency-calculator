package org.slieb.closure.dependencies;


import com.google.common.collect.ImmutableSet;
import org.slieb.dependencies.DependencyNode;
import slieb.kute.api.Resource;

public class GoogDependencyNode implements DependencyNode<Resource.Readable> {

    private final Resource.Readable resource;

    private final ImmutableSet<String> provides, requires;

    private final Boolean isBaseFile;

    public GoogDependencyNode(Resource.Readable resource, ImmutableSet<String> provides, ImmutableSet<String> requires, Boolean isBaseFile) {
        this.resource = resource;
        this.provides = provides;
        this.requires = requires;
        this.isBaseFile = isBaseFile;
    }

    @Override
    public Resource.Readable getResource() {
        return resource;
    }

    public Boolean isBaseFile() {
        return isBaseFile;
    }

    @Override
    public ImmutableSet<String> getRequires() {
        return requires;
    }

    @Override
    public ImmutableSet<String> getProvides() {
        return provides;
    }
}
