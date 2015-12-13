package org.slieb.closure.dependencies;


import com.google.common.collect.ImmutableSet;
import org.slieb.dependencies.DependencyNode;
import slieb.kute.api.Resource;

import java.io.Serializable;
import java.util.Objects;

public class GoogDependencyNode implements DependencyNode<Resource.Readable>, Serializable {

    private final Resource.Readable resource;

    private final ImmutableSet<String> provides, requires;

    private final Boolean isBase;

    public GoogDependencyNode(Resource.Readable resource,
                              ImmutableSet<String> provides,
                              ImmutableSet<String> requires,
                              Boolean isBase) {
        this.resource = resource;
        this.provides = provides;
        this.requires = requires;
        this.isBase = isBase;
    }


    @Override
    public Resource.Readable getResource() {
        return resource;
    }


    public Boolean isBaseFile() {
        return isBase;
    }

    @Override
    public ImmutableSet<String> getRequires() {
        return requires;
    }

    @Override
    public ImmutableSet<String> getProvides() {
        return provides;
    }

    @Override
    public String toString() {
        return "GoogDependencyNode{" +
                "resource=" + resource +
                ", provides=" + provides +
                ", requires=" + requires +
                ", isBase=" + isBase +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GoogDependencyNode)) return false;
        GoogDependencyNode that = (GoogDependencyNode) o;
        return Objects.equals(resource, that.resource) &&
                Objects.equals(provides, that.provides) &&
                Objects.equals(requires, that.requires) &&
                Objects.equals(isBase, that.isBase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource, provides, requires, isBase);
    }
}
